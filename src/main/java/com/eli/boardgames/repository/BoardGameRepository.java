package com.eli.boardgames.repository;

import com.eli.boardgames.model.BoardGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BoardGameRepository
        extends JpaRepository<BoardGame, Long>, JpaSpecificationExecutor<BoardGame> {

    Optional<BoardGame> findByIdAndOwnerUsername(Long id, String username);
}