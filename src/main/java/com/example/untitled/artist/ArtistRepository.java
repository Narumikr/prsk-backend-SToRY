package com.example.untitled.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByArtistNameAndIsDeleted(String artistName, boolean isDeleted);
}
