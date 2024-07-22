package com.bod.bod.global.exception;

import com.bod.bod.global.exception.dto.ExceptionResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //접근 거부 되었을때 발생하는 예외처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponseDto> AccessDeniedException(HttpServletRequest request, Exception e){
        e.printStackTrace();
        ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder()
            .msg(ErrorCode.ACCESS_DINIED.getMsg())
            .path(request.getRequestURI())
            .build();
        return new ResponseEntity<>(exceptionResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //기본 예외처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponseDto> defaultException(HttpServletRequest request, Exception e){
        e.printStackTrace();
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
            .msg(ErrorCode.FAIL.getMsg())
            .path(request.getRequestURI())
            .build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //사용자 정의 예외처리
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ExceptionResponseDto> handleInvalidPasswordException(HttpServletRequest request, GlobalException e) {
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
            .msg(e.getErrorCode().getMsg())
            .path(request.getRequestURI())
            .build();
        return new ResponseEntity<>(exceptionResponse, HttpStatusCode.valueOf(e.getErrorCode().getStatus()));
    }

    //메소드 인자 유효성 검사실패 시 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(e.getBindingResult().getFieldErrors().get(0).getDefaultMessage() ,HttpStatus.BAD_REQUEST);
    }

}
