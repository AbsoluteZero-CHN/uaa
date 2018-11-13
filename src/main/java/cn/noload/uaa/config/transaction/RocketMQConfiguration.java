package cn.noload.uaa.config.transaction;


import cn.noload.uaa.config.ApplicationProperties;
import cn.noload.uaa.config.rocketmq.RocketMQProperties;
import cn.noload.uaa.domain.MessageConfirmation;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfiguration {


    private final ApplicationProperties applicationProperties;

    public RocketMQConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    @Qualifier("transaction")
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("transaction");
        producer.setNamesrvAddr(StringUtils.join(applicationProperties.getRocket().getHosts(), ";"));
        producer.start();
        return producer;
    }

    @Bean
    @Qualifier("transactionKey")
    public ThreadLocal<MessageConfirmation> threadLocal() {
        return new ThreadLocal();
    }
}
