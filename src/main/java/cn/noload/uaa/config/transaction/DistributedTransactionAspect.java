package cn.noload.uaa.config.transaction;


import cn.noload.uaa.domain.MessageConfirmation;
import cn.noload.uaa.repository.MessageConfirmationRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;


@Aspect
@Component
public class DistributedTransactionAspect {

    private final ThreadLocal<MessageConfirmation> context;
    private final MessageConfirmationRepository messageConfirmationRepository;

    public DistributedTransactionAspect(
        @Qualifier("transactionKey") ThreadLocal<MessageConfirmation> context,
        MessageConfirmationRepository messageConfirmationRepository) {
        this.context = context;
        this.messageConfirmationRepository = messageConfirmationRepository;
    }

    @Pointcut("@annotation(cn.noload.uaa.config.transaction.DistributedTransaction)")
    public void pointCut(){}

    @Before(value = "pointCut()&&@annotation(distributedTransaction)")
    public void doBefore(JoinPoint joinPoint, DistributedTransaction distributedTransaction) {
        DistributedTransaction.Busness busness = distributedTransaction.value();
        // 设置回查 key
        MessageConfirmation messageConfirmation = new MessageConfirmation();
        this.context.set(messageConfirmation);
        messageConfirmation.setId(UUID.randomUUID().toString());
        messageConfirmation.setStatus(0);
        messageConfirmation.setUpdateTime(Instant.now());
        messageConfirmationRepository.save(messageConfirmation);
    }

    @After(value = "pointCut()&&@annotation(distributedTransaction)")
    public void doAfter(JoinPoint joinPoint, DistributedTransaction distributedTransaction) {
        MessageConfirmation messageConfirmation = this.context.get();
        messageConfirmation.setUpdateTime(Instant.now());
        messageConfirmation.setStatus(1);
        messageConfirmationRepository.save(messageConfirmation);
    }
}
