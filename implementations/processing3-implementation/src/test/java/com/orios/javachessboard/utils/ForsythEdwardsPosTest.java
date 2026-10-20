package com.orios.javachessboard.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ForsythEdwardsPosTest {

    @Test
    void decodesTheDefaultPositionAndItsMetadata() {
        ForsythEdwardsPos fen = new ForsythEdwardsPos();

        List<String[]> position = fen.getDefaultNewPosition();

        assertEquals(8, position.size());
        assertArrayEquals(new String[] {"r", "n", "b", "q", "k", "b", "n", "r"}, position.get(0));
        assertArrayEquals(new String[] {"R", "N", "B", "Q", "K", "B", "N", "R"}, position.get(7));
        assertTrue(fen.isWhiteToMove());
        assertTrue(fen.isWhiteCanShortCastle());
        assertTrue(fen.isWhiteCanLongCastle());
        assertTrue(fen.isBlackCanShortCastle());
        assertTrue(fen.isBlackCanLongCastle());
        assertEquals("-", fen.getEnPassant());
        assertEquals(0, fen.getHalfmoveClock());
        assertEquals(1, fen.getCurrentMoveNumber());
    }

    @Test
    void parsesEveryFenField() {
        ForsythEdwardsPos fen = new ForsythEdwardsPos(
                "r3k2r/ppp2ppp/2n5/3pp3/8/2N2N2/PPP2PPP/R3K2R w Kq e6 12 27");

        fen.getDecodedPositionFromFen();

        assertTrue(fen.isWhiteToMove());
        assertTrue(fen.isWhiteCanShortCastle());
        assertFalse(fen.isWhiteCanLongCastle());
        assertFalse(fen.isBlackCanShortCastle());
        assertTrue(fen.isBlackCanLongCastle());
        assertEquals("e6", fen.getEnPassant());
        assertEquals(12, fen.getHalfmoveClock());
        assertEquals(27, fen.getCurrentMoveNumber());
    }

    @Test
    void parsingASecondPositionResetsAllFlags() {
        ForsythEdwardsPos fen = new ForsythEdwardsPos();
        fen.getDecodedPositionFromFen("8/8/8/8/8/8/8/8 b KQkq - 5 10");

        fen.getDecodedPositionFromFen("8/8/8/8/8/8/8/8 w - - 0 1");

        assertTrue(fen.isWhiteToMove());
        assertFalse(fen.isWhiteCanShortCastle());
        assertFalse(fen.isWhiteCanLongCastle());
        assertFalse(fen.isBlackCanShortCastle());
        assertFalse(fen.isBlackCanLongCastle());
    }

    @Test
    void decodesAnEncodedRank() {
        ForsythEdwardsPos fen = new ForsythEdwardsPos();

        assertArrayEquals(
                new String[] {"r", "-", "-", "q", "-", "r", "k", "-"},
                fen.decodeRow("r2q1rk1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "8/8/8/8/8/8/8/8 w - - 0",
        "8/8/8/8/8/8/8 w - - 0 1",
        "9/8/8/8/8/8/8/8 w - - 0 1",
        "44/8/8/8/8/8/8/8 w - - 0 1",
        "7x/8/8/8/8/8/8/8 w - - 0 1",
        "8/8/8/8/8/8/8/8 x - - 0 1",
        "8/8/8/8/8/8/8/8 w qK - 0 1",
        "8/8/8/8/8/8/8/8 w KK - 0 1",
        "8/8/8/8/8/8/8/8 w - e3 0 1",
        "8/8/8/8/8/8/8/8 b - e6 0 1",
        "8/8/8/8/8/8/8/8 w - - -1 1",
        "8/8/8/8/8/8/8/8 w - - 0 0"
    })
    void rejectsMalformedFen(String invalidFen) {
        ForsythEdwardsPos fen = new ForsythEdwardsPos();

        assertThrows(IllegalArgumentException.class, () -> fen.getDecodedPositionFromFen(invalidFen));
    }

    @Test
    void rejectsNullFen() {
        ForsythEdwardsPos fen = new ForsythEdwardsPos();

        assertThrows(NullPointerException.class, () -> fen.getDecodedPositionFromFen(null));
    }
}
