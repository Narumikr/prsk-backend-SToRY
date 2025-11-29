package com.example.untitled.artist.dto;

import com.example.untitled.artist.Artist;
import com.example.untitled.common.dto.MetaInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * アーティストマスタAPIレスポンス for GET
 */
@Getter
@AllArgsConstructor
public class ArtistListResponse {

    /** アーティストリスト **/
    private List<ArtistResponse> items;

    /** メタ情報 **/
    private MetaInfo meta;

    public static ArtistListResponse from(Page<Artist> artistPage) {
        List<ArtistResponse> items = artistPage.getContent().stream()
                .map(artist -> ArtistResponse.from(artist))
                .toList();

        MetaInfo meta = MetaInfo.from(artistPage);

        return new ArtistListResponse(items, meta);
    }
}
