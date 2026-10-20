package com.orios.demos;

import com.orios.javachessboard.ChessBoard;
import com.orios.javachessboard.PieceSwapConfirmation;
import java.util.Objects;
import processing.core.PApplet;
import processing.core.PSurface;

/**
 *
 * @author obedrios
 */
public class ChessBoardDemoSketch extends PApplet {

    public static final int CANVAS_SIZE = 600;
    public static final float SQUARE_SIZE = 60.0f;

    private ChessBoard chessBoard;
    private PieceSwapConfirmation swapConfirmation = (source, destination) -> false;
    private BoardGeometry geometry = BoardGeometry.fit(CANVAS_SIZE, CANVAS_SIZE);

    @Override
    public void settings() {
        size(CANVAS_SIZE, CANVAS_SIZE);
    }

    @Override
    public void setup() {
        surface.setResizable(true);
        chessBoard = new ChessBoard(this, SQUARE_SIZE);
        chessBoard.setSwapConfirmation(swapConfirmation);
        chessBoard.setDefaultNewGamePosition();
    }

    @Override
    public void draw() {
        background(204);
        geometry = BoardGeometry.fit(Math.max(width, 1), Math.max(height, 1));
        chessBoard.setSquareSize(geometry.squareSize());
        chessBoard.draw(geometry.x(), geometry.y(), true);
    }

    @Override
    public void mousePressed() {
        BoardGeometry current = geometry;
        chessBoard.handleMousePressed(current.x(), current.y());
    }

    public ChessBoard getChessBoard() {
        return chessBoard;
    }

    public void setSwapConfirmation(PieceSwapConfirmation swapConfirmation) {
        this.swapConfirmation = Objects.requireNonNull(
                swapConfirmation, "swapConfirmation must not be null");
        if (chessBoard != null) {
            chessBoard.setSwapConfirmation(swapConfirmation);
        }
    }

    public PSurface getInitSurface() {
        return initSurface();
    }
}
