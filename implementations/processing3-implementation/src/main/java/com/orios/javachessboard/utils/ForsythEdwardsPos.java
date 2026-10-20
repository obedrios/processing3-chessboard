package com.orios.javachessboard.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Parses and stores the six fields of a Forsyth-Edwards Notation (FEN)
 * position.
 */
public final class ForsythEdwardsPos {

    public static final String DEFAULT_POSITION =
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private static final int BOARD_SIZE = 8;
    private static final Pattern PIECE_PLACEMENT = Pattern.compile("[prnbqkPRNBQK1-8]+");
    private static final Pattern EN_PASSANT_TARGET = Pattern.compile("-|[a-h][36]");

    private String fenPosition = DEFAULT_POSITION;
    private boolean whiteToMove = true;
    private String enPassant = "-";
    private boolean whiteCanShortCastle = true;
    private boolean whiteCanLongCastle = true;
    private boolean blackCanShortCastle = true;
    private boolean blackCanLongCastle = true;
    private int halfmoveClock;
    private int currentMoveNumber = 1;

    public ForsythEdwardsPos() {
    }

    public ForsythEdwardsPos(String fenPosition) {
        setFenPosition(fenPosition);
    }

    public void setFenPosition(String fenPosition) {
        this.fenPosition = requireText(fenPosition, "FEN position");
    }

    public String getFenPosition() {
        return fenPosition;
    }

    public void setWhiteToMove(boolean whiteToMove) {
        this.whiteToMove = whiteToMove;
    }

    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    public String getEnPassant() {
        return enPassant;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public void setCurrentMoveNumber(int moveNumber) {
        if (moveNumber < 1) {
            throw new IllegalArgumentException("Fullmove number must be at least 1");
        }
        this.currentMoveNumber = moveNumber;
    }

    public int getCurrentMoveNumber() {
        return currentMoveNumber;
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

    /**
     * Decodes a FEN position and updates this object's metadata only after the
     * complete value has been validated.
     *
     * @param fenPosition complete six-field FEN position
     * @return eight decoded ranks, from rank 8 to rank 1
     * @throws IllegalArgumentException when the FEN is malformed
     */
    public List<String[]> getDecodedPositionFromFen(String fenPosition) {
        String normalizedFen = requireText(fenPosition, "FEN position");
        String[] fields = normalizedFen.split("\\s+");
        if (fields.length != 6) {
            throw new IllegalArgumentException("A FEN position must contain exactly 6 fields");
        }

        String[] encodedRanks = fields[0].split("/", -1);
        if (encodedRanks.length != BOARD_SIZE) {
            throw new IllegalArgumentException("FEN piece placement must contain exactly 8 ranks");
        }

        List<String[]> decodedPosition = new ArrayList<>(BOARD_SIZE);
        for (String encodedRank : encodedRanks) {
            decodedPosition.add(decodeRow(encodedRank));
        }

        boolean parsedWhiteToMove = parseActiveColor(fields[1]);
        CastlingRights castlingRights = parseCastlingRights(fields[2]);
        String parsedEnPassant = parseEnPassant(fields[3], parsedWhiteToMove);
        int parsedHalfmoveClock = parseInteger(fields[4], "Halfmove clock", 0);
        int parsedFullmoveNumber = parseInteger(fields[5], "Fullmove number", 1);

        this.fenPosition = normalizedFen;
        this.whiteToMove = parsedWhiteToMove;
        this.whiteCanShortCastle = castlingRights.whiteShort();
        this.whiteCanLongCastle = castlingRights.whiteLong();
        this.blackCanShortCastle = castlingRights.blackShort();
        this.blackCanLongCastle = castlingRights.blackLong();
        this.enPassant = parsedEnPassant;
        this.halfmoveClock = parsedHalfmoveClock;
        this.currentMoveNumber = parsedFullmoveNumber;

        return List.copyOf(decodedPosition);
    }

    public List<String[]> getDecodedPositionFromFen() {
        return getDecodedPositionFromFen(fenPosition);
    }

    public String[] decodeRow(String codedRow) {
        String rank = requireText(codedRow, "FEN rank");
        if (!PIECE_PLACEMENT.matcher(rank).matches()) {
            throw new IllegalArgumentException("FEN rank contains an invalid piece code: " + rank);
        }

        List<String> decoded = new ArrayList<>(BOARD_SIZE);
        boolean previousWasDigit = false;
        for (char symbol : rank.toCharArray()) {
            if (Character.isDigit(symbol)) {
                if (previousWasDigit) {
                    throw new IllegalArgumentException("FEN rank contains adjacent empty counts: " + rank);
                }
                int emptySquares = Character.digit(symbol, 10);
                for (int i = 0; i < emptySquares; i++) {
                    decoded.add("-");
                }
            } else {
                decoded.add(String.valueOf(symbol));
            }
            previousWasDigit = Character.isDigit(symbol);

            if (decoded.size() > BOARD_SIZE) {
                throw new IllegalArgumentException("FEN rank expands to more than 8 squares: " + rank);
            }
        }

        if (decoded.size() != BOARD_SIZE) {
            throw new IllegalArgumentException("FEN rank must expand to exactly 8 squares: " + rank);
        }
        return decoded.toArray(String[]::new);
    }

    public List<String[]> getDefaultNewPosition() {
        return getDecodedPositionFromFen(DEFAULT_POSITION);
    }

    public String getDefaultNewPosString() {
        return DEFAULT_POSITION;
    }

    private static boolean parseActiveColor(String activeColor) {
        return switch (activeColor) {
            case "w" -> true;
            case "b" -> false;
            default -> throw new IllegalArgumentException("Active color must be 'w' or 'b'");
        };
    }

    private static CastlingRights parseCastlingRights(String value) {
        if ("-".equals(value)) {
            return new CastlingRights(false, false, false, false);
        }
        if (value.isEmpty() || value.length() > 4) {
            throw new IllegalArgumentException("Invalid castling availability: " + value);
        }

        String canonicalOrder = "KQkq";
        int previousIndex = -1;
        for (char symbol : value.toCharArray()) {
            int index = canonicalOrder.indexOf(symbol);
            if (index < 0 || index <= previousIndex) {
                throw new IllegalArgumentException("Invalid castling availability: " + value);
            }
            previousIndex = index;
        }

        return new CastlingRights(
                value.indexOf('K') >= 0,
                value.indexOf('Q') >= 0,
                value.indexOf('k') >= 0,
                value.indexOf('q') >= 0);
    }

    private static String parseEnPassant(String target, boolean whiteToMove) {
        if (!EN_PASSANT_TARGET.matcher(target).matches()) {
            throw new IllegalArgumentException("Invalid en-passant target square: " + target);
        }
        if (!"-".equals(target)) {
            char expectedRank = whiteToMove ? '6' : '3';
            if (target.charAt(1) != expectedRank) {
                throw new IllegalArgumentException(
                        "En-passant target is inconsistent with the active color: " + target);
            }
        }
        return target;
    }

    private static int parseInteger(String value, String fieldName, int minimum) {
        if (!value.matches("[0-9]+")) {
            throw new IllegalArgumentException(fieldName + " must be a non-negative integer");
        }
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < minimum) {
                throw new IllegalArgumentException(fieldName + " must be at least " + minimum);
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be an integer", exception);
        }
    }

    private static String requireText(String value, String fieldName) {
        String text = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return text;
    }

    private record CastlingRights(
            boolean whiteShort,
            boolean whiteLong,
            boolean blackShort,
            boolean blackLong) {
    }
}
