package cn.noload.uaa.config.transaction;


import cn.noload.uaa.config.ApplicationProperties;
import cn.noload.uaa.config.rocketmq.RocketMQProperties;
import cn.noload.uaa.domain.MessageConfirmation;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

@Configuration
public class RocketMQConfiguration {


    private final ApplicationProperties applicationProperties;

    public RocketMQConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
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
    public ThreadLocal<MessageConfirmation> transactionKey() {
        return new ThreadLocal();
    }

    @Bean
    @Qualifier("sendResult")
    public ThreadLocal<MessageConfirmation> sendResult() {
        return new ThreadLocal();
    }

    @Bean
    @Qualifier("transactionWait")
    public ThreadLocal<CountDownLatch> transactionWait() {
        return new ThreadLocal();
    }



    @Bean
    @Qualifier("distributedTransactionListener")
    public DistributedTransactionListener distributedTransactionListener() {
        return new DistributedTransactionListener(transactionWait());
    }
}
