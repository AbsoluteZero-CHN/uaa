package cn.noload.uaa.domain;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public abstract class Transaction {

    @NotNull
    @Column(nullable = false, name = "key")
    protected String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
