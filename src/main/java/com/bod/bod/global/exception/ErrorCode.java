package com.bod.bod.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

//    예외(상태코드, "메세지"),  추가하여 작성하세요

    ACCESS_DINIED(403, "접근 권한이 없습니다."),
    FAIL(500, "실패했습니다."),







    ;
    private final int status;
    private final String msg;

}

