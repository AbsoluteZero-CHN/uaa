package cn.noload.uaa.config.transaction;


import cn.noload.uaa.domain.MessageConfirmation;
import cn.noload.uaa.repository.MessageConfirmationRepository;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;


@Aspect
@Component
public class DistributedTransactionAspect {

    private static final Logger logger = LoggerFactory.getLogger(DistributedTransactionAspect.class);

    private final ThreadLocal<MessageConfirmation> context;
    private final ThreadLocal<MessageConfirmation> sendResult;
    private final ThreadLocal<CountDownLatch> countDownLatch;
    private final MessageConfirmationRepository messageConfirmationRepository;
    private final TransactionMQProducer transactionMQProducer;
    private final DistributedTransactionListener distributedTransactionListener;

    public DistributedTransactionAspect(
        @Qualifier("sendResult") ThreadLocal<MessageConfirmation> sendResult,
        @Qualifier("transactionWait") ThreadLocal<CountDownLatch> countDownLatch,
        @Qualifier("transactionKey") ThreadLocal<MessageConfirmation> context,
        @Qualifier("transaction") TransactionMQProducer transactionMQProducer,
        MessageConfirmationRepository messageConfirmationRepository,
        DistributedTransactionListener distributedTransactionListener) {
        this.context = context;
        this.sendResult = sendResult;
        this.countDownLatch = countDownLatch;
        this.messageConfirmationRepository = messageConfirmationRepository;
        this.transactionMQProducer = transactionMQProducer;
        this.distributedTransactionListener = distributedTransactionListener;
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

    }


    @AfterThrowing(value = "pointCut()&&@annotation(distributedTransaction)", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, DistributedTransaction distributedTransaction, Exception exception) {

    }
}
