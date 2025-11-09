package com.example.untitled.artist.dto;

import com.example.untitled.artist.Artist;
import com.example.untitled.common.dto.AuditInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * アーティストマスタAPIレスポンス
 */
@Getter
@AllArgsConstructor
public class ArtistResponse {

    /** ID **/
    private Long id;

    /** アーティスト名 **/
    private String artistName;

    /** ユニット名 **/
    private String unitName;

    /** コンテンツ名 **/
    private String content;

    /** 監査情報 **/
    private AuditInfo auditInfo;

    public static ArtistResponse from(Artist artist) {
        return new ArtistResponse(
                artist.getId(),
                artist.getArtistName(),
                artist.getUnitName(),
                artist.getContent(),
                AuditInfo.from(artist)
        );
    }
}
