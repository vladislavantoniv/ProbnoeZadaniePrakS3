package com.lab.dto;

import java.util.List;
@SuppressWarnings("unused")
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PageResponse() {}

    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    public List<T> getContent() { return content; }
    @SuppressWarnings("unused")
    public void setContent(List<T> content) { this.content = content; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotalElements() { return totalElements; }
    @SuppressWarnings("unused")
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    @SuppressWarnings("unused")
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    @SuppressWarnings("unused")
    public boolean isLast() { return last; }
    @SuppressWarnings("unused")
    public void setLast(boolean last) { this.last = last; }
}