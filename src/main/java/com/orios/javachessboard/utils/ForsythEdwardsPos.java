package com.orios.javachessboard.utils;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author obedrios
 */
public class ForsythEdwardsPos {

    private String defaultpos = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private String fenPosition = "";
    private boolean whiteToMove = true;
    private String enpassant = "-";
    //private String possibleCastles = "KQkq";
    private boolean whiteCanShortCastle = true;
    private boolean whiteCanLongCastle = true;
    private boolean blackCanShortCastle = true;
    private boolean blackCanLongCastle = true;
    private int currentMoveNumber = 0;


    public ForsythEdwardsPos(){
    }

    public ForsythEdwardsPos(String fenPosition){
        this.fenPosition = fenPosition;
    }

    public void setFenPosition(String fenPosition){
        this.fenPosition = fenPosition;
    }

    public String getFenPosition(){
        return fenPosition;
    }

    public void setWhiteToMove(boolean whiteToMove){
        this.whiteToMove = whiteToMove;
    }

    public boolean isWhiteToMove(){
        return this.whiteToMove;
    }

    public void setCurrentMoveNumber(int moveNumber){
        this.currentMoveNumber = moveNumber;
    }

    public int getCurrentMoveNumber(){
        return this.currentMoveNumber;
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

    public List<String[]> getDecodedPositionFromFen(String fenPosition){
        String[] boardStateCoded = fenPosition.split(" ");
        String[] positionCode = boardStateCoded[0].split("/");
        List<String[]> boardPosition = new LinkedList<>();
        //To Move state
        if(boardStateCoded[1].equals("b")) setWhiteToMove(false);
        setCurrentMoveNumber(Integer.parseInt(boardStateCoded[5]));
        //Decode castle posibilities
        switch (boardStateCoded[2]){
            case "q":
                setWhiteCanShortCastle(false);
                setWhiteCanLongCastle(false);
                setBlackCanShortCastle(false);
                setBlackCanLongCastle(true);
                break;
            case "k":
                setWhiteCanShortCastle(false);
                setWhiteCanLongCastle(false);
                setBlackCanShortCastle(true);
                setBlackCanLongCastle(false);
                break;
            case "kq":
                setWhiteCanShortCastle(false);
                setWhiteCanLongCastle(false);
                setBlackCanShortCastle(true);
                setBlackCanLongCastle(true);
                break;
            case "Q":
                setWhiteCanShortCastle(false);
                setWhiteCanLongCastle(true);
                setBlackCanShortCastle(false);
                setBlackCanLongCastle(false);
                break;
            case "Qq":
                setWhiteCanShortCastle(false);
                setWhiteCanLongCastle(true);
                setBlackCanShortCastle(false);
                setBlackCanLongCastle(true);
                break;
            case "Qk":
                setWhiteCanShortCastle(false);
                setWhiteCanLongCastle(true);
                setBlackCanShortCastle(true);
                setBlackCanLongCastle(false);
                break;
            case "Qkq":
                setWhiteCanShortCastle(false);
                setWhiteCanLongCastle(true);
                setBlackCanShortCastle(true);
                setBlackCanLongCastle(true);
                break;
            case "K":
                setWhiteCanShortCastle(true);
                setWhiteCanLongCastle(false);
                setBlackCanShortCastle(false);
                setBlackCanLongCastle(false);
                break;
            case "Kq":
                setWhiteCanShortCastle(true);
                setWhiteCanLongCastle(false);
                setBlackCanShortCastle(false);
                setBlackCanLongCastle(true);
                break;
            case "Kk":
                setWhiteCanShortCastle(true);
                setWhiteCanLongCastle(false);
                setBlackCanShortCastle(true);
                setBlackCanLongCastle(false);
                break;
            case "Kkq":
                setWhiteCanShortCastle(true);
                setWhiteCanLongCastle(false);
                setBlackCanShortCastle(true);
                setBlackCanLongCastle(true);
                break;
            case "KQ":
                setWhiteCanShortCastle(true);
                setWhiteCanLongCastle(true);
                setBlackCanShortCastle(false);
                setBlackCanLongCastle(false);
                break;
            case "KQq":
                setWhiteCanShortCastle(true);
                setWhiteCanLongCastle(true);
                setBlackCanShortCastle(false);
                setBlackCanLongCastle(true);
                break;
            case "KQk":
                setWhiteCanShortCastle(true);
                setWhiteCanLongCastle(true);
                setBlackCanShortCastle(true);
                setBlackCanLongCastle(false);
                break;
            case "KQkq":
                setWhiteCanShortCastle(true);
                setWhiteCanLongCastle(true);
                setBlackCanShortCastle(true);
                setBlackCanLongCastle(true);
                break;
            default:
                setWhiteCanShortCastle(false);
                setWhiteCanLongCastle(false);
                setBlackCanShortCastle(false);
                setBlackCanLongCastle(false);
        }
        //TODO: Set enpassant propierties
        //
        for(String codedRow : positionCode){
            //System.out.println(Arrays.toString(decodeRow(codedRow)));
            boardPosition.add(decodeRow(codedRow));
        }
        //
        setFenPosition(fenPosition);
        return boardPosition;
    }

    public List<String[]> getDecodedPositionFromFen(){
        return getDecodedPositionFromFen(fenPosition);
    }

    public String[] decodeRow(String codedRow){
        int rowcount = 0;
        String[]  decodedRow = new String[8];
        for(char pos : codedRow.toCharArray()){
            if(Character.isDigit(pos)){
                int steps = Character.getNumericValue(pos);
                for(int i = 0; i < steps; i++) {
                    decodedRow[rowcount] = "-";
                    rowcount++;
                }
            } else {
                decodedRow[rowcount] = Character.toString(pos);
                rowcount++;
            }
        }
        return  decodedRow;
    }

    public List<String[]> getDefaultNewPosition(){
        return  getDecodedPositionFromFen(defaultpos);
    }

    public String getDefaultNewPosString(){
        return defaultpos;
    }

}
