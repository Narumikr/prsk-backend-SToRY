package com.example.untitled.artist;

import com.example.untitled.artist.dto.ArtistRequest;
import com.example.untitled.artist.dto.ArtistResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    // GET /artists : アーティスト一覧取得 - Get artists list
//    @GetMapping
//    public List<ArtistResponse> getArtistsList(
//            @RequestParam(required = false, defaultValue = "1") Integer page,
//            @RequestParam(required = false, defaultValue = "20") Integer limit
//    ) {
//        ;
//    }

    // POST /artists : アーティスト情報の登録 - Register artist information
    @PostMapping
    public ResponseEntity<ArtistResponse> registerArtist(
            @Valid @RequestBody ArtistRequest request
    ) {
        Artist artist = artistService.createArtist(request);
        ArtistResponse response = ArtistResponse.from(artist);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
