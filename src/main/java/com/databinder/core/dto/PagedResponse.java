package com.databinder.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> items;
    private long totalItems;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}