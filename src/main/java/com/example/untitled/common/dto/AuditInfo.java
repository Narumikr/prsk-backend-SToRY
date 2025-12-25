package com.example.untitled.common.dto;

import com.example.untitled.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Audit 情報
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuditInfo {

    /** レコード作成日時 **/
    private LocalDateTime createdAt;

    /** レコード作成者 **/
    private String createdBy;

    /** レコード更新日時 **/
    private LocalDateTime updatedAt;

    /** レコード更新者 **/
    private String updatedBy;

    public static AuditInfo from(BaseEntity entity) {
        return new AuditInfo(
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }
}
