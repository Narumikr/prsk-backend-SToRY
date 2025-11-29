package com.example.untitled.artist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Page<Artist> findByIsDeleted(boolean isDeleted, Pageable pageable);

    Optional<Artist> findByIdAndIsDeleted(Long id, boolean isDeleted);

    Optional<Artist> findByArtistNameAndIsDeleted(String artistName, boolean isDeleted);
}
