package com.bod.bod.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponseDto<T> {

  private int statusCode;
  private String msg;
  private T data;
}