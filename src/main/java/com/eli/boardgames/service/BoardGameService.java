package com.eli.boardgames.service;

import com.eli.boardgames.dto.BoardGameResponse;
import com.eli.boardgames.dto.BoardGameRequest;
import com.eli.boardgames.model.BoardGame;
import com.eli.boardgames.repository.BoardGameRepository;
import org.springframework.stereotype.Service;
import com.eli.boardgames.exception.BoardGameNotFoundException;

@Service
public class BoardGameService {
    private final BoardGameRepository repository;

    public BoardGameService(BoardGameRepository repository) {
        this.repository = repository;
    }

    public BoardGameResponse create(BoardGameRequest request) {
        BoardGame game = new BoardGame();

        game.setTitle(request.title());
        game.setPublisher(request.publisher());
        game.setMinPlayers(request.minPlayers());
        game.setMaxPlayers(request.maxPlayers());
        game.setPlayTimeMinutes(request.playTimeMinutes());
        game.setComplexity(request.complexity());
        game.setPurchaseDate(request.purchaseDate());

        BoardGame saved = repository.save(game);

        return toResponse(saved);
    }

    public BoardGameResponse getById(Long id) {
        BoardGame game = repository.findById(id)
                .orElseThrow(() -> new BoardGameNotFoundException(id));
        return toResponse(game);
    }

    private BoardGameResponse toResponse(BoardGame game) {
        return new BoardGameResponse(
                game.getId(),
                game.getTitle(),
                game.getPublisher(),
                game.getMinPlayers(),
                game.getMaxPlayers(),
                game.getPlayTimeMinutes(),
                game.getComplexity(),
                game.getPurchaseDate(),
                game.getCreatedAt(),
                game.getUpdatedAt()
        );
    }


}
