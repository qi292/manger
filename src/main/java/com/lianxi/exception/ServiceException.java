package com.lianxi.exception;

import lombok.Data;

@Data
public class ServiceException extends RuntimeException{
    private Integer code;
    private String msg;

    public ServiceException(Integer code,String msg){
        this.msg=msg;
        this.code=code;
    }
}
