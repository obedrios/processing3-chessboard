package com.orios.pgn;

import java.util.List;
import java.util.Objects;

/** One immutable move in a PGN game tree. */
public final class PgnMoveNode {
    private final String san;
    private final String fenAfter;
    private final List<String> comments;
    private final List<String> nags;
    private final List<PgnMoveNode> continuations;

    public PgnMoveNode(String san, String fenAfter, List<String> comments,
                       List<String> nags, List<PgnMoveNode> continuations) {
        this.san = Objects.requireNonNull(san, "san must not be null");
        this.fenAfter = Objects.requireNonNull(fenAfter, "fenAfter must not be null");
        this.comments = List.copyOf(comments);
        this.nags = List.copyOf(nags);
        this.continuations = List.copyOf(continuations);
    }

    public String san() {
        return san;
    }

    public String fenAfter() {
        return fenAfter;
    }

    public List<String> comments() {
        return comments;
    }

    public List<String> nags() {
        return nags;
    }

    /** The first continuation is the PGN main line; later entries are RAVs. */
    public List<PgnMoveNode> continuations() {
        return continuations;
    }
}
