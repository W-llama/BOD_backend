package com.bod.bod.global.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "CustomException:: ")
@Getter
public class GlobalException extends RuntimeException{
    private final ErrorCode errorCode;

    public GlobalException(ErrorCode errorCode){
        super(errorCode.getMsg());
        this.errorCode = errorCode;
        log.info("ExceptionMethod: {}", getExceptionMethod());
        log.info("ErrorCode: {}", errorCode.getMsg());
    }
    public String getExceptionMethod(){
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        return className + "." +methodName;
    }
}
