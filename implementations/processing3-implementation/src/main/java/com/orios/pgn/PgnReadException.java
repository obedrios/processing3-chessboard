package com.orios.pgn;

/** Indicates that a PGN file could not be parsed atomically. */
public final class PgnReadException extends Exception {
    public PgnReadException(String message) {
        super(message);
    }

    public PgnReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
