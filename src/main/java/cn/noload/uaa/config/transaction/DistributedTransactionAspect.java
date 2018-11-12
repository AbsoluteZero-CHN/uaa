package cn.noload.uaa.config.transaction;


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

    public DistributedTransactionAspect(@Qualifier("transactionKey") ThreadLocal<String> context) {
        this.context = context;
    }

    @Pointcut("@annotation(cn.noload.uaa.config.transaction.DistributedTransaction)")
    public void pointCut(){}

    @Before(value = "pointCut()&&@annotation(distributedTransaction)")
    public void doBefore(JoinPoint joinPoint, DistributedTransaction distributedTransaction) {
        String values = distributedTransaction.values();
        System.out.println(values);
        this.context.set(UUID.randomUUID().toString());
    }
}
