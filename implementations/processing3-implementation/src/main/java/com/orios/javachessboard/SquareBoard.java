package com.orios.javachessboard;

import java.util.Arrays;
import java.util.Objects;
import processing.core.PApplet;

/** A drawable square and the chess piece currently occupying it. */
public final class SquareBoard {

    private static final int COLOR_COMPONENTS = 3;
    private static final int[] DEFAULT_HIGHLIGHT_COLOR = {0, 255, 0};
    private static final int[] DEFAULT_DARK_COLOR = {112, 133, 182};
    private static final int[] DEFAULT_LIGHT_COLOR = {238, 238, 238};

    private final PApplet pApplet;
    private final BoardImagePieces boardPieces;

    private boolean squareEmpty = true;
    private boolean whiteSquare = true;
    private boolean whitePiece = true;
    private boolean highlightedSquare;
    private boolean visibleCoordinates;
    private float squareSize = 110.0f;
    private int boardPiece = BoardImagePieces.PAWN;
    private String coordinate = "";
    private int squareIndex;
    private boolean selectedSquare;
    private boolean selectedWithHighlight;
    private int[] highlightSquareColor = DEFAULT_HIGHLIGHT_COLOR.clone();
    private int[] darkSquaresColor = DEFAULT_DARK_COLOR.clone();
    private int[] lightSquaresColor = DEFAULT_LIGHT_COLOR.clone();

    public SquareBoard(PApplet pApplet, BoardImagePieces boardPieces) {
        this.pApplet = Objects.requireNonNull(pApplet, "pApplet must not be null");
        this.boardPieces = Objects.requireNonNull(boardPieces, "boardPieces must not be null");
    }

    public boolean isSquareEmpty() {
        return squareEmpty;
    }

    public void setEmpty(boolean empty) {
        squareEmpty = empty;
    }

    public void clearPiece() {
        squareEmpty = true;
    }

    public void setSquareSize(float squareSize) {
        if (!Float.isFinite(squareSize) || squareSize <= 0) {
            throw new IllegalArgumentException("Square size must be a positive finite number");
        }
        this.squareSize = squareSize;
    }

    public float getSquareSize() {
        return squareSize;
    }

    public boolean isWhiteSquare() {
        return whiteSquare;
    }

    public void setWhiteSquare(boolean whiteSquare) {
        this.whiteSquare = whiteSquare;
    }

    public void setBoardPiece(int boardPiece) {
        // Validation is centralized in the sprite/code lookup.
        boardPieces.getBoardPieceCode(boardPiece, true, false);
        this.boardPiece = boardPiece;
        squareEmpty = false;
    }

    public void setBoardPiece(String pieceCode) {
        if ("-".equals(pieceCode)) {
            clearPiece();
            return;
        }
        int imageCode = boardPieces.getImageCodePieceFromStringCode(pieceCode);
        if (imageCode == 0) {
            throw new IllegalArgumentException("Invalid chess piece code: " + pieceCode);
        }
        whitePiece = Character.isUpperCase(pieceCode.charAt(0));
        setBoardPiece(imageCode);
    }

    public void setWhitePiece(boolean whitePiece) {
        this.whitePiece = whitePiece;
    }

    public boolean isWhitePiece() {
        return whitePiece;
    }

    public int getBoardPiece() {
        return boardPiece;
    }

    public int getSquareIndex() {
        return squareIndex;
    }

    public void setSquareIndex(int index) {
        if (index < 0 || index >= ChessBoard.SQUARE_COUNT) {
            throw new IllegalArgumentException("Square index must be between 0 and 63");
        }
        squareIndex = index;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = Objects.requireNonNull(coordinate, "coordinate must not be null");
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setVisibleCoordinates(boolean visible) {
        visibleCoordinates = visible;
    }

    public boolean isVisibleCoordinates() {
        return visibleCoordinates;
    }

    public void setLightSquaresColor(int[] color) {
        lightSquaresColor = copyColor(color);
    }

    public void setDarkSquaresColor(int[] color) {
        darkSquaresColor = copyColor(color);
    }

    public void setHighLightSquareColor(int[] color) {
        highlightSquareColor = copyColor(color);
    }

    public void setHighLightedSquare(boolean highlighted) {
        highlightedSquare = highlighted;
    }

    public boolean isHighLightedSquare() {
        return highlightedSquare;
    }

    public boolean isSelectedWithHighLight() {
        return selectedWithHighlight;
    }

    public void setSelectedWithHighLight(boolean selectedWithHighlight) {
        this.selectedWithHighlight = selectedWithHighlight;
        if (!selectedWithHighlight) {
            highlightedSquare = false;
        } else if (selectedSquare) {
            highlightedSquare = true;
        }
    }

    public boolean isSelectedSquare() {
        return selectedSquare;
    }

    public void setSelectedSquare(boolean selectedSquare) {
        this.selectedSquare = selectedSquare;
        if (selectedWithHighlight) {
            highlightedSquare = selectedSquare;
        }
    }

    public void toggleSelected() {
        setSelectedSquare(!selectedSquare);
    }

    public void clearSelection() {
        setSelectedSquare(false);
    }

    public String getPieceCode() {
        return boardPieces.getBoardPieceCode(boardPiece, whitePiece, squareEmpty);
    }

    public void draw(float x, float y) {
        pApplet.pushStyle();
        int[] squareColor = whiteSquare ? lightSquaresColor : darkSquaresColor;
        pApplet.fill(squareColor[0], squareColor[1], squareColor[2]);
        pApplet.stroke(squareColor[0], squareColor[1], squareColor[2]);
        pApplet.rect(x, y, squareSize, squareSize);

        if (highlightedSquare) {
            pApplet.fill(highlightSquareColor[0], highlightSquareColor[1], highlightSquareColor[2], 128);
            pApplet.rect(x, y, squareSize, squareSize);
        }

        if (!squareEmpty) {
            pApplet.image(boardPieces.getImagePiece(boardPiece, whitePiece), x, y, squareSize, squareSize);
        }

        if (visibleCoordinates) {
            pApplet.fill(0, 0, 255);
            pApplet.text(coordinate + " : " + squareIndex, x, y + squareSize);
        }

        if (selectedSquare || isMouseOverSquare(x, y)) {
            int blue = selectedSquare ? 0 : 255;
            pApplet.fill(0, 255, blue);
            pApplet.ellipse(x + squareSize / 2.0f, y + squareSize / 2.0f, 10, 10);
            pApplet.noFill();
            pApplet.stroke(0, 255, blue);
            pApplet.rect(x, y, squareSize, squareSize);
        }
        pApplet.popStyle();
    }

    public boolean isMouseOverSquare(float x, float y) {
        return pApplet.mouseX >= x && pApplet.mouseX < x + squareSize
                && pApplet.mouseY >= y && pApplet.mouseY < y + squareSize;
    }

    public String getPieceCoordinate() {
        return getPieceCode() + ":" + coordinate;
    }

    @Override
    public String toString() {
        return getPieceCoordinate();
    }

    private static int[] copyColor(int[] color) {
        Objects.requireNonNull(color, "color must not be null");
        if (color.length != COLOR_COMPONENTS
                || Arrays.stream(color).anyMatch(component -> component < 0 || component > 255)) {
            throw new IllegalArgumentException("Color must contain three components between 0 and 255");
        }
        return color.clone();
    }
}
