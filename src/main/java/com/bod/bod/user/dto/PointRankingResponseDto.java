package com.bod.bod.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PointRankingResponseDto {

  private int rank;
  private String name;
  private long point;

  public PointRankingResponseDto(String name, double score) {
    this.name = name;
    this.point = (long) score;
  }
}
