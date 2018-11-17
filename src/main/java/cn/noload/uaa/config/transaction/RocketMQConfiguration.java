package cn.noload.uaa.config.transaction;


import cn.noload.uaa.config.ApplicationProperties;
import cn.noload.uaa.domain.MessageConfirmation;
import cn.noload.uaa.service.MessageConfirmationSevice;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.IntStream;

@Configuration
public class RocketMQConfiguration {

    private final ApplicationProperties applicationProperties;
    private final MessageConfirmationSevice messageConfirmationSevice;

    public RocketMQConfiguration(
        ApplicationProperties applicationProperties,
        MessageConfirmationSevice messageConfirmationSevice) {
        this.applicationProperties = applicationProperties;
        this.messageConfirmationSevice = messageConfirmationSevice;
    }

    @Bean
    @Qualifier("transaction")
    public TransactionMQProducer defaultMQProducer() throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer("transaction");
        producer.setNamesrvAddr(StringUtils.join(applicationProperties.getRocket().getHosts(), ";"));
        producer.setTransactionListener(distributedTransactionListener());
        producer.start();
        return producer;
    }

    @Bean
    @Qualifier("transactionKey")
    public ThreadLocal<List<MessageConfirmation>> transactionKey() {
        return new ThreadLocal();
    }

    @Bean
    @Qualifier("distributedTransactionListener")
    public DistributedTransactionListener distributedTransactionListener() {
        return new DistributedTransactionListener(submittedTransactionIdSet(), messageConfirmationSevice);
    }

    @Bean
    @Qualifier("submittedTransactionIdSet")
    public Set<String> submittedTransactionIdSet() {
        return Collections.synchronizedSet(new HashSet());
    }

    public static void main(String[] args) throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer("transaction");
        producer.setNamesrvAddr("10.0.0.202:9876;10.0.0.203:9876");
        producer.setTransactionListener(new Listener());
        producer.start();
        IntStream.range(0, 10).forEach(
            i -> {
                try {
                    TransactionSendResult result = producer.sendMessageInTransaction(new Message("transaction", "nas", (i + "").getBytes()), null);
                    System.out.println(result.getSendStatus());
                } catch (MQClientException e) {
                    e.printStackTrace();
                }
            }
        );
    }

    static class Listener implements TransactionListener {

        @Override
        public LocalTransactionState executeLocalTransaction(Message message, Object o) {
            if(new Random().nextInt(3) == 2) {
                return LocalTransactionState.UNKNOW;
            }
            return LocalTransactionState.COMMIT_MESSAGE;
        }

        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
            return LocalTransactionState.COMMIT_MESSAGE;
        }
    }
}
