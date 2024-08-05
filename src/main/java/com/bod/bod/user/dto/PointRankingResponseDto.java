package com.bod.bod.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointRankingResponseDto {

  private String nickName;
  private long point;

  public PointRankingResponseDto(String nickName, double score) {
    this.nickName = nickName;
    this.point = (long) score;
  }
}
