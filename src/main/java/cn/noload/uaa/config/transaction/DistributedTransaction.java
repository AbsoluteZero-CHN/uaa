package cn.noload.uaa.config.transaction;


import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedTransaction {

    /**
     * 定义分布式事务的种类
     * */
    DistributedTransaction.Busness value();

    public enum Busness {
        TEST
    }
}
