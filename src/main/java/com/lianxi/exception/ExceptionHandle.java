package com.lianxi.exception;

import com.lianxi.entity.utils.R;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHandle {
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public R handle(ServiceException se){
        return R.error(se.getCode(),se.getMsg());
    }
}
