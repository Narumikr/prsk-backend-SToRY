package com.example.untitled.prskmusic;

import com.example.untitled.prskmusic.dto.PrskMusicRequest;
import com.example.untitled.prskmusic.dto.PrskMusicResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prsk-music")
@Validated
public class PrskMusicController {

    private final PrskMusicService prskMusicService;

    public PrskMusicController(PrskMusicService prskMusicService) {
        this.prskMusicService = prskMusicService;
    }

    // GET /prsk-music : プロセカ楽曲一覧取得 - Get prsk music list

    // POST /prsk-music : プロセカ楽曲情報の登録 - Register prsk music information
    @PostMapping
    public ResponseEntity<PrskMusicResponse> registerPrskMusic(
            @Valid @RequestBody PrskMusicRequest request
    ) {
        PrskMusic prskMusic = prskMusicService.createPrskMusic(request);
        PrskMusicResponse response = PrskMusicResponse.from(prskMusic);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
