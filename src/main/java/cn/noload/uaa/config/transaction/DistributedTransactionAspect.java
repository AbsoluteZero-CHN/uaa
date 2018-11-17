package cn.noload.uaa.config.transaction;


import cn.noload.uaa.domain.MessageConfirmation;
import cn.noload.uaa.service.MessageConfirmationSevice;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Aspect
@Component
public class DistributedTransactionAspect {

    private static final Logger logger = LoggerFactory.getLogger(DistributedTransactionAspect.class);

    private final ThreadLocal<List<MessageConfirmation>> context;
    private final Set<String> submittedTransactionIdSet;
    private final MessageConfirmationSevice messageConfirmationSevice;

    public DistributedTransactionAspect(
        @Qualifier("transactionKey") ThreadLocal<List<MessageConfirmation>> context,
        @Qualifier("submittedTransactionIdSet") Set<String> submittedTransactionIdSet,
        MessageConfirmationSevice messageConfirmationSevice) {
        this.context = context;
        this.submittedTransactionIdSet = submittedTransactionIdSet;
        this.messageConfirmationSevice = messageConfirmationSevice;
    }

    @Pointcut("@annotation(cn.noload.uaa.config.transaction.DistributedTransaction)")
    public void pointCut(){}

    @Before(value = "pointCut()&&@annotation(distributedTransaction)")
    public void doBefore(JoinPoint joinPoint, DistributedTransaction distributedTransaction) {
        context.set(new ArrayList());
    }

    @After(value = "pointCut()&&@annotation(distributedTransaction)")
    public void doAfter(JoinPoint joinPoint, DistributedTransaction distributedTransaction) throws Exception {
        if(context.get().size() == 0) {
            // 在本地事务提交前, 校验一次是否发送了消息, 如果未获取到事务消息 id, 则抛出异常回滚
            throw new Exception("未执行事务RPC, 或事务消息发送失败");
        }
    }

    @AfterReturning(value = "pointCut()&&@annotation(distributedTransaction)")
    public void doAfterReturning(JoinPoint joinPoint, DistributedTransaction distributedTransaction) {
        // 当执行到此处, 说明本地事务执行没有任何问题, 修改数据回查表数据状态为 `已提交`
        context.get().stream().map(MessageConfirmation::getMsgId).forEach(msgId -> {
            // 由于之前是异步执行, 所以此处 CAS 执行避免存在数据未提交的情况
            while (true) {
                Optional<MessageConfirmation> messageConfirmation = messageConfirmationSevice.findByMsgId(msgId);
                if(messageConfirmation.isPresent()) {
                    messageConfirmationSevice.updateStatus(msgId);
                   break;
                }
            }
            // 为了提高消息回查吞吐率, 保存一份副本在 bean 容器中
            submittedTransactionIdSet.add(msgId);
        });
    }

    @AfterThrowing(value = "pointCut()&&@annotation(distributedTransaction)", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, DistributedTransaction distributedTransaction, Exception exception) throws Exception {
        if(exception != null) {
            // 当执行到此处, 说明本地事务执行异常
            logger.error("本地事务执行异常: {}, 异常信息: {}", context.get(), exception);
            throw exception;
        }
    }
}
