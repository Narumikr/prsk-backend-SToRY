package com.example.untitled.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * エラー詳細情報
 */

@Getter
@AllArgsConstructor
public class ErrorDetails {

    /** エラー発生フィールド **/
    private String field;

    /** エラーメッセージ **/
    private String message;
}
