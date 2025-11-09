package com.example.untitled.artist;

import com.example.untitled.artist.dto.ArtistRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public Artist createArtist(ArtistRequest dto) {
        artistRepository.findByArtistNameAndIsDeleted(dto.getArtistName(), false)
                .ifPresent(artist -> {
                    throw new IllegalArgumentException("Artist name already exist: " + dto.getArtistName());
                });

        Artist artist = new Artist();
        artist.setArtistName(dto.getArtistName());
        artist.setUnitName(dto.getUnitName());
        artist.setContent(dto.getContent());

        return artistRepository.save(artist);
    }
}
