package cn.noload.uaa.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "t_transaction_task")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TransactionTask {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator="uuid2")
    @GenericGenerator(name="uuid2",strategy="uuid2")
    @Column(name="id", unique=true, nullable=false, updatable=false, length = 36)
    private String id;

    @Column(name = "old_time")
    private Instant oldTime;

    @Column(name = "time")
    private Instant time;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getOldTime() {
        return oldTime;
    }

    public void setOldTime(Instant oldTime) {
        this.oldTime = oldTime;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
