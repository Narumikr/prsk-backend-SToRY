package com.example.untitled.user.dto;

import com.example.untitled.common.dto.MetaInfo;
import com.example.untitled.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * ユーザーマスタAPIレスポンス for GET
 */
@Getter
@AllArgsConstructor
public class UserListResponse {

    /** ユーザーリスト **/
    private List<UserResponse> items;

    /** メタ情報 **/
    private MetaInfo metaInfo;

    public static UserListResponse from(Page<User> userPage) {
        List<UserResponse> items = userPage.getContent().stream()
                .map(user -> UserResponse.from(user))
                .toList();

        MetaInfo meta = MetaInfo.from(userPage);

        return new UserListResponse(items, meta);
    }
}
