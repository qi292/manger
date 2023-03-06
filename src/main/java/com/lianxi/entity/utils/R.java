package com.lianxi.entity.utils;

import lombok.Data;
import org.apache.ibatis.annotations.ConstructorArgs;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应类型处理
 */
@Data

public class R {
    private Integer stateCode;
    private String message;
    private Boolean success;
    private Map<String, Object> data = new HashMap<>();

    public static R ok() {
        R r = new R();
        r.setStateCode(200);
        r.setSuccess(true);
        r.setMessage("ok");
        return r;
    }

    public static R error(int stateCode, String message) {
        R r = new R();
        r.setStateCode(stateCode);
        r.setSuccess(false);
        r.setMessage(message);
        return r;
    }

    public R data(String key, Object val) {
        this.getData().put(key, val);
        return this;
    }
}
