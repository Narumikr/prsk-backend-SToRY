package com.example.untitled.artist;

import com.example.untitled.artist.dto.ArtistRequest;
import com.example.untitled.artist.dto.OptionalArtistRequest;
import com.example.untitled.common.dto.ErrorDetails;
import com.example.untitled.common.exception.DuplicationResourceException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.untitled.common.util.EntityHelper.*;

@Service
@Transactional
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public Page<Artist> getAllArtists(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return artistRepository.findByIsDeleted(false, pageable);
    }

    public Artist createArtist(ArtistRequest dto) {
        artistRepository.findByArtistNameAndIsDeleted(dto.getArtistName(), false)
                .ifPresent(artist -> {
                    throw new DuplicationResourceException(
                            "Conflict detected",
                            List.of(new ErrorDetails(
                                    "artistName",
                                    "Artist name already exist: " + dto.getArtistName()))
                    );
                });

        Artist artist = new Artist();
        artist.setArtistName(dto.getArtistName());
        artist.setUnitName(dto.getUnitName());
        artist.setContent(dto.getContent());

        return artistRepository.save(artist);
    }

    public Artist updateArtist(Long id, OptionalArtistRequest dto) {
        Artist artist = artistRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found for id: " + id));

        if(dto.getArtistName() != null && !dto.getArtistName().equals(artist.getArtistName())) {
            artistRepository.findByArtistNameAndIsDeleted(dto.getArtistName(), false)
                    .ifPresent(existArtist -> {
                        throw new DuplicationResourceException(
                                "Conflict detected",
                                List.of(new ErrorDetails(
                                        "artistName",
                                        "Artist name already exist: " + dto.getArtistName()))
                        );
                    });
        }

        // Memo: artist::setArtistNameはラムダ式の簡略記法で(value) -> artist.setArtistName(value));と同じ
        updateIfNotNull(dto.getArtistName(), artist::setArtistName);
        updateIfNotNull(dto.getUnitName(), artist::setUnitName);
        updateIfNotNull(dto.getContent(), artist::setContent);

        return artistRepository.save(artist);
    }

    public void deleteArtist(Long id) {
        Artist artist = artistRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found for id: " + id));

        artist.setDeleted(true);
        artistRepository.save(artist);
    }
}
