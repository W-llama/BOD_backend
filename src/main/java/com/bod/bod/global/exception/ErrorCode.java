package com.bod.bod.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Authorization errors
    ACCESS_DENIED(403, "접근 권한이 없습니다."),
    INVALID_ADMIN_TOKEN(401, "잘못된 관리자 토큰입니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(401, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(403, "토큰이 만료되었습니다."),
    USER_ACCESS_DENIED(403, "관리자만 접근 가능합니다."),

    // User-related errors
    ALREADY_NICKNAME(409, "해당 닉네임은 이미 사용 중입니다."),
    ALREADY_USERNAME(409, "해당 아이디는 이미 사용 중입니다."),
    ALREADY_WITHDRAWN(410, "해당 유저는 회원탈퇴한 유저입니다."),
    NOT_FOUND_USERNAME(404, "해당 아이디는 존재하지 않습니다."),
    DUPLICATE_NICKNAME(409, "해당 닉네임은 이미 사용 중입니다."),
    DUPLICATE_EMAIL(409, "중복된 이메일입니다."),
    INVALID_PASSWORD(404, "비밀번호가 일치하지 않습니다."),
    INVALID_USER_STATUS(403, "회원 탈퇴한 유저입니다."),
    INVALID_USERNAME(404, "아이디가 일치하지 않습니다."),
    INVALID_NEW_PASSWORD(400, "최근 3개의 비밀번호와 일치함으로 변경할 수 없습니다."),
    REDIS_CONNECTION_FAILED(500, "레디스와 연결이 되어있지 않습니다."),
    EMPTY_POINT_RANKING_LIST(204, "현재 포인트 적립내역이 없습니다."),

    // Challenge-related errors
    NOT_FOUND_CHALLENGE(404, "해당 챌린지는 존재하지 않습니다."),
    DUPLICATE_CHALLENGE(409, "이미 신청한 챌린지입니다."),
    COMPLETE_CHALLENGE(400, "마감된 챌린지이므로 참여할 수 없습니다."),
    LIMIT_FULL_CHALLENGE(400, "제한인원이 가득 차서 참여할 수 없습니다."),

    // Verification-related errors
    EMPTY_VERIFICATION(404, "해당 챌린지에는 현재까지 인증 신청 내역이 없습니다."),
    ACCESS_DENIED_VERIFICATION(403, "해당 인증에 대한 수정/삭제 권한이 없는 유저입니다."),
    ALREADY_EXISTS_VERIFICATION(409, "이미 챌린지 인증을 하였습니다."),
    NOT_FOUND_VERIFICATION(404, "해당 챌린지 인증은 존재하지 않습니다."),
    ALREADY_EXISTS_APPROVE_VERIFICATION(409, "이미 챌린지 인증 요청을 승인하였습니다."),
    ALREADY_EXISTS_REJECT_VERIFICATION(409, "이미 챌린지 인증 요청을 거절하였습니다."),
    NOT_FOUND_USER_VERIFICATION(404, "유저의 인증신청 내역이 없습니다."),
    DO_NOT_CANCEL_VERIFICATION(400, "인증 승인 상태로 인증취소는 불가능합니다."),
    EMPTY_FILE(400, "파일 업로드가 필요합니다."),

    // UserChallenge-related errors
    NOT_FOUND_USER_CHALLENGE(404, "해당 유저의 챌린지 신청기록을 찾지 못했습니다."),

    // File-related errors
    FILE_UPLOAD_ERROR(500, "파일 크기가 너무 큽니다."),
    FILE_CONVERSION_ERROR(500, "파일 변환에 실패하였습니다."),

    // General error
    FAIL(500, "실패하였습니다.");

    private final int status;
    private final String msg;
}
