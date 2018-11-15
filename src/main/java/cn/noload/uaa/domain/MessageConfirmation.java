package cn.noload.uaa.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "t_message_confirmation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MessageConfirmation {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator="uuid2")
    @GenericGenerator(name="uuid2",strategy="uuid2")
    @Column(name="id", unique=true, nullable=false, updatable=false, length = 36)
    private String id;

    @Column(name = "msg_id")
    private String msgId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "update_time")
    private Instant updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "MessageConfirmation{" +
            "id='" + id + '\'' +
            ", msgId='" + msgId + '\'' +
            ", status=" + status +
            ", updateTime=" + updateTime +
            '}';
    }
}
