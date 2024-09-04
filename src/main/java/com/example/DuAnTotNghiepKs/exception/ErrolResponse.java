package com.example.DuAnTotNghiepKs.exception;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrolResponse {

    public int status;
    public String message;
    public long timestamp;

    public ErrolResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp=System.currentTimeMillis();
    }
}
