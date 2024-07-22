package com.bod.bod.global.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommonResponseDto<T> {
    private String msg;
    private T data;

    @Builder
    public CommonResponseDto(String msg, T data){
        this.msg = msg;
        this.data = data;
    }

}
