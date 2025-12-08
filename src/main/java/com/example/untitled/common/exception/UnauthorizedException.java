package com.example.untitled.common.exception;

import com.example.untitled.common.dto.ErrorDetails;

import java.util.List;

public class UnauthorizedException extends RuntimeException {

    private final List<ErrorDetails> details;

    public UnauthorizedException(String message, List<ErrorDetails> details) {
        super(message);
        this.details = details;
    }

    public List<ErrorDetails> getDetails() {
        return this.details;
    }
}
