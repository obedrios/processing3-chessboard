package com.orios.pgn;

import java.util.List;

/** Immutable snapshot emitted by the playback controller. */
public record PlaybackState(PgnGame game, String fen, PgnMoveNode currentMove,
                            int currentIndex, List<PgnMoveNode> activeLine) {
    public PlaybackState {
        activeLine = List.copyOf(activeLine);
    }

    public boolean atStart() {
        return currentIndex < 0;
    }

    public boolean atEnd() {
        return activeLine.isEmpty() || currentIndex >= activeLine.size() - 1;
    }
}
