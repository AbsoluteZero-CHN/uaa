package cn.noload.uaa.config.transaction;


import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfiguration {


    @Bean()
    @Qualifier("transaction")
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("niwei_producer_group");
        producer.setNamesrvAddr("10.0.0.202:9876");
        producer.start();
        return producer;
    }

    @Bean
    @Qualifier("transactionKey")
    public ThreadLocal threadLocal() {
        return new ThreadLocal();
    }
}
