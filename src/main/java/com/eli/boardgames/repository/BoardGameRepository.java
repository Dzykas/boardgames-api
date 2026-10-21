package com.eli.boardgames.repository;

import com.eli.boardgames.model.BoardGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardGameRepository extends JpaRepository<BoardGame, Long> {}
