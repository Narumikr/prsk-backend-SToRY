package com.example.untitled.e2e;

import com.example.untitled.artist.dto.ArtistListResponse;
import com.example.untitled.artist.dto.ArtistRequest;
import com.example.untitled.artist.dto.ArtistResponse;
import com.example.untitled.artist.dto.OptionalArtistRequest;
import com.example.untitled.common.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Artist E2E Tests")
class ArtistE2ETest extends E2ETestBase {

    private static final String ARTISTS_PATH = "/artists";

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String uniqueArtistName() {
        return "Artist-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private ArtistResponse createArtist(String artistName, String unitName, String content) {
        ArtistRequest request = new ArtistRequest();
        request.setArtistName(artistName);
        request.setUnitName(unitName);
        request.setContent(content);

        ResponseEntity<ArtistResponse> response = restTemplate.postForEntity(
                getBaseUrl() + ARTISTS_PATH,
                request,
                ArtistResponse.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody();
    }

    private ArtistResponse createArtist(String artistName) {
        return createArtist(artistName, null, null);
    }

    // ========================================================================
    // GET /artists - List Artists
    // ========================================================================

    @Nested
    @DisplayName("GET /artists")
    class GetArtists {

        @Test
        @DisplayName("Success - returns list with created artists")
        void getArtistsSuccess() {
            // Arrange: Create test artists
            String artistName1 = uniqueArtistName();
            String artistName2 = uniqueArtistName();
            createArtist(artistName1, "Unit1", "Content1");
            createArtist(artistName2, "Unit2", "Content2");

            // Act
            ResponseEntity<ArtistListResponse> response = restTemplate.getForEntity(
                    getBaseUrl() + ARTISTS_PATH,
                    ArtistListResponse.class
            );

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getItems());
            assertNotNull(response.getBody().getMeta());
            assertTrue(response.getBody().getItems().size() >= 2);
        }

        @Test
        @DisplayName("Success - returns paginated results with page and limit params")
        void getArtistsSuccess_withPagination() {
            // Arrange: Create 3 artists
            for (int i = 0; i < 3; i++) {
                createArtist(uniqueArtistName());
            }

            // Act: Get page 1 with limit 2
            ResponseEntity<ArtistListResponse> response = restTemplate.getForEntity(
                    getBaseUrl() + ARTISTS_PATH + "?page=1&limit=2",
                    ArtistListResponse.class
            );

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getMeta());
            assertEquals(2, response.getBody().getMeta().getLimit());
            assertTrue(response.getBody().getItems().size() <= 2);
        }
    }

    // ========================================================================
    // POST /artists - Create Artist
    // ========================================================================

    @Nested
    @DisplayName("POST /artists")
    class CreateArtist {

        @Test
        @DisplayName("Success - creates artist with all fields")
        void createArtistSuccess() {
            // Arrange
            ArtistRequest request = new ArtistRequest();
            request.setArtistName(uniqueArtistName());
            request.setUnitName("Leo/need");
            request.setContent("プロセカ");

            // Act
            ResponseEntity<ArtistResponse> response = restTemplate.postForEntity(
                    getBaseUrl() + ARTISTS_PATH,
                    request,
                    ArtistResponse.class
            );

            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getId());
            assertEquals(request.getArtistName(), response.getBody().getArtistName());
            assertEquals("Leo/need", response.getBody().getUnitName());
            assertEquals("プロセカ", response.getBody().getContent());
            assertNotNull(response.getBody().getAuditInfo());
        }

        @Test
        @DisplayName("Success - creates artist with required fields only")
        void createArtistSuccess_withRequiredFieldsOnly() {
            // Arrange
            ArtistRequest request = new ArtistRequest();
            request.setArtistName(uniqueArtistName());

            // Act
            ResponseEntity<ArtistResponse> response = restTemplate.postForEntity(
                    getBaseUrl() + ARTISTS_PATH,
                    request,
                    ArtistResponse.class
            );

            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getId());
            assertNull(response.getBody().getUnitName());
            assertNull(response.getBody().getContent());
        }

        @Test
        @DisplayName("Error - 409 Conflict when artistName already exists")
        void createArtistError_withConflict() {
            // Arrange: Create an artist first
            String existingName = uniqueArtistName();
            createArtist(existingName);

            // Try to create another with the same name
            ArtistRequest duplicateRequest = new ArtistRequest();
            duplicateRequest.setArtistName(existingName);

            // Act
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    getBaseUrl() + ARTISTS_PATH,
                    duplicateRequest,
                    ErrorResponse.class
            );

            // Assert
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(409, response.getBody().getStatusCode());
        }

        @Test
        @DisplayName("Error - 400 Bad Request when artistName is blank")
        void createArtistError_withBadRequest_blankArtistName() {
            // Arrange
            ArtistRequest request = new ArtistRequest();
            request.setArtistName("");

            // Act
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    getBaseUrl() + ARTISTS_PATH,
                    request,
                    ErrorResponse.class
            );

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getStatusCode());
        }

        @Test
        @DisplayName("Error - 400 Bad Request when artistName is null")
        void createArtistError_withBadRequest_nullArtistName() {
            // Arrange
            ArtistRequest request = new ArtistRequest();
            request.setArtistName(null);

            // Act
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    getBaseUrl() + ARTISTS_PATH,
                    request,
                    ErrorResponse.class
            );

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getStatusCode());
        }

        @Test
        @DisplayName("Error - 400 Bad Request when artistName exceeds max length")
        void createArtistError_withBadRequest_artistNameTooLong() {
            // Arrange: artistName max is 50 characters
            ArtistRequest request = new ArtistRequest();
            request.setArtistName("A".repeat(51));

            // Act
            ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                    getBaseUrl() + ARTISTS_PATH,
                    request,
                    ErrorResponse.class
            );

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    // ========================================================================
    // PUT /artists/{id} - Update Artist
    // ========================================================================

    @Nested
    @DisplayName("PUT /artists/{id}")
    class UpdateArtist {

        @Test
        @DisplayName("Success - updates all fields")
        void updateArtistSuccess() {
            // Arrange: Create an artist
            ArtistResponse created = createArtist(uniqueArtistName(), "OldUnit", "OldContent");

            OptionalArtistRequest updateRequest = new OptionalArtistRequest();
            updateRequest.setArtistName(uniqueArtistName());
            updateRequest.setUnitName("NewUnit");
            updateRequest.setContent("NewContent");

            // Act
            ResponseEntity<ArtistResponse> response = restTemplate.exchange(
                    getBaseUrl() + ARTISTS_PATH + "/" + created.getId(),
                    HttpMethod.PUT,
                    new HttpEntity<>(updateRequest),
                    ArtistResponse.class
            );

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(created.getId(), response.getBody().getId());
            assertEquals(updateRequest.getArtistName(), response.getBody().getArtistName());
            assertEquals("NewUnit", response.getBody().getUnitName());
            assertEquals("NewContent", response.getBody().getContent());
        }

        @Test
        @DisplayName("Success - updates partial fields (artistName only)")
        void updateArtistSuccess_partialUpdate() {
            // Arrange: Create an artist with all fields
            ArtistResponse created = createArtist(uniqueArtistName(), "OriginalUnit", "OriginalContent");

            OptionalArtistRequest updateRequest = new OptionalArtistRequest();
            updateRequest.setArtistName(uniqueArtistName());

            // Act
            ResponseEntity<ArtistResponse> response = restTemplate.exchange(
                    getBaseUrl() + ARTISTS_PATH + "/" + created.getId(),
                    HttpMethod.PUT,
                    new HttpEntity<>(updateRequest),
                    ArtistResponse.class
            );

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(updateRequest.getArtistName(), response.getBody().getArtistName());
            // Original values should be preserved
            assertEquals("OriginalUnit", response.getBody().getUnitName());
            assertEquals("OriginalContent", response.getBody().getContent());
        }

        @Test
        @DisplayName("Error - 404 Not Found when ID does not exist")
        void updateArtistError_withNotFound() {
            // Arrange
            OptionalArtistRequest updateRequest = new OptionalArtistRequest();
            updateRequest.setArtistName(uniqueArtistName());

            // Act: Try to update non-existent artist
            ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                    getBaseUrl() + ARTISTS_PATH + "/999999",
                    HttpMethod.PUT,
                    new HttpEntity<>(updateRequest),
                    ErrorResponse.class
            );

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().getStatusCode());
        }

        @Test
        @DisplayName("Error - 409 Conflict when updating to existing artistName")
        void updateArtistError_withConflict() {
            // Arrange: Create two artists
            String existingName = uniqueArtistName();
            createArtist(existingName);
            ArtistResponse toUpdate = createArtist(uniqueArtistName());

            // Try to update second artist with first artist's name
            OptionalArtistRequest updateRequest = new OptionalArtistRequest();
            updateRequest.setArtistName(existingName);

            // Act
            ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                    getBaseUrl() + ARTISTS_PATH + "/" + toUpdate.getId(),
                    HttpMethod.PUT,
                    new HttpEntity<>(updateRequest),
                    ErrorResponse.class
            );

            // Assert
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(409, response.getBody().getStatusCode());
        }

        @Test
        @DisplayName("Error - 400 Bad Request when artistName exceeds max length")
        void updateArtistError_withBadRequest_artistNameTooLong() {
            // Arrange: Create an artist
            ArtistResponse created = createArtist(uniqueArtistName());

            OptionalArtistRequest updateRequest = new OptionalArtistRequest();
            updateRequest.setArtistName("A".repeat(51));

            // Act
            ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                    getBaseUrl() + ARTISTS_PATH + "/" + created.getId(),
                    HttpMethod.PUT,
                    new HttpEntity<>(updateRequest),
                    ErrorResponse.class
            );

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    // ========================================================================
    // DELETE /artists/{id} - Delete Artist
    // ========================================================================

    @Nested
    @DisplayName("DELETE /artists/{id}")
    class DeleteArtist {

        @Test
        @DisplayName("Success - deletes artist and returns 204")
        void deleteArtistSuccess() {
            // Arrange: Create an artist
            ArtistResponse created = createArtist(uniqueArtistName());

            // Act
            ResponseEntity<Void> response = restTemplate.exchange(
                    getBaseUrl() + ARTISTS_PATH + "/" + created.getId(),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

            // Verify: Try to delete again should return 404 (soft-deleted)
            ResponseEntity<ErrorResponse> verifyResponse = restTemplate.exchange(
                    getBaseUrl() + ARTISTS_PATH + "/" + created.getId(),
                    HttpMethod.DELETE,
                    null,
                    ErrorResponse.class
            );
            assertEquals(HttpStatus.NOT_FOUND, verifyResponse.getStatusCode());
        }

        @Test
        @DisplayName("Error - 404 Not Found when ID does not exist")
        void deleteArtistError_withNotFound() {
            // Act
            ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                    getBaseUrl() + ARTISTS_PATH + "/999999",
                    HttpMethod.DELETE,
                    null,
                    ErrorResponse.class
            );

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().getStatusCode());
        }

        @Test
        @DisplayName("Success - deleted artist does not appear in list")
        void deleteArtistSuccess_notInList() {
            // Arrange: Create and delete an artist
            String artistName = uniqueArtistName();
            ArtistResponse created = createArtist(artistName);

            restTemplate.exchange(
                    getBaseUrl() + ARTISTS_PATH + "/" + created.getId(),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            // Act: Get list
            ResponseEntity<ArtistListResponse> listResponse = restTemplate.getForEntity(
                    getBaseUrl() + ARTISTS_PATH,
                    ArtistListResponse.class
            );

            // Assert: Deleted artist should not be in the list
            assertEquals(HttpStatus.OK, listResponse.getStatusCode());
            assertNotNull(listResponse.getBody());

            boolean artistFound = listResponse.getBody().getItems().stream()
                    .anyMatch(a -> a.getId().equals(created.getId()));
            assertFalse(artistFound, "Deleted artist should not appear in list");
        }
    }
}
