package com.orios.javachessboard;

import com.orios.javachessboard.utils.ForsythEdwardsPos;
import java.util.HashMap;
import java.util.List;
import processing.core.PApplet;

/**
 *
 * @author obedrios
 */
public class ChessBoard {
    private PApplet pApplet;
    private HashMap<String, SquareBoard> boardCoordinatesMap;
    private SquareBoard[] chessBoard = new SquareBoard[64];
    private BoardImagePieces boardImagePieces;
    private String[] columns = {"A","B","C","D","E","F","G","H"};
    private float squareSize = 60;
    private boolean whiteToMove = true;
    private boolean whiteCanShortCastle = true;
    private boolean whiteCanLongCastle = true;
    private boolean blackCanShortCastle = true;
    private boolean blackCanLongCastle = true;
    private int currentMoveNumber = 0;
    //
    int xcount = 0;
    int ycount = 8;
    int scount = 0;
    //float[] boardOffset = {60,60};

    public ChessBoard(){
    }

    public ChessBoard(PApplet pApplet, float squareSize){
        this.pApplet = pApplet;
        this.squareSize = squareSize;
        this.boardCoordinatesMap = new HashMap<>();
        init();
    }

    private void init(){
        boardImagePieces = new BoardImagePieces(pApplet);
        boolean flag = true;
        for(int i = 0; i < 64; i++){
            chessBoard[i] = new SquareBoard(pApplet, boardImagePieces);
            chessBoard[i].setSquareSize(squareSize);
            chessBoard[i].setWhiteSquare(flag);
            chessBoard[i].setEmpty(true);
            chessBoard[i].setSquareIndex(i);
            chessBoard[i].setCoordinate(columns[xcount]+pApplet.str(ycount));
            boardCoordinatesMap.put(columns[xcount]+pApplet.str(ycount), chessBoard[i]);
            //chessBoard[i].setVisibleCoordinates(false);
            flag = !flag;
            xcount++;
            if(xcount > 7) {
                flag = !flag;
                xcount = 0;
                ycount = ycount - 1;
            }
        }
        //
        xcount = 0;
        ycount = 0;
    }

    public void setDefaultNewGamePosition(){
        /*
          //This is an initial concept using hardcoded positions, now we can use FEN Strings
            for(int i=0; i<8; i++){
                chessBoard[i].setEmpty(false);
                chessBoard[i].setWhitePiece(false);
                //
                chessBoard[i+8].setBoardPiece(BoardImagePieces.PAWN);
                chessBoard[i+8].setEmpty(false);
                chessBoard[i+8].setWhitePiece(false);
                //
                chessBoard[i+48].setBoardPiece(BoardImagePieces.PAWN);
                chessBoard[i+48].setEmpty(false);
                chessBoard[i+48].setWhitePiece(true);
                //
                chessBoard[i+56].setEmpty(false);
                chessBoard[i+56].setWhitePiece(true);
            }
            //
            chessBoard[56].setBoardPiece(BoardImagePieces.ROOK);
            chessBoard[63].setBoardPiece(BoardImagePieces.ROOK);
            chessBoard[0].setBoardPiece(BoardImagePieces.ROOK);
            chessBoard[7].setBoardPiece(BoardImagePieces.ROOK);
            //
            chessBoard[57].setBoardPiece(BoardImagePieces.KNIGHT);
            chessBoard[62].setBoardPiece(BoardImagePieces.KNIGHT);
            chessBoard[1].setBoardPiece(BoardImagePieces.KNIGHT);
            chessBoard[6].setBoardPiece(BoardImagePieces.KNIGHT);
            //
            chessBoard[58].setBoardPiece(BoardImagePieces.BISHOP);
            chessBoard[61].setBoardPiece(BoardImagePieces.BISHOP);
            chessBoard[2].setBoardPiece(BoardImagePieces.BISHOP);
            chessBoard[5].setBoardPiece(BoardImagePieces.BISHOP);
            //
            chessBoard[59].setBoardPiece(BoardImagePieces.QUEEN);
            chessBoard[60].setBoardPiece(BoardImagePieces.KING);
            chessBoard[3].setBoardPiece(BoardImagePieces.QUEEN);
            chessBoard[4].setBoardPiece(BoardImagePieces.KING);
        */
        ForsythEdwardsPos fenProcessor = new ForsythEdwardsPos();
        setPositionFromFENString(fenProcessor.getDefaultNewPosString());
    }

    public void setVisibleCoordinates(boolean visibleCoordinates){
        for(int i = 0; i < 64; i++)
          chessBoard[i].setVisibleCoordinates(visibleCoordinates);
    }

    public BoardImagePieces getBoardImagePieces(){
        return boardImagePieces;
    }

    public SquareBoard[] getChessBoard(){
        return chessBoard;
    }

    public SquareBoard getCoordinate(String coordinate){
        return boardCoordinatesMap.get(coordinate);
    }

    public SquareBoard getCoordinate(int i, int j){
        int index = 8*i + j; //using row-major addressing
        return chessBoard[index];
    }

    public SquareBoard[] getBoardRow(int row){
        SquareBoard[] boardRow = new SquareBoard[8];
        for(int i = 0; i < 8; ++i){
            boardRow[i] = getCoordinate(row, i);
        }
        //
        return boardRow;
    }

    public void setSelectedHightLightSquares(boolean selectedHightLightSquares){
        for(int i=0; i<64; i++ )chessBoard[i].setSelectedWithHighLight(selectedHightLightSquares);
    }


    public void setClearBoard(){
        for(int i=0; i<64; i++) chessBoard[i].setEmpty(true);
    }

    public void setPositionFromFENString(String fenPosition){
        //TODO: Pending validate Fen position processing
        ForsythEdwardsPos positionProcessor = new ForsythEdwardsPos(fenPosition);
        List<String[]> boardPosition = positionProcessor.getDecodedPositionFromFen();
        setClearBoard();
        int squareIndex = 0;
        //
        for(String[] decodedRow : boardPosition){
            for(String code : decodedRow){
                chessBoard[squareIndex].setBoardPiece(code);
                squareIndex++;
            }
        }
        //
        setWhiteToMove(positionProcessor.isWhiteToMove());
        setWhiteCanShortCastle(positionProcessor.isWhiteCanShortCastle());
        setWhiteCanLongCastle(positionProcessor.isWhiteCanLongCastle());
        setBlackCanShortCastle(positionProcessor.isBlackCanShortCastle());
        setBlackCanLongCastle(positionProcessor.isBlackCanLongCastle());
        setCurrentMoveNumber(positionProcessor.getCurrentMoveNumber());
    }

    public String getFENStringFromCurrentPosition(){
        //TODO: PENDING
        //String fenPosition = "r2q1rk1/pp2ppbp/2p2np1/6B1/3PP1b1/Q1P2N2/P4PPP/3RKB1R b K - 0 13";
        StringBuilder fenPosition = new StringBuilder();
        SquareBoard[] currentRow;
        String strCurrentCode = "";
        String strRow = "";
        int emptyCount = 0;
        //Iterate over the rows of boards
        for(int row = 0; row < 8; row++){
            currentRow = getBoardRow(row);
            for(SquareBoard square : currentRow){
                strCurrentCode = square.getPieceCode();
                if(strCurrentCode.equals("-")){
                    emptyCount++;
                } else {
                    strRow += emptyCount+strCurrentCode;
                    emptyCount = 0;
                }
            }
            // Avoid last "/" character
            if(row == 7) strRow = strRow+emptyCount;
            else strRow = strRow+emptyCount+"/";
            emptyCount = 0;
            fenPosition.append(strRow.replace("0",""));
            strRow = "";
        }
        // Complete FEN string
        String whiteToMove = "b";
        String canCastle = "-";
        if(isWhiteToMove()) whiteToMove = "w";
        fenPosition.append(" " + whiteToMove);
        if(!getBlackCanCastleFENCode().isEmpty() || !getWhiteCanCastleFENCode().isEmpty()) {
            canCastle = getWhiteCanCastleFENCode()+getBlackCanCastleFENCode();
        }
        fenPosition.append(" "+canCastle);
        fenPosition.append(" - "); //TODO: enpassant code pending
        fenPosition.append("0");
        fenPosition.append(" " + getCurrentMoveNumber());
        return fenPosition.toString() ;
    }

    public void move(String from, String to){
        SquareBoard squareFrom = getCoordinate(from);
        getCoordinate(to).setBoardPiece(squareFrom.getPieceCode());
        squareFrom.setEmpty(true);
    }


    public float getSquareSize(){
        return this.squareSize;
    }

    public boolean isWhiteToMove(){
        return whiteToMove;
    }

    public void setWhiteToMove(boolean whiteToMove){
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

    public int getCurrentMoveNumber() {
        return currentMoveNumber;
    }

    public void setCurrentMoveNumber(int currentMoveNumber) {
        this.currentMoveNumber = currentMoveNumber;
    }

    public String getWhiteCanCastleFENCode(){
        String code = "";
        if(whiteCanShortCastle){
            code += "K";
        }
        //
        if(whiteCanLongCastle){
            code += "Q";
        }
        return code;
    }

    public String getBlackCanCastleFENCode(){
        String code = "";
        if(blackCanShortCastle){
            code += "k";
        }
        //
        if(blackCanLongCastle){
            code += "q";
        }
        return code;
    }

    public void draw(float xpos, float ypos, boolean boardCoordinates){
        // Board Frame drawing
        float bframe_backsize = squareSize*8;
        float bframe_c2 = bframe_backsize*0.05f + bframe_backsize;
        float bframe_c1 = bframe_backsize*0.025f;
        pApplet.fill(0);
        pApplet.rect(xpos-bframe_c1, ypos-bframe_c1, bframe_c2, bframe_c2);
        // Plot Coordinates selection
        if(boardCoordinates) {
            for (int i = 0; i < 8; i++) {
                pApplet.fill(0);
                pApplet.text(columns[i], i * squareSize + xpos + squareSize / 2.0f,
                          ypos - bframe_backsize * 0.05f);
                pApplet.text(pApplet.str(8 - i), xpos - bframe_backsize * 0.08f,
                          i * squareSize + ypos + squareSize / 2);
            }
        }
        // Visualize when black or white plays
        if(isWhiteToMove()) pApplet.fill(255);
        else pApplet.fill(0);
        pApplet.ellipse(xpos - bframe_backsize*0.05f, ypos - bframe_backsize*0.05f,
                        squareSize/4,squareSize/4);
        // Draw square boards
        for(int i = 0; i < 64; i++) {
            if (xcount > 7) {
                xcount = 0;
                ycount++;
                if (ycount > 7) ycount = 0;
            }
            if (scount > 64) scount = 0;
            chessBoard[scount].draw(xcount * squareSize + xpos, ycount * squareSize + ypos);
            //System.out.println(chessBoard[scount].getCoordinate());
            xcount++;
            scount++;
        }
        //
        xcount = 0; ycount = 0; scount = 0;
    }

    @Override
    public String toString(){
        StringBuilder strChessBoard = new StringBuilder();
        SquareBoard[] boardRow = new SquareBoard[8];
        for(int row = 0; row < 8; row++){
            boardRow = getBoardRow(row);
            strChessBoard.append("[ ");
            for(SquareBoard currentSquare : boardRow) {
                strChessBoard.append(currentSquare.getPieceCode() + ", ");
            }
            strChessBoard.append("]\n");
        }
        return strChessBoard.toString();
    }
}

