package com.example.tiamedsadmin.utility.responses;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({
        "status",
        "message",
        "count",
        "data",
        "timestamp"
})
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;
    private long count;
    private LocalDateTime timestamp;

    public ApiResponse(HttpStatus status, String message, T data, long count) {
        this.status = status.value();
        this.message = message;
        this.data = data;
        this.count = count;
        this.timestamp = LocalDateTime.now();
    }

    // Convenience constructor (non-list responses)
    public ApiResponse(HttpStatus status, String message, T data) {
        this(status, message, data, data == null ? 0 : 1);
    }
}
