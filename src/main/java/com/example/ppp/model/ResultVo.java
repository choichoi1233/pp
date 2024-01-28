package com.example.marsphoto.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ResultVo<T> {
    public ResultVo(String msg, String code, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }
    private String msg;
    private String code;
    private T data;
}
