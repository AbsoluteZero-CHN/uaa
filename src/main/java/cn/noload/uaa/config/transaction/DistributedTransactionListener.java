package cn.noload.uaa.config.transaction;

import cn.noload.uaa.domain.MessageConfirmation;
import cn.noload.uaa.service.MessageConfirmationSevice;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class DistributedTransactionListener implements TransactionListener {

    private final Set<String> submittedTransactionIdSet;
    private final MessageConfirmationSevice messageConfirmationSevice;
    private final Map<String, Integer> mqRecheck = new HashMap<>();

    public DistributedTransactionListener(
        @Qualifier("submittedTransactionIdSet") Set<String> submittedTransactionIdSet,
        MessageConfirmationSevice messageConfirmationSevice) {
        this.submittedTransactionIdSet = submittedTransactionIdSet;
        this.messageConfirmationSevice = messageConfirmationSevice;
    }

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        /**
         * PS: 不明白, 为什么 prepare 消息发送完毕后, 确认消息需要同步确认, 我认为完全可以异步确认
         *
         * 因为 RocketMQ 的确认消息是同步执行, 所以此处设计为所有确认消息状态全部为 UNKNOW,
         * 完全通过消息回查机制来 COMMIT / ROLLBACK
         * */
        return LocalTransactionState.UNKNOW;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        String transactionId = messageExt.getTransactionId();
        // 为了提高吞吐效率, 优先从内存中获取已提交的事务
        if(submittedTransactionIdSet.contains(transactionId)) {
            // 此处不会存在并发问题
            submittedTransactionIdSet.remove(transactionId);
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        // 如果内存中没有数据(比如重启服务, 才向数据库中获取
        Optional<MessageConfirmation> messageConfirmation = messageConfirmationSevice.findByMsgId(transactionId);
        if(messageConfirmation.isPresent() && messageConfirmation.get().getStatus() == 1) {
            if(isTimeout(transactionId)) {
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
            return LocalTransactionState.UNKNOW;
        }
        return LocalTransactionState.ROLLBACK_MESSAGE;
    }

    /**
     * 消息回查可能存在本地事务尚未提交的情况
     * 如果次数次数超过阈值, 判定本地事务执行失败
     * */
    private boolean isTimeout(String transactionId) {
        if(!mqRecheck.containsKey(transactionId)) {
            mqRecheck.put(transactionId, 0);
        }
        if(mqRecheck.get(transactionId) >= 3) {
            mqRecheck.remove(transactionId);
            return true;
        }
        mqRecheck.put(transactionId, mqRecheck.get(transactionId) + 1);
        return false;
    }
}
