package de.lion5.spring.dvd.api;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class RestControllerException extends RuntimeException {

    private HttpStatus status;
    private String message;

    public RestControllerException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
