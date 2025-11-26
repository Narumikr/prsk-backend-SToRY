package com.example.untitled.artist;

import com.example.untitled.common.dto.ErrorDetails;
import com.example.untitled.common.exception.DuplicationResourceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.untitled.common.util.UtilsFunction.generateRandomString;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArtistController.class)
public class ArtistControllerTest {

    @Autowired
    private MockMvc mvcMock;

    @MockitoBean
    private ArtistService artistService;

    /**
     * POST /artists : Response success
     */
    @Test
    public void createArtistSuccess() throws Exception {
        String expectedArtistName = "Test artist";
        String expectedUnitName = "Test unit name";
        String expectedContent = "Test content";

        Artist mockArtist = new Artist();
        mockArtist.setId(1L);
        mockArtist.setArtistName(expectedArtistName);
        mockArtist.setUnitName(expectedUnitName);
        mockArtist.setContent(expectedContent);

        when(artistService.createArtist(argThat(request ->
                request.getArtistName().equals(expectedArtistName) &&
                        request.getUnitName().equals(expectedUnitName) &&
                        request.getContent().equals(expectedContent)
        ))).thenReturn(mockArtist);

        String reqBody = """
                {
                    "artistName": "%s",
                    "unitName": "%s",
                    "content": "%s"
                }
                """.formatted(expectedArtistName, expectedUnitName, expectedContent);

        mvcMock.perform(post("/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.artistName").value("Test artist"))
                .andExpect(jsonPath("$.unitName").value("Test unit name"))
                .andExpect(jsonPath("$.content").value("Test content"));
    }

    /**
     * POST /artists : Response BadRequest
     * artistNameのNull不可チェック
     */
    @Test
    public void createArtistError_withBadRequest_ArtistNameNull() throws Exception {
        String reqBody = """
                {
                    "artistName": null,
                    "unitName": "Test unit name",
                    "content": "Test content"
                }
                """;

        mvcMock.perform(post("/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details[0].field").value("artistName"));
    }

    /**
     * POST /artists : Response BadRequest
     * Request body validation error
     */
    @Test
    public void createArtistError_withBadRequest_LengthOver() throws Exception {
        String ngArtistName = generateRandomString(51);
        String ngUnitName = generateRandomString(26);
        String ngContent = generateRandomString(21);

        String reqBody = """
                {
                    "artistName": "%s",
                    "unitName": "%s",
                    "content": "%s"
                }
                """.formatted(ngArtistName, ngUnitName, ngContent);

        mvcMock.perform(post("/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details[*].field", containsInAnyOrder("artistName", "unitName", "content")));
    }

    /**
     * POST /artists : Conflict
     * 登録アーティスト名の重複エラー
     */
    @Test
    public void createArtistError_withConflict_AlreadyExist() throws Exception {
        when(artistService.createArtist(any()))
                .thenThrow(new DuplicationResourceException(
                        "Conflict detected",
                        List.of(new ErrorDetails("artistName", "Artist name already exist"))
                ));

        String reqBody = """
            {
                "artistName": "Test artist name",
                "unitName": "Test unit name",
                "content": "Test content"
            }
            """;

        mvcMock.perform(post("/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details[0].field").value("artistName"));
    }
}
