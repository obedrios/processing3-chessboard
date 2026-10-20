package com.orios.pgn.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.orios.javachessboard.utils.ForsythEdwardsPos;
import com.orios.pgn.PgnGame;
import com.orios.pgn.PgnMoveNode;
import com.orios.pgn.PlaybackState;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScoresheetTableModelTest {
    @Test
    void mapsWhiteAndBlackCellsBackToPlaybackIndexes() {
        PgnMoveNode e4 = move("e4");
        PgnMoveNode e5 = move("e5");
        PgnMoveNode nf3 = move("Nf3");
        PgnGame game = new PgnGame(Map.of(), ForsythEdwardsPos.DEFAULT_POSITION, List.of(e4));
        PlaybackState state = new PlaybackState(game, nf3.fenAfter(), nf3, 2, List.of(e4, e5, nf3));
        ScoresheetTableModel model = new ScoresheetTableModel();
        model.setPlaybackState(state);

        assertEquals(2, model.getRowCount());
        assertEquals("e4", model.getValueAt(0, 1));
        assertEquals("e5", model.getValueAt(0, 2));
        assertEquals(2, model.moveIndexAt(1, 1));
    }

    private static PgnMoveNode move(String san) {
        return new PgnMoveNode(san, ForsythEdwardsPos.DEFAULT_POSITION, List.of(), List.of(), List.of());
    }
}
