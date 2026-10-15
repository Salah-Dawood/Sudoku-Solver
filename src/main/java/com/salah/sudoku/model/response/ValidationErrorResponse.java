package com.salah.sudoku.model.response;

import lombok.Data;

import java.util.Map;

@Data
public class ValidationErrorResponse {
    private String message;
    private Map<String, String> errors;

    public ValidationErrorResponse(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }
}
