package cn.noload.uaa.config.transaction;


import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;

@Component
public class DistributedTransactionSender {
    private final TransactionMQProducer transactionMQProducer;
    private final ThreadLocal<CountDownLatch> countDownLatch;
    private final Executor executor;

    private final static ExecutorService executorService = Executors.newFixedThreadPool(8);

    public DistributedTransactionSender(
        @Qualifier("transaction") TransactionMQProducer transactionMQProducer,
        @Qualifier("transactionWait") ThreadLocal<CountDownLatch> countDownLatch,
        @Qualifier("taskExecutor") Executor executor
    ) {
        this.transactionMQProducer = transactionMQProducer;
        this.countDownLatch = countDownLatch;
        this.executor = executor;
    }

    public void send() throws ExecutionException, InterruptedException, UnsupportedEncodingException {
        CountDownLatch wait = new CountDownLatch(1);
        Message message = new Message(
            "transaction",   // 消息主题
            "tx-test", // 消息标签
            "{\"msg\": \"事务测试消息\"}".getBytes(RemotingHelper.DEFAULT_CHARSET)  // 消息内容
        );
        Future future = executorService.submit(new MQSenderCallable(wait, message));
        future.get();
        // 主线程等候子线程执行
        wait.await();
        // 等候主线程执行的闭锁
        countDownLatch.set(new CountDownLatch(1));
    }

    class MQSenderCallable implements Callable<Boolean> {

        private final CountDownLatch wait;
        private final Message message;
        public MQSenderCallable(
            CountDownLatch wait,
            Message message
        ) {
            this.wait = wait;
            this.message = message;
        }

        @Override
        public Boolean call() throws Exception {
            transactionMQProducer.sendMessageInTransaction(message, null);
            wait.countDown();
            return true;
        }
    }
}
