package com.orios.pgn;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Immutable parsed PGN game, including its complete move tree. */
public final class PgnGame {
    private final Map<String, String> tags;
    private final String initialFen;
    private final List<PgnMoveNode> continuations;

    public PgnGame(Map<String, String> tags, String initialFen,
                   List<PgnMoveNode> continuations) {
        this.tags = Map.copyOf(tags);
        this.initialFen = Objects.requireNonNull(initialFen, "initialFen must not be null");
        this.continuations = List.copyOf(continuations);
    }

    public Map<String, String> tags() {
        return tags;
    }

    public String tag(String name) {
        return tags.getOrDefault(name, "");
    }

    public String initialFen() {
        return initialFen;
    }

    public List<PgnMoveNode> continuations() {
        return continuations;
    }

    public String white() { return tag("White"); }
    public String black() { return tag("Black"); }
    public String result() { return tag("Result"); }
    public String event() { return tag("Event"); }
    public String site() { return tag("Site"); }
    public String eco() { return tag("ECO"); }

    public String year() {
        String date = tag("Date");
        return date.length() >= 4 ? date.substring(0, 4) : date;
    }
}
