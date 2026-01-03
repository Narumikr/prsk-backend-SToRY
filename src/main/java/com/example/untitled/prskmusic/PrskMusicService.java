package com.example.untitled.prskmusic;

import com.example.untitled.artist.Artist;
import com.example.untitled.artist.ArtistRepository;
import com.example.untitled.common.dto.ErrorDetails;
import com.example.untitled.common.exception.DuplicationResourceException;
import com.example.untitled.prskmusic.dto.PrskMusicRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrskMusicService {

    private final PrskMusicRepository prskMusicRepository;

    private final ArtistRepository artistRepository;

    public PrskMusicService(
            PrskMusicRepository prskMusicRepository,
            ArtistRepository artistRepository
    ) {
        this.prskMusicRepository = prskMusicRepository;
        this.artistRepository = artistRepository;
    }

    public PrskMusic createPrskMusic(PrskMusicRequest reqDto) {
        prskMusicRepository.findByTitleAndMusicTypeAndIsDeleted(
                reqDto.getTitle(),
                reqDto.getMusicType(),
                false
        ).ifPresent(prskMusic -> {
            throw new DuplicationResourceException(
                    "Conflict detected",
                    List.of(new ErrorDetails(
                            "Title and MusicType",
                            "Duplicate title and music type combination."
                    ))
            );
        });

        Artist artist = artistRepository.findByIdAndIsDeleted(reqDto.getArtistId(), false)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found for id: " + reqDto.getArtistId()));

        PrskMusic prskMusic = new PrskMusic();
        prskMusic.setTitle(reqDto.getTitle());
        prskMusic.setArtist(artist);
        prskMusic.setMusicType(reqDto.getMusicType());
        prskMusic.setSpecially(reqDto.getSpecially());
        prskMusic.setLyricsName(reqDto.getLyricsName());
        prskMusic.setMusicName(reqDto.getMusicName());
        prskMusic.setFeaturing(reqDto.getFeaturing());
        prskMusic.setYoutubeLink(reqDto.getYoutubeLink());

        return prskMusicRepository.save(prskMusic);
    }
}
