package com.example.marsphoto.model;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UnauthorizedCustomException extends RuntimeException {

    private String message;
    private String code;
    private String returnUrl;
    private String reToken;
    public UnauthorizedCustomException(String message, String code , String returnUrl) {
        this.code = code;
        this.message = message;
        this.returnUrl = returnUrl;
    }
    public UnauthorizedCustomException(String message, String code , String returnUrl, String reToken) {
        this.code = code;
        this.message = message;
        this.returnUrl = returnUrl;
        this.reToken = reToken;
    }
}