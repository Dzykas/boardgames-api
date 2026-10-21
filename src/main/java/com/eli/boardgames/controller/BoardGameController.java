package com.eli.boardgames.controller;

import com.eli.boardgames.dto.BoardGameRequest;
import com.eli.boardgames.dto.BoardGameResponse;
import com.eli.boardgames.service.BoardGameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boardgames")
public class BoardGameController {

    private final BoardGameService service;

    public BoardGameController(BoardGameService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoardGameResponse create(
            @Valid @RequestBody BoardGameRequest request) {
        return service.create(request);
    }
}
