package com.eli.boardgames.exception;

public class BoardGameNotFoundException extends RuntimeException {
    public BoardGameNotFoundException(Long id) {
        super("Board game with id " + id + " not found");
    }
}
