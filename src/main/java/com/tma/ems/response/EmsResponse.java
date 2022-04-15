package com.tma.ems.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class EmsResponse {
    private HttpStatus httpStatus;
    private String message;
    private Object data;

    public EmsResponse(HttpStatus httpStatus, String message, Object data) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.data = data;
    }
}
