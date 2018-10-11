package cn.noload.uaa.utils;

import java.util.UUID;

public class StringUtils extends org.apache.commons.lang3.StringUtils {


    /**
     * 生成 36 位主键
     * */
    public String IdGen() {
        return UUID.randomUUID().toString();
    }
}
