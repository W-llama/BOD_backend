package com.bod.bod.global.exception;

import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.global.exception.dto.ExceptionResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 접근 거부 되었을 때 발생하는 예외처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponseDto> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        e.printStackTrace();
        ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder()
            .msg(ErrorCode.ACCESS_DENIED.getMsg())
            .path(request.getRequestURI())
            .build();
        return new ResponseEntity<>(exceptionResponseDto, HttpStatus.FORBIDDEN);
    }

    // 기본 예외처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponseDto> handleDefaultException(HttpServletRequest request, Exception e) {
        e.printStackTrace();
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
            .msg(ErrorCode.FAIL.getMsg())
            .path(request.getRequestURI())
            .build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 사용자 정의 예외처리
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ExceptionResponseDto> handleGlobalException(HttpServletRequest request, GlobalException e) {
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
            .msg(e.getErrorCode().getMsg())
            .path(request.getRequestURI())
            .build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    // 메소드 인자 유효성 검사 실패 시 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
