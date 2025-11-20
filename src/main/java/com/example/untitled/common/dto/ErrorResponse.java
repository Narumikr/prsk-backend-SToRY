package com.example.untitled.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * エラーレスポンス
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

    /** ステータスコード **/
    private int statusCode;

    /** エラータイプ **/
    private String error;

    /** エラーメッセージ **/
    private String message;
}
