package com.orios.javachessboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

class ChessBoardMovementTest {

    private static final float SQUARE_SIZE = 10.0f;

    private StubApplet applet;
    private ChessBoard board;

    @BeforeEach
    void setUp() {
        applet = new StubApplet();
        board = new ChessBoard(applet, SQUARE_SIZE);
        board.setDefaultNewGamePosition();
    }

    @Test
    void resizesAllSquaresWithoutChangingThePosition() {
        String fen = board.getFENStringFromCurrentPosition();
        board.setSquareSize(42.5f);

        assertEquals(42.5f, board.getSquareSize());
        for (SquareBoard square : board.getChessBoard()) {
            assertEquals(42.5f, square.getSquareSize());
        }
        assertEquals(fen, board.getFENStringFromCurrentPosition());
    }

    @Test
    void movesAnOccupiedSquareToAnEmptyDestination() {
        click("E2");
        assertTrue(board.getCoordinate("E2").isSelectedSquare());

        click("E4");

        assertTrue(board.getCoordinate("E2").isSquareEmpty());
        assertEquals("P", board.getCoordinate("E4").getPieceCode());
        assertFalse(board.getCoordinate("E4").isSelectedSquare());
    }

    @Test
    void exchangesPiecesWhenAnOccupiedDestinationIsConfirmed() {
        AtomicReference<String> requestedMove = new AtomicReference<>();
        board.setSwapConfirmation((source, destination) -> {
            requestedMove.set(source + "-" + destination);
            return true;
        });

        click("A2");
        click("A1");

        assertEquals("A2-A1", requestedMove.get());
        assertEquals("R", board.getCoordinate("A2").getPieceCode());
        assertEquals("P", board.getCoordinate("A1").getPieceCode());
        assertFalse(board.getCoordinate("A2").isSelectedSquare());
    }

    @Test
    void leavesBothPiecesUnchangedWhenExchangeIsCancelled() {
        board.setSwapConfirmation((source, destination) -> false);

        click("A2");
        click("A1");

        assertEquals("P", board.getCoordinate("A2").getPieceCode());
        assertEquals("R", board.getCoordinate("A1").getPieceCode());
        assertFalse(board.getCoordinate("A2").isSelectedSquare());
    }

    @Test
    void clickingTheOriginTwiceCancelsTheMove() {
        click("B1");
        click("B1");

        assertEquals("N", board.getCoordinate("B1").getPieceCode());
        assertFalse(board.getCoordinate("B1").isSelectedSquare());
    }

    @Test
    void emptySquaresRemainSelectableForPositionEditing() {
        click("D4");

        assertTrue(board.getCoordinate("D4").isSelectedSquare());
    }

    private void click(String coordinate) {
        int column = coordinate.charAt(0) - 'A';
        int rank = Character.digit(coordinate.charAt(1), 10);
        int row = ChessBoard.BOARD_SIZE - rank;
        applet.mouseX = (int) (column * SQUARE_SIZE + SQUARE_SIZE / 2.0f);
        applet.mouseY = (int) (row * SQUARE_SIZE + SQUARE_SIZE / 2.0f);
        applet.mouseButton = PApplet.LEFT;
        board.handleMousePressed(0, 0);
    }

    private static final class StubApplet extends PApplet {

        @Override
        public PImage loadImage(String filename) {
            return new PImage(700, 120);
        }
    }
}
