package com.clinicaltrial.ddd.interfaces.dto;

/**
 * 通用 ID 响应，用于创建操作返回新实体的标识.
 */
public class IdResponse {

    private Long id;

    public IdResponse() {
    }

    public IdResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
