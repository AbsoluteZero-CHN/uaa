package cn.noload.uaa.config.transaction;


import cn.noload.uaa.repository.MessageConfirmationRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Aspect
@Component
public class DistributedTransactionAspect {

    private final ThreadLocal<String> context;
    private final MessageConfirmationRepository messageConfirmationRepository;

    public DistributedTransactionAspect(
        @Qualifier("transactionKey") ThreadLocal<String> context,
        MessageConfirmationRepository messageConfirmationRepository) {
        this.context = context;
        this.messageConfirmationRepository = messageConfirmationRepository;
    }

    @Pointcut("@annotation(cn.noload.uaa.config.transaction.DistributedTransaction)")
    public void pointCut(){}

    @Before(value = "pointCut()&&@annotation(distributedTransaction)")
    public void doBefore(JoinPoint joinPoint, DistributedTransaction distributedTransaction) {
        DistributedTransaction.Busness busness = distributedTransaction.value();
        System.out.println(busness);
        // 设置回查 key
        this.context.set(UUID.randomUUID().toString());
    }
}
