package com.example.untitled.user.dto;

import com.example.untitled.common.dto.AuditInfo;
import com.example.untitled.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ユーザーマスターAPIレスポンス
 */
@Getter
@AllArgsConstructor
public class UserResponse {

    /** ユーザーID **/
    private Long id;

    /** ユーザー名 **/
    private String userName;

    /** 監査情報 **/
    private AuditInfo auditInfo;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUserName(),
                AuditInfo.from(user)
        );
    }
}
