package com.orios.javachessboard;

import java.util.Map;
import java.util.Objects;
import processing.core.PApplet;
import processing.core.PImage;

/** Loads and extracts chess-piece sprites from the bundled image sheets. */
public final class BoardImagePieces {

    public static final int PAWN = 1;
    public static final int KNIGHT = 111;
    public static final int BISHOP = 221;
    public static final int ROOK = 331;
    public static final int QUEEN = 441;
    public static final int KING = 551;

    private static final int SPRITE_SIZE = 109;
    private static final int SPRITE_Y = 1;
    private static final Map<Integer, String> PIECE_CODES = Map.of(
            PAWN, "p",
            KNIGHT, "n",
            BISHOP, "b",
            ROOK, "r",
            QUEEN, "q",
            KING, "k");

    private final PImage whitePieces;
    private final PImage blackPieces;

    public BoardImagePieces(PApplet pApplet) {
        PApplet applet = Objects.requireNonNull(pApplet, "pApplet must not be null");
        whitePieces = requireImage(applet.loadImage("images/merida_white.png"), "white pieces");
        blackPieces = requireImage(applet.loadImage("images/merida_black.png"), "black pieces");
    }

    public PImage getImagePiece(int piece, boolean white) {
        if (!PIECE_CODES.containsKey(piece)) {
            throw new IllegalArgumentException("Unknown chess piece image code: " + piece);
        }
        PImage spriteSheet = white ? whitePieces : blackPieces;
        if (piece + SPRITE_SIZE > spriteSheet.width || SPRITE_Y + SPRITE_SIZE > spriteSheet.height) {
            throw new IllegalStateException("Chess piece sprite sheet has unexpected dimensions");
        }
        return spriteSheet.get(piece, SPRITE_Y, SPRITE_SIZE, SPRITE_SIZE);
    }

    public String getBoardPieceCode(int boardPiece, boolean white, boolean empty) {
        if (empty) {
            return "-";
        }
        String pieceCode = PIECE_CODES.get(boardPiece);
        if (pieceCode == null) {
            throw new IllegalArgumentException("Unknown chess piece image code: " + boardPiece);
        }
        return white ? pieceCode.toUpperCase() : pieceCode;
    }

    public int getImageCodePieceFromStringCode(String pieceCode) {
        if (pieceCode == null || pieceCode.length() != 1) {
            return 0;
        }
        return switch (Character.toLowerCase(pieceCode.charAt(0))) {
            case 'p' -> PAWN;
            case 'n' -> KNIGHT;
            case 'b' -> BISHOP;
            case 'r' -> ROOK;
            case 'q' -> QUEEN;
            case 'k' -> KING;
            default -> 0;
        };
    }

    private static PImage requireImage(PImage image, String description) {
        if (image == null) {
            throw new IllegalStateException("Unable to load the sprite sheet for " + description);
        }
        return image;
    }
}
