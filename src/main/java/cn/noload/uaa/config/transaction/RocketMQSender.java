/*
package cn.noload.uaa.config.transaction;


import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.MessageDecoder;
import org.apache.rocketmq.common.message.MessageId;
import org.apache.rocketmq.common.protocol.header.EndTransactionRequestHeader;
import org.apache.rocketmq.common.sysflag.MessageSysFlag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;

@Component
public class RocketMQSender {
    private final TransactionMQProducer transactionMQProducer;

    public RocketMQSender(
        @Qualifier("transaction") TransactionMQProducer transactionMQProducer) {
        this.transactionMQProducer = transactionMQProducer;
    }

    public void send(SendResult sendResult, LocalTransactionState state) throws UnknownHostException {
        MessageId id = sendResult.getOffsetMsgId() == null ? MessageDecoder.decodeMessageId(sendResult.getMsgId()) : MessageDecoder.decodeMessageId(sendResult.getOffsetMsgId());
        String transactionId = sendResult.getTransactionId();
        EndTransactionRequestHeader requestHeader = new EndTransactionRequestHeader();
        requestHeader.setTransactionId(transactionId);
        requestHeader.setCommitLogOffset(id.getOffset());
        // 本地事务因为是在切面中执行, 所以只存在两种状态: 提交和回滚
        if(state == LocalTransactionState.COMMIT_MESSAGE) {
            requestHeader.setCommitOrRollback(MessageSysFlag.TRANSACTION_COMMIT_TYPE);
        } else {
            requestHeader.setCommitOrRollback(MessageSysFlag.TRANSACTION_ROLLBACK_TYPE);
        }
        requestHeader.setProducerGroup(transactionMQProducer.getProducerGroup());
        requestHeader.setTranStateTableOffset(sendResult.getQueueOffset());
        requestHeader.setMsgId(sendResult.getMsgId());
        transactionMQProducer.getCl
    }
}
*/
