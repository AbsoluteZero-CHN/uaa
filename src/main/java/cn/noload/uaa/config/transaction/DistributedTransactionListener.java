package cn.noload.uaa.config.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.CountDownLatch;


public class DistributedTransactionListener implements TransactionListener {

    private final ThreadLocal<CountDownLatch> threadLocal;

    public DistributedTransactionListener(
        ThreadLocal<CountDownLatch> threadLocal
    ) {
        this.threadLocal = threadLocal;
    }

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        try {
            this.threadLocal.get().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        return null;
    }
}
