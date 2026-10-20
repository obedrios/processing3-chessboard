package com.orios.demos;

import com.orios.javachessboard.ChessBoard;
import processing.core.PApplet;
import processing.core.PSurface;

/**
 *
 * @author obedrios
 */
public class ChessBoardDemoSketch extends PApplet {

    private ChessBoard chessBoard;

    public ChessBoardDemoSketch(){
    }


    @Override
    public void settings(){
        size(600,600);
    }

    @Override
    public void setup(){
        chessBoard = new ChessBoard(this, 60);
        chessBoard.setDefaultNewGamePosition();
    }

    @Override
    public void draw(){
        chessBoard.draw(60,60, true);
    }

    public ChessBoard getChessBoard(){
        return this.chessBoard;
    }

    public PSurface getInitSurface(){
        return this.initSurface();
    }

}