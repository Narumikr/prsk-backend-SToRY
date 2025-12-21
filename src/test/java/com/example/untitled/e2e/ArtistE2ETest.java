package com.example.untitled.e2e;

import com.example.untitled.artist.dto.ArtistRequest;
import com.example.untitled.artist.dto.ArtistResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Artist E2E Tests")
class ArtistE2ETest extends E2ETestBase {

    @Test
    @DisplayName("POST /artists - Create artist successfully")
    void createArtistSuccess() {
        // Arrange
        ArtistRequest request = new ArtistRequest();
        request.setArtistName("Leo/need");
        request.setUnitName("Leo/need");
        request.setContent("プロセカ");

        // Act
        ResponseEntity<ArtistResponse> response = restTemplate.postForEntity(
                getBaseUrl() + "/artists",
                request,
                ArtistResponse.class
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Leo/need", response.getBody().getArtistName());
    }
}
