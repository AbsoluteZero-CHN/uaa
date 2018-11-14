package cn.noload.uaa.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MQTransactionTest {

    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException {
        TransactionMQProducer producer = new TransactionMQProducer("transaction");
        producer.setNamesrvAddr("10.0.0.202:9876");
        producer.setTransactionListener(new DistributedTransactionListener());
        producer.start();
        producer.sendMessageInTransaction(new Message(
            "transaction",   // 消息主题
            "tx-test", // 消息标签
            getJson().getBytes(RemotingHelper.DEFAULT_CHARSET)  // 消息内容
        ), null);
        System.out.println(Thread.currentThread().getName());
    }

    static String getJson() {
        Map<String, String> msg = new HashMap<>();
        msg.put("msg", "测试消息");
        return JSON.toJSONString(msg);
    }
}


class DistributedTransactionListener implements TransactionListener {

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        System.out.println(Thread.currentThread().getName());
        return LocalTransactionState.ROLLBACK_MESSAGE;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        return null;
    }
}
