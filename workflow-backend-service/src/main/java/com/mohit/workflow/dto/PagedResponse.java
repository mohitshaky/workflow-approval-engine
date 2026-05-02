package com.mohit.workflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PagedResponse<T> {
    private List<T> content;
    private long totalElements;

    public PagedResponse() {}
    public PagedResponse(List<T> content, long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
    }

}
