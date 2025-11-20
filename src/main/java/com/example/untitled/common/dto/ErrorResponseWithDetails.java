package com.example.untitled.common.dto;

import lombok.Getter;

import java.util.List;

/**
 * 詳細付きエラーレスポンス
 */
@Getter
public class ErrorResponseWithDetails extends ErrorResponse {

    /** エラー詳細情報 **/
    private List<ErrorDetails> details;

    public ErrorResponseWithDetails(int statusCode, String error, String message, List<ErrorDetails> details) {
        super(statusCode, error, message);
        this.details = details;
    }
}
