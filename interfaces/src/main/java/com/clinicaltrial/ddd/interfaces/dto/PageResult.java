package com.clinicaltrial.ddd.interfaces.dto;

import java.util.Collections;
import java.util.List;

/**
 * 通用分页结果.
 *
 * @param <T> 数据类型
 */
public class PageResult<T> {

    private List<T> content;
    private int page;
    private int size;
    private long total;

    public PageResult() {
        this.content = Collections.emptyList();
    }

    public PageResult(List<T> content, int page, int size, long total) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
