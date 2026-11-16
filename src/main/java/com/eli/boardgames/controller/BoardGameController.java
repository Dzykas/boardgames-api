package com.eli.boardgames.controller;

import com.eli.boardgames.dto.BoardGameRequest;
import com.eli.boardgames.dto.BoardGameResponse;
import com.eli.boardgames.dto.PageResponse;
import com.eli.boardgames.model.Complexity;
import com.eli.boardgames.service.BoardGameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody BoardGameRequest request) {
        return service.create(principal.getUsername(), request);
    }

    @GetMapping
    public PageResponse<BoardGameResponse> list(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Complexity complexity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.list(principal.getUsername(), title, complexity, page, size);
    }

    @GetMapping("/{id}")
    public BoardGameResponse getById(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        return service.getById(principal.getUsername(), id);
    }

    @PutMapping("/{id}")
    public BoardGameResponse update(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @Valid @RequestBody BoardGameRequest request) {
        return service.update(principal.getUsername(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        service.delete(principal.getUsername(), id);
    }
}