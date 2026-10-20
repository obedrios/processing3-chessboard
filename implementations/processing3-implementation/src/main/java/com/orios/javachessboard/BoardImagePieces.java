package com.orios.javachessboard;

import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PImage;

/**
 *
 * @author obedrios
 */
public class BoardImagePieces {

    private PApplet pApplet;
    private PImage img_white;
    private PImage img_black;
    private int offset = 110;

    public static int PAWN = 1;
    public static int KNIGHT = 111;
    public static int BISHOP = 221;
    public static int ROOK = 331;
    public static int QUEEN = 441;
    public static int KING = 551;

    private HashMap<Integer, String> boardPieceCodeMap;

    public BoardImagePieces() {
        //pApplet = new PApplet();
    }

    public BoardImagePieces(PApplet pApplet) {
        this.pApplet = pApplet;
        this.boardPieceCodeMap = new HashMap<>();
        init();
    }

    private void init() {
        img_white = pApplet.loadImage("images/merida_white.png");
        img_black = pApplet.loadImage("images/merida_black.png");
        //img_white = pApplet.loadImage("resources/images/alpha_white.png");
        //img_black = pApplet.loadImage("resources/images/alpha_black.png");
        //
        boardPieceCodeMap.put(PAWN, "p");
        boardPieceCodeMap.put(KNIGHT, "n");
        boardPieceCodeMap.put(BISHOP, "b");
        boardPieceCodeMap.put(ROOK, "r");
        boardPieceCodeMap.put(QUEEN, "q");
        boardPieceCodeMap.put(KING, "k");
    }

    public PImage getImagePiece(int pcount, boolean iswhite) {
        if (iswhite) {
            return img_white.get(pcount, 1, offset - 1, offset - 1);
        }
        return img_black.get(pcount, 1, offset - 1, offset - 1);
    }

    public String getBoardPieceCode(int boardPiece, boolean iswhite, boolean isEmpty) {
        String pieceCode = boardPieceCodeMap.get(boardPiece);
        if (isEmpty) {
            return "-";
        }
        if (iswhite) {
            return pieceCode.toUpperCase();
        } else {
            return pieceCode.toLowerCase();
        }
    }

    public int getImageCodePieceFromStringCode(String pcode) {
        switch (pcode) {
            case "p": return PAWN;
            case "P": return PAWN;
            case "n": return KNIGHT;
            case "N": return KNIGHT;
            case "b": return BISHOP;
            case "B": return BISHOP;
            case "r": return ROOK;
            case "R": return ROOK;
            case "q": return QUEEN;
            case "Q": return QUEEN;
            case "k": return KING;
            case "K": return KING;
            default:  return 0;
        }
    }

}
