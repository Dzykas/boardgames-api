package com.eli.boardgames.service;

import com.eli.boardgames.dto.BoardGameRequest;
import com.eli.boardgames.dto.BoardGameResponse;
import com.eli.boardgames.dto.PageResponse;
import com.eli.boardgames.exception.BoardGameNotFoundException;
import com.eli.boardgames.model.BoardGame;
import com.eli.boardgames.model.Complexity;
import com.eli.boardgames.model.User;
import com.eli.boardgames.repository.BoardGameRepository;
import com.eli.boardgames.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardGameService {

    private static final Logger log = LoggerFactory.getLogger(BoardGameService.class);
    private static final int MAX_PAGE_SIZE = 100;

    private final BoardGameRepository repository;
    private final UserRepository userRepository;

    public BoardGameService(BoardGameRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BoardGameResponse create(String username, BoardGameRequest request) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User no longer exists"));

        BoardGame game = new BoardGame();
        game.setOwner(owner);
        applyRequest(game, request);

        BoardGame saved = repository.save(game);
        log.info("User {} created board game id={}", username, saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BoardGameResponse getById(String username, Long id) {
        return toResponse(findOwnedOrThrow(id, username));
    }

    @Transactional(readOnly = true)
    public PageResponse<BoardGameResponse> list(String username, String title,
                                                Complexity complexity, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("id"));

        Specification<BoardGame> spec =
                (root, query, cb) -> cb.equal(root.get("owner").get("username"), username);

        if (title != null && !title.isBlank()) {
            String pattern = "%" + title.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern));
        }
        if (complexity != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("complexity"), complexity));
        }

        Page<BoardGameResponse> result = repository.findAll(spec, pageable).map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional
    public BoardGameResponse update(String username, Long id, BoardGameRequest request) {
        BoardGame game = findOwnedOrThrow(id, username);
        applyRequest(game, request);
        BoardGame saved = repository.save(game);
        log.info("User {} updated board game id={}", username, id);
        return toResponse(saved);
    }

    @Transactional
    public void delete(String username, Long id) {
        BoardGame game = findOwnedOrThrow(id, username);
        repository.delete(game);
        log.info("User {} deleted board game id={}", username, id);
    }

    private BoardGame findOwnedOrThrow(Long id, String username) {
        return repository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new BoardGameNotFoundException(id));
    }

    private void applyRequest(BoardGame game, BoardGameRequest request) {
        game.setTitle(request.title());
        game.setPublisher(request.publisher());
        game.setMinPlayers(request.minPlayers());
        game.setMaxPlayers(request.maxPlayers());
        game.setPlayTimeMinutes(request.playTimeMinutes());
        game.setComplexity(request.complexity());
        game.setPurchaseDate(request.purchaseDate());
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