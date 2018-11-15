package cn.noload.uaa.config.transaction;


import cn.noload.uaa.domain.MessageConfirmation;
import cn.noload.uaa.repository.MessageConfirmationRepository;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;


@Aspect
@Component
public class DistributedTransactionAspect {

    private static final Logger logger = LoggerFactory.getLogger(DistributedTransactionAspect.class);

    private final ThreadLocal<MessageConfirmation> context;
    private final Set<String> submittedTransactionIdSet;
    private final MessageConfirmationRepository messageConfirmationRepository;

    public DistributedTransactionAspect(
        @Qualifier("transactionKey") ThreadLocal<MessageConfirmation> context,
        @Qualifier("submittedTransactionIdSet") Set<String> submittedTransactionIdSet,
        MessageConfirmationRepository messageConfirmationRepository) {
        this.context = context;
        this.messageConfirmationRepository = messageConfirmationRepository;
        this.submittedTransactionIdSet = submittedTransactionIdSet;
    }

    @Pointcut("@annotation(cn.noload.uaa.config.transaction.DistributedTransaction)")
    public void pointCut(){}

    @After(value = "pointCut()&&@annotation(distributedTransaction)")
    public void doAfter(JoinPoint joinPoint, DistributedTransaction distributedTransaction) throws Exception {
        if(context.get() == null || context.get().getId() == null) {
            // 在本地事务提交前, 校验一次是否发送了消息, 如果未获取到事务消息 id, 则回滚
            throw new Exception("未执行事务RPC, 或事务消息发送失败");
        }
    }

    @AfterReturning(value = "pointCut()&&@annotation(distributedTransaction)")
    public void doAfterReturning(JoinPoint joinPoint, DistributedTransaction distributedTransaction) {
        // 当执行到此处, 说明本地事务执行没有任何问题, 修改数据回查表数据状态为 `已提交`
        String msgId = context.get().getMsgId();
        messageConfirmationRepository.updateStatus(msgId);
        // 为了提高消息回查吞吐率, 保存一份副本在 bean 容器中
        submittedTransactionIdSet.add(msgId);
    }

    @AfterThrowing(value = "pointCut()&&@annotation(distributedTransaction)", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, DistributedTransaction distributedTransaction, Exception exception) {
        // 当执行到此处, 说明本地事务执行异常, 修改数据回查表数据状态为 `已回滚`
        MessageConfirmation messageConfirmation = context.get();
        logger.debug("本地事务执行异常: {}", messageConfirmation);
    }
}
