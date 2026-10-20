package com.orios.demos;

import com.orios.javachessboard.ChessBoard;
import processing.core.PApplet;
import processing.core.PSurface;

/**
 *
 * @author obedrios
 */
public class ChessBoardDemoSketch extends PApplet {

    public static final int CANVAS_SIZE = 600;
    public static final float BOARD_OFFSET = 60.0f;
    public static final float SQUARE_SIZE = 60.0f;

    private ChessBoard chessBoard;

    @Override
    public void settings() {
        size(CANVAS_SIZE, CANVAS_SIZE);
    }

    @Override
    public void setup() {
        chessBoard = new ChessBoard(this, SQUARE_SIZE);
        chessBoard.setDefaultNewGamePosition();
    }

    @Override
    public void draw() {
        background(204);
        chessBoard.draw(BOARD_OFFSET, BOARD_OFFSET, true);
    }

    @Override
    public void mousePressed() {
        chessBoard.handleMousePressed(BOARD_OFFSET, BOARD_OFFSET);
    }

    public ChessBoard getChessBoard() {
        return chessBoard;
    }

    public PSurface getInitSurface() {
        return initSurface();
    }
}
