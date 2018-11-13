package cn.noload.uaa.config.transaction;


import cn.noload.uaa.domain.MessageConfirmation;
import cn.noload.uaa.repository.MessageConfirmationRepository;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Executor;


@Aspect
@Component
public class DistributedTransactionAspect {

    private static final Logger logger = LoggerFactory.getLogger(DistributedTransactionAspect.class);

    private final ThreadLocal<MessageConfirmation> context;
    private final MessageConfirmationRepository messageConfirmationRepository;
    private final DefaultMQProducer defaultMQProducer;
    private final Executor executor;

    public DistributedTransactionAspect(
        @Qualifier("transactionKey") ThreadLocal<MessageConfirmation> context,
        @Qualifier("transaction") DefaultMQProducer defaultMQProducer,
        MessageConfirmationRepository messageConfirmationRepository,
        @Qualifier("taskExecutor") Executor executor) {
        this.context = context;
        this.messageConfirmationRepository = messageConfirmationRepository;
        this.defaultMQProducer = defaultMQProducer;
        this.executor = executor;
    }

    @Pointcut("@annotation(cn.noload.uaa.config.transaction.DistributedTransaction)")
    public void pointCut(){}

    @Before(value = "pointCut()&&@annotation(distributedTransaction)")
    public void doBefore(JoinPoint joinPoint, DistributedTransaction distributedTransaction) {
        DistributedTransaction.Busness busness = distributedTransaction.value();
        // 设置回查 key
        MessageConfirmation messageConfirmation = new MessageConfirmation();
        messageConfirmation.setStatus(0);
        messageConfirmation.setUpdateTime(Instant.now());
        messageConfirmationRepository.save(messageConfirmation);
        this.context.set(messageConfirmation);
    }

    @AfterReturning(value = "pointCut()&&@annotation(distributedTransaction)")
    public void doAfter(JoinPoint joinPoint, DistributedTransaction distributedTransaction) {
        MessageConfirmation messageConfirmation = this.context.get();
        executor.execute(() -> {
            Message message;
            try {
                message = new Message(
                    "transaction",   // 消息主题
                    distributedTransaction.value().toString(), // 消息标签
                    JSON.toJSONString(messageConfirmation).getBytes(RemotingHelper.DEFAULT_CHARSET)  // 消息内容
                );
                SendResult sendResult = defaultMQProducer.send(message);
                if(sendResult.getSendStatus() != SendStatus.SEND_OK) {
                    logger.error("事务消息发送失败: {}", sendResult.getMessageQueue());
                } else {
                    messageConfirmation.setStatus(1);
                    messageConfirmation.setUpdateTime(Instant.now());
                    messageConfirmationRepository.save(messageConfirmation);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (MQClientException e) {
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            }
        });
    }
}
