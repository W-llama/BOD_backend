package com.bod.bod.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PointRankingResponseDto {

  private int rank;
  private String nickName;
  private long point;

  public PointRankingResponseDto(String nickName, double score) {
    this.nickName = nickName;
    this.point = (long) score;
  }
}
