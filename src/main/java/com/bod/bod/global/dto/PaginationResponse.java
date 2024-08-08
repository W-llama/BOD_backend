package com.bod.bod.global.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaginationResponse<T> {
  private List<T> content;
  private int totalPages;
  private long totalElements;
  private int currentPage;
  private int pageSize;

  public PaginationResponse(List<T> content, int totalPages, long totalElements, int currentPage, int pageSize) {
	this.content = content;
	this.totalPages = totalPages;
	this.totalElements = totalElements;
	this.currentPage = currentPage;
	this.pageSize = pageSize;
  }
}
