package com.bod.bod.challenge.dto;

import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.Challenge;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeSummaryResponseDto {

  private Long id;
  private String title;
  private String content;
  private Category category;

  public ChallengeSummaryResponseDto(Challenge challenge) {
	this.id = challenge.getId();
	this.title = challenge.getTitle();
	this.content = challenge.getContent();
	this.category = challenge.getCategory();
  }
}
