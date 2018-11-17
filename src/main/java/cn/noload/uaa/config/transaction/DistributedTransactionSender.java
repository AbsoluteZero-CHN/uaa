package cn.noload.uaa.config.transaction;


import cn.noload.uaa.domain.MessageConfirmation;
import cn.noload.uaa.service.MessageConfirmationSevice;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;

@Component
public class DistributedTransactionSender {

    private final TransactionMQProducer transactionMQProducer;
    private final Executor executor;
    private final MessageConfirmationSevice messageConfirmationSevice;
    private final ThreadLocal<List<MessageConfirmation>> context;

    public DistributedTransactionSender(
        @Qualifier("transaction") TransactionMQProducer transactionMQProducer,
        @Qualifier("taskExecutor") Executor executor,
        MessageConfirmationSevice messageConfirmationSevice,
        @Qualifier("transactionKey") ThreadLocal<List<MessageConfirmation>> context) {
        this.transactionMQProducer = transactionMQProducer;
        this.executor = executor;
        this.messageConfirmationSevice = messageConfirmationSevice;
        this.context = context;
    }

    public void send(String service, MessageBody body) throws Exception {
        Message message = new Message();
        message.setTopic("transaction");
        // 以调用方的服务名作为标签
        message.setTags(service);
        message.setBody(JSON.toJSONString(body).getBytes());
        TransactionSendResult sendResult = transactionMQProducer.sendMessageInTransaction(message, null);
        if(sendResult.getSendStatus() == SendStatus.SEND_OK) {
            // 保存一份未提交的事务消息数据到数据库
            MessageConfirmation messageConfirmation = new MessageConfirmation();
            messageConfirmation.setStatus(0);
            messageConfirmation.setUpdateTime(Instant.now());
            messageConfirmation.setMsgId(sendResult.getMsgId());
//            executor.execute(() -> messageConfirmationSevice.save(messageConfirmation));
            messageConfirmationSevice.save(messageConfirmation);
            context.get().add(messageConfirmation);
            System.out.println(context.get());
        } else {
            // 抛出异常, 让本地事务回滚
            throw new Exception("发送 prepare 事务消息失败");
        }
    }
}
