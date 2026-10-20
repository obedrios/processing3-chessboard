package com.orios.javachessboard;

import com.orios.javachessboard.utils.ForsythEdwardsPos;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import processing.core.PApplet;

/** A Processing-backed, editable representation of a chessboard. */
public final class ChessBoard {

    public static final int BOARD_SIZE = 8;
    public static final int SQUARE_COUNT = BOARD_SIZE * BOARD_SIZE;

    private static final String[] COLUMNS = {"A", "B", "C", "D", "E", "F", "G", "H"};

    private final PApplet pApplet;
    private final Map<String, SquareBoard> boardCoordinates = new HashMap<>();
    private final SquareBoard[] squares = new SquareBoard[SQUARE_COUNT];
    private final BoardImagePieces boardImagePieces;
    private final float squareSize;

    private boolean whiteToMove = true;
    private boolean whiteCanShortCastle = true;
    private boolean whiteCanLongCastle = true;
    private boolean blackCanShortCastle = true;
    private boolean blackCanLongCastle = true;
    private boolean selectionEnabled = true;
    private String enPassantTarget = "-";
    private int halfmoveClock;
    private int currentMoveNumber = 1;

    public ChessBoard(PApplet pApplet, float squareSize) {
        this.pApplet = Objects.requireNonNull(pApplet, "pApplet must not be null");
        if (!Float.isFinite(squareSize) || squareSize <= 0) {
            throw new IllegalArgumentException("Square size must be a positive finite number");
        }
        this.squareSize = squareSize;
        this.boardImagePieces = new BoardImagePieces(pApplet);
        initializeSquares();
    }

    private void initializeSquares() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                int index = row * BOARD_SIZE + column;
                String coordinate = COLUMNS[column] + (BOARD_SIZE - row);
                SquareBoard square = new SquareBoard(pApplet, boardImagePieces);
                square.setSquareSize(squareSize);
                square.setWhiteSquare((row + column) % 2 == 0);
                square.setSquareIndex(index);
                square.setCoordinate(coordinate);
                squares[index] = square;
                boardCoordinates.put(coordinate, square);
            }
        }
    }

    public void setDefaultNewGamePosition() {
        setPositionFromFENString(ForsythEdwardsPos.DEFAULT_POSITION);
    }

    public void setVisibleCoordinates(boolean visibleCoordinates) {
        for (SquareBoard square : squares) {
            square.setVisibleCoordinates(visibleCoordinates);
        }
    }

    public BoardImagePieces getBoardImagePieces() {
        return boardImagePieces;
    }

    /**
     * Returns a shallow copy so callers can edit squares but cannot reorder the
     * board's internal storage.
     */
    public SquareBoard[] getChessBoard() {
        return squares.clone();
    }

    public SquareBoard getCoordinate(String coordinate) {
        String normalized = Objects.requireNonNull(coordinate, "coordinate must not be null")
                .trim()
                .toUpperCase(Locale.ROOT);
        SquareBoard square = boardCoordinates.get(normalized);
        if (square == null) {
            throw new IllegalArgumentException("Invalid board coordinate: " + coordinate);
        }
        return square;
    }

    public SquareBoard getCoordinate(int row, int column) {
        if (row < 0 || row >= BOARD_SIZE || column < 0 || column >= BOARD_SIZE) {
            throw new IndexOutOfBoundsException("Board row and column must be between 0 and 7");
        }
        return squares[row * BOARD_SIZE + column];
    }

    public SquareBoard[] getBoardRow(int row) {
        if (row < 0 || row >= BOARD_SIZE) {
            throw new IndexOutOfBoundsException("Board row must be between 0 and 7");
        }
        SquareBoard[] boardRow = new SquareBoard[BOARD_SIZE];
        System.arraycopy(squares, row * BOARD_SIZE, boardRow, 0, BOARD_SIZE);
        return boardRow;
    }

    public void setSelectedHighlightSquares(boolean selectedHighlightSquares) {
        for (SquareBoard square : squares) {
            square.setSelectedWithHighLight(selectedHighlightSquares);
        }
    }

    /** @deprecated Use {@link #setSelectedHighlightSquares(boolean)}. */
    @Deprecated(forRemoval = false)
    public void setSelectedHightLightSquares(boolean selectedHighlightSquares) {
        setSelectedHighlightSquares(selectedHighlightSquares);
    }

    public void clearSelection() {
        for (SquareBoard square : squares) {
            square.clearSelection();
        }
    }

    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }

    public void setSelectionEnabled(boolean selectionEnabled) {
        this.selectionEnabled = selectionEnabled;
        if (!selectionEnabled) {
            clearSelection();
        }
    }

    public void setClearBoard() {
        for (SquareBoard square : squares) {
            square.clearPiece();
            square.clearSelection();
        }
        whiteCanShortCastle = false;
        whiteCanLongCastle = false;
        blackCanShortCastle = false;
        blackCanLongCastle = false;
        enPassantTarget = "-";
        halfmoveClock = 0;
        currentMoveNumber = 1;
    }

    public void setPositionFromFENString(String fenPosition) {
        ForsythEdwardsPos parser = new ForsythEdwardsPos(fenPosition);
        List<String[]> boardPosition = parser.getDecodedPositionFromFen();

        int squareIndex = 0;
        for (String[] decodedRow : boardPosition) {
            for (String code : decodedRow) {
                squares[squareIndex++].setBoardPiece(code);
            }
        }
        clearSelection();

        whiteToMove = parser.isWhiteToMove();
        whiteCanShortCastle = parser.isWhiteCanShortCastle();
        whiteCanLongCastle = parser.isWhiteCanLongCastle();
        blackCanShortCastle = parser.isBlackCanShortCastle();
        blackCanLongCastle = parser.isBlackCanLongCastle();
        enPassantTarget = parser.getEnPassant();
        halfmoveClock = parser.getHalfmoveClock();
        currentMoveNumber = parser.getCurrentMoveNumber();
    }

    public String getFENStringFromCurrentPosition() {
        StringBuilder fen = new StringBuilder();
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (row > 0) {
                fen.append('/');
            }
            appendFenRow(fen, getBoardRow(row));
        }

        String castling = getWhiteCanCastleFENCode() + getBlackCanCastleFENCode();
        fen.append(whiteToMove ? " w " : " b ")
                .append(castling.isEmpty() ? "-" : castling)
                .append(' ')
                .append(enPassantTarget)
                .append(' ')
                .append(halfmoveClock)
                .append(' ')
                .append(currentMoveNumber);
        return fen.toString();
    }

    private static void appendFenRow(StringBuilder fen, SquareBoard[] row) {
        int emptySquares = 0;
        for (SquareBoard square : row) {
            if (square.isSquareEmpty()) {
                emptySquares++;
                continue;
            }
            if (emptySquares > 0) {
                fen.append(emptySquares);
                emptySquares = 0;
            }
            fen.append(square.getPieceCode());
        }
        if (emptySquares > 0) {
            fen.append(emptySquares);
        }
    }

    public void move(String from, String to) {
        SquareBoard source = getCoordinate(from);
        if (source.isSquareEmpty()) {
            throw new IllegalArgumentException("Cannot move from an empty square: " + from);
        }
        SquareBoard destination = getCoordinate(to);
        destination.setBoardPiece(source.getPieceCode());
        source.clearPiece();
        clearSelection();
    }

    public float getSquareSize() {
        return squareSize;
    }

    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    public void setWhiteToMove(boolean whiteToMove) {
        this.whiteToMove = whiteToMove;
    }

    public boolean isWhiteCanShortCastle() {
        return whiteCanShortCastle;
    }

    public void setWhiteCanShortCastle(boolean whiteCanShortCastle) {
        this.whiteCanShortCastle = whiteCanShortCastle;
    }

    public boolean isWhiteCanLongCastle() {
        return whiteCanLongCastle;
    }

    public void setWhiteCanLongCastle(boolean whiteCanLongCastle) {
        this.whiteCanLongCastle = whiteCanLongCastle;
    }

    public boolean isBlackCanShortCastle() {
        return blackCanShortCastle;
    }

    public void setBlackCanShortCastle(boolean blackCanShortCastle) {
        this.blackCanShortCastle = blackCanShortCastle;
    }

    public boolean isBlackCanLongCastle() {
        return blackCanLongCastle;
    }

    public void setBlackCanLongCastle(boolean blackCanLongCastle) {
        this.blackCanLongCastle = blackCanLongCastle;
    }

    public String getEnPassantTarget() {
        return enPassantTarget;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public int getCurrentMoveNumber() {
        return currentMoveNumber;
    }

    public void setCurrentMoveNumber(int currentMoveNumber) {
        if (currentMoveNumber < 1) {
            throw new IllegalArgumentException("Fullmove number must be at least 1");
        }
        this.currentMoveNumber = currentMoveNumber;
    }

    public String getWhiteCanCastleFENCode() {
        return (whiteCanShortCastle ? "K" : "") + (whiteCanLongCastle ? "Q" : "");
    }

    public String getBlackCanCastleFENCode() {
        return (blackCanShortCastle ? "k" : "") + (blackCanLongCastle ? "q" : "");
    }

    public void handleMousePressed(float boardX, float boardY) {
        if (!selectionEnabled || pApplet.mouseButton != PApplet.LEFT) {
            return;
        }
        float relativeX = pApplet.mouseX - boardX;
        float relativeY = pApplet.mouseY - boardY;
        float boardPixels = squareSize * BOARD_SIZE;
        if (relativeX >= 0 && relativeX < boardPixels && relativeY >= 0 && relativeY < boardPixels) {
            int column = (int) (relativeX / squareSize);
            int row = (int) (relativeY / squareSize);
            getCoordinate(row, column).toggleSelected();
        }
    }

    public void draw(float x, float y, boolean boardCoordinates) {
        float boardPixels = squareSize * BOARD_SIZE;
        float framePadding = boardPixels * 0.025f;

        pApplet.pushStyle();
        pApplet.fill(0);
        pApplet.rect(x - framePadding, y - framePadding,
                boardPixels + framePadding * 2, boardPixels + framePadding * 2);

        if (boardCoordinates) {
            for (int index = 0; index < BOARD_SIZE; index++) {
                pApplet.fill(0);
                pApplet.text(COLUMNS[index], index * squareSize + x + squareSize / 2.0f,
                        y - framePadding * 1.5f);
                pApplet.text(Integer.toString(BOARD_SIZE - index), x - framePadding * 2.5f,
                        index * squareSize + y + squareSize / 2.0f);
            }
        }

        pApplet.fill(whiteToMove ? 255 : 0);
        pApplet.ellipse(x - framePadding * 2, y - framePadding * 2,
                squareSize / 4.0f, squareSize / 4.0f);

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                getCoordinate(row, column).draw(
                        column * squareSize + x,
                        row * squareSize + y);
            }
        }
        pApplet.popStyle();
    }

    @Override
    public String toString() {
        StringBuilder board = new StringBuilder();
        for (int row = 0; row < BOARD_SIZE; row++) {
            board.append("[ ");
            for (int column = 0; column < BOARD_SIZE; column++) {
                if (column > 0) {
                    board.append(", ");
                }
                board.append(getCoordinate(row, column).getPieceCode());
            }
            board.append(" ]").append(System.lineSeparator());
        }
        return board.toString();
    }
}
