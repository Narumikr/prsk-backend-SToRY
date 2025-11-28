package com.example.untitled.artist;

import com.example.untitled.artist.dto.ArtistRequest;
import com.example.untitled.common.exception.DuplicationResourceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    /**
     * createArtists : 正常系 - アーティストが正常に作成される
     */
    @Test
    public void createArtistSuccess() {
        ArtistRequest request = new ArtistRequest();
        request.setArtistName("Test artist name");
        request.setUnitName("Test unit name");
        request.setContent("Test content");

        Artist createdArtist = new Artist();
        createdArtist.setId(1L);
        createdArtist.setArtistName("Test artist name");
        createdArtist.setUnitName("Test unit name");
        createdArtist.setContent("Test content");

        when(artistRepository.findByArtistNameAndIsDeleted("Test artist name", false)).thenReturn(Optional.empty());
        when(artistRepository.save(any(Artist.class))).thenReturn(createdArtist);

        Artist result = artistService.createArtist(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test artist name", result.getArtistName());
        assertEquals("Test unit name", result.getUnitName());
        assertEquals("Test content", result.getContent());

        verify(artistRepository, times(1)).findByArtistNameAndIsDeleted("Test artist name", false);
        verify(artistRepository, times(1)).save(any(Artist.class));
    }

    /**
     * createArtists : 異常系 - アーティスト名が重複しており、例外をスルーする
     */
    public void createArtistsError_withDuplication() {
        ArtistRequest request = new ArtistRequest();
        request.setArtistName("Test artist name");
        request.setUnitName("Test unit name");
        request.setContent("Test content");

        Artist createdArtist = new Artist();
        createdArtist.setId(1L);
        createdArtist.setArtistName("Test artist name");
        createdArtist.setUnitName("Test unit name");
        createdArtist.setContent("Test content");

        when(artistRepository.findByArtistNameAndIsDeleted("Test artist name", false)).thenReturn(Optional.of(createdArtist));

        DuplicationResourceException exception = assertThrows(
                DuplicationResourceException.class,
                () -> artistService.createArtist(request)
        );

        assertNotNull(exception.getDetails());
        assertEquals("Test artist name", exception.getDetails().get(0).getField());

        verify(artistRepository, times(1)).findByArtistNameAndIsDeleted("Test artist name", false);
        verify(artistRepository, never()).save(any(Artist.class));
    }
}
