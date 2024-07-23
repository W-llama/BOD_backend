package com.bod.bod.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

//    예외(상태코드, "메세지"),  추가하여 작성하세요

    ACCESS_DENIED(403, "접근 권한이 없습니다."),
    ALREADY_NICKNAME(401, "해당 닉네임은 존재하는 닉네임입니다."),
    NOT_FOUND_USERNAME(401, "해당 아이디는 존재하지 않습니다."),
    FAIL(500, "실패했습니다."),
    DUPLICATE_NICKNAME(409, "해당 닉네임은 이미 사용 중입니다."),
    DUPLICATE_EMAIL(400, "중복된 이메일입니다."),
    INVALID_ADMIN_TOKEN(400, "잘못된 관리자 토큰입니다."),
    INVALID_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(400, "토큰이 일치하지 않습니다." ),

    NOT_FOUND_CHALLENGE(400, "현재 존재하는 챌린지가 없습니다.");


    private final int status;
    private final String msg;

}

