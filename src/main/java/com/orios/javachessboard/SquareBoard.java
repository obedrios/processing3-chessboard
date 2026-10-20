package com.orios.javachessboard;

import processing.core.PApplet;
/**
 *
 * @author obedrios
 */
public class SquareBoard {

    private PApplet pApplet;
    private BoardImagePieces boardPieces;
    private boolean squareEmpty = true;
    private boolean whiteSquare = true;
    private boolean whitePiece = true;
    private boolean highLightedSquare = false;
    private boolean visibleCoordinates = false;
    private float squareSize = 110.0f;
    private float ratio = 0.0f;
    private float imgDefaultSize = 110.0f;
    private int boardPiece = BoardImagePieces.PAWN;
    private String coordinate = "";
    private int squareIndex = 0;
    private boolean selectedSquare = false;
    private boolean selectedWithHighLight = false;
    //
    private int[] highLightSquareColor = {0,255,0};
    private int[] darkSquaresColor     = {112,133,182};
    private int[] lightSquaresColor    = {238,238,238};

    public SquareBoard(){
    }

    public SquareBoard(PApplet pApplet, BoardImagePieces boardPieces){
        this.pApplet = pApplet;
        this.boardPieces = boardPieces;
    }

    public boolean isSquareEmpty(){
        return squareEmpty;
    }

    public void setEmpty(boolean empty){
        this.squareEmpty = empty;
    }

    public void setSquareSize(float squareSize){
        this.squareSize = squareSize;
    }

    public float getSquareSize(){
        return this.squareSize;
    }

    public boolean isWhiteSquare(){
        return this.whiteSquare;
    }

    public void setWhiteSquare(boolean isWhiteSquare){
        this.whiteSquare = isWhiteSquare;
    }

    public void setBoardPiece(int boardPiece){
        this.boardPiece = boardPiece;
        setEmpty(false);
    }

    public void setBoardPiece(String pieceCode){
        int pcode = boardPieces.getImageCodePieceFromStringCode(pieceCode);
        //
        if(pcode == 0) {
            setEmpty(true);
        } else {
           boolean isWhite = Character.isUpperCase(pieceCode.charAt(0));
           setWhitePiece(isWhite);
           setBoardPiece(pcode);
           setEmpty(false);
        }
    }

    public void setWhitePiece(boolean pieceColor){
        this.whitePiece = pieceColor;
    }

    public boolean isWhitePiece(){
        return this.whitePiece;
    }

    public int getBoardPiece(){
        return boardPiece;
    }

    public int getSquareIndex(){
        return squareIndex;
    }

    public void setSquareIndex(int index){
        this.squareIndex = index;
    }

    public void setCoordinate(String coordinate){
        this.coordinate = coordinate;
    }

    public String getCoordinate(){
        return coordinate;
    }

    public void setVisibleCoordinates(boolean visible){
        this.visibleCoordinates = visible;
    }

    public boolean isVisibleCoordinates(){
        return this.visibleCoordinates;
    }

    public void setLightSquaresColor(int[] lightSquaresColor){
        this.lightSquaresColor = lightSquaresColor;
    }

    public void setDarkSquaresColor(int[] darkSquaresColor){
        this.darkSquaresColor = darkSquaresColor;
    }

    public void setHighLightSquareColor(int[]highLightSquareColor){
        this.highLightSquareColor = highLightSquareColor;
    }

    public void setHighLightedSquare(boolean highLightedSquare){
        this.highLightedSquare = highLightedSquare;
    }

    public boolean isHighLightedSquare(){
       return this.highLightedSquare;
    }

    public boolean isSelectedWithHighLight(){
        return this.selectedWithHighLight;
    }

    public void setSelectedWithHighLight(boolean selectedWithHighLight){
        this.selectedWithHighLight = selectedWithHighLight;
    }

    public boolean isSelectedSquare(){
        return this.selectedSquare;
    }

    public void setSelectedSquare(boolean selectedSquare){
        this.selectedSquare = selectedSquare;
    }

    public String getPieceCode(){
        return boardPieces.getBoardPieceCode(getBoardPiece(), isWhitePiece(), isSquareEmpty());
    }

    public void draw(float posx, float posy){
        // Verify if the square board is white or black
        if(isWhiteSquare()){
            pApplet.fill(lightSquaresColor[0],lightSquaresColor[1],lightSquaresColor[2]);
            pApplet.stroke(lightSquaresColor[0],lightSquaresColor[1],lightSquaresColor[2]);
        } else {
            pApplet.fill(darkSquaresColor[0],darkSquaresColor[1],darkSquaresColor[2]);
            pApplet.stroke(darkSquaresColor[0],darkSquaresColor[1],darkSquaresColor[2]);
        }
        // Draw the square board
        pApplet.rect(posx, posy, getSquareSize(),getSquareSize());
        ratio = imgDefaultSize/getSquareSize();
        //
        // Highlight Square
        if(isHighLightedSquare()){
            pApplet.fill(highLightSquareColor[0], highLightSquareColor[1], highLightSquareColor[2],128);
            pApplet.rect(posx, posy, getSquareSize(), getSquareSize());
        }
        //
        // Draw the piece image if square not empty
        if(!isSquareEmpty()){
            pApplet.image(boardPieces.getImagePiece(boardPiece, whitePiece), posx,posy,
                       imgDefaultSize/ratio, imgDefaultSize/ratio);
        }
        // Draw index and coordinate for visual debug purposes
        if(isVisibleCoordinates()) {
            pApplet.fill(0, 0, 255);
            pApplet.text(getCoordinate()+" : "+pApplet.str(getSquareIndex()),
                         posx, posy + squareSize);
        }


        // Verify if mouse over square boards and detect mouse left press
        // TODO: How to select an specific action for selected square (highlight, setpiece, move, etc)
        // TODO: Listeners for mouse_events in board (observer)
        float midx = posx + squareSize/2.0f;
        float midy = posy + squareSize/2.0f;
        if(!isSelectedSquare()){
            if(isMouseOverSquare(posx, posy)){
                pApplet.fill(0,255,255);
                pApplet.ellipse(midx, midy, 10,10);
                pApplet.noFill();
                pApplet.stroke(0,255,255);
                pApplet.rect(posx, posy, squareSize-squareSize*0.015f, squareSize-squareSize*0.025f);
                //
                if(pApplet.mouseButton == pApplet.LEFT){
                    setSelectedSquare(true);
                    if(selectedWithHighLight) setHighLightedSquare(true);
                    pApplet.delay(300);
                }
            }
        } else {
            pApplet.fill(0,255,0);
            pApplet.ellipse(midx, midy, 10,10);
            pApplet.noFill();
            pApplet.stroke(0,255,0);
            pApplet.rect(posx, posy, squareSize-squareSize*0.015f, squareSize-squareSize*0.025f);
            //
            if(isMouseOverSquare(posx, posy)){
                if(pApplet.mouseButton == pApplet.LEFT){
                    setSelectedSquare(false);
                    if(selectedWithHighLight) setHighLightedSquare(false);
                    pApplet.delay(300);
                }
            }
        }

    }

    public boolean isMouseOverSquare(float posx, float posy){
       return  pApplet.mouseX  > posx  &&  pApplet.mouseX  < posx + squareSize &&
               pApplet.mouseY > posy  &&  pApplet.mouseY  < posy + squareSize;
    }

    public String getPieceCoordinate(){
        return getPieceCode() + ":" + getCoordinate();
    }

    @Override
    public String toString(){
        //TODO: Pending for a some representation such YAML or XML
        return null;
    }

}
