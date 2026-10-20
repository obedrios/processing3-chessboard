package com.orios.demos;

import com.orios.javachessboard.BoardImagePieces;
import com.orios.javachessboard.ChessBoard;
import com.orios.javachessboard.SquareBoard;
import com.orios.javachessboard.utils.ForsythEdwardsPos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import processing.awt.PSurfaceAWT;
import processing.core.PImage;
import processing.core.PSurface;

/**
 *
 * @author obedrios
 */
public class ChessBoardDemoFrame  extends JFrame {

    private final String sketchPath = Path.of("").toAbsolutePath().normalize().toString();
    private ChessBoardDemoSketch chessBoardSketch;
    private PSurface ps;
    private PSurfaceAWT.SmoothCanvas smoothCanvas;
    private BoardImagePieces pieceImages;

    private JTextField textFieldStrFen;
    private JButton buttonSetFenPos;
    private JButton buttonGetFenStr;
    private JButton buttonClearBoard;
    private JButton buttonSetDefaultPos;
    private JButton buttonGetTextBoard;
    private JButton buttonClearSquareSel;

    private JButton buttonKingPiece;
    private JButton buttonQueenPiece;
    private JButton buttonRookPiece;
    private JButton buttonKnightPiece;
    private JButton buttonBishopPiece;
    private JButton buttonPawnPiece;

    private JCheckBox checkBoxWhiteShortCastle;
    private JCheckBox checkBoxWhiteLongCastle;
    private JCheckBox checkBoxBlackShortCastle;
    private JCheckBox checkBoxBlackLongCastle;

    private JCheckBox checkBoxWhiteToMove;
    private JCheckBox checkBoxWhitePiece;

    private JTextArea textAreaOutput;

    private JCheckBox checkBoxVisibleCoords;
    private JCheckBox checkBoxHighlightCoords;
    private JCheckBox checkBoxEnableBoardSelection;
    private JButton buttonAbout;

    public ChessBoardDemoFrame(){
        initWidgets();
        init();
        configWidgetActions();
    }


    private void init(){
        // Instance Sketch and configure
        chessBoardSketch = new ChessBoardDemoSketch();
        chessBoardSketch.sketchPath(sketchPath);
        ps = chessBoardSketch.getInitSurface();
        ps.setSize(ChessBoardDemoSketch.CANVAS_SIZE, ChessBoardDemoSketch.CANVAS_SIZE);

        //Get the SmoothCanvas that holds the PSurface
        smoothCanvas = (PSurfaceAWT.SmoothCanvas)ps.getNative();
        Frame processingFrame = smoothCanvas.getFrame();
        processingFrame.remove(smoothCanvas);
        processingFrame.dispose();
        smoothCanvas.setPreferredSize(new Dimension(
                ChessBoardDemoSketch.CANVAS_SIZE,
                ChessBoardDemoSketch.CANVAS_SIZE));
        pieceImages = new BoardImagePieces(chessBoardSketch);

        // Configure frame
        JPanel contentPanel = new JPanel();
        setContentPane(contentPanel);
        contentPanel.setLayout(new BorderLayout());

        //Add canvas as a component
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new BorderLayout());
        boardPanel.add(smoothCanvas, BorderLayout.CENTER);
        //
        JPanel loadPositionPanel = new JPanel();
        loadPositionPanel.setLayout(new BorderLayout());
        loadPositionPanel.add(new JLabel("Position FEN String:"), BorderLayout.WEST);
        loadPositionPanel.add(textFieldStrFen, BorderLayout.CENTER);
        JPanel loadPositionButtonsPanel = new JPanel();
        loadPositionButtonsPanel.add(buttonSetFenPos, BorderLayout.EAST);
        loadPositionButtonsPanel.add(buttonGetFenStr, BorderLayout.WEST);
        loadPositionPanel.add(loadPositionButtonsPanel, BorderLayout.EAST);
        loadPositionPanel.setBorder(BorderFactory.createTitledBorder("Positions Loading"));
        boardPanel.add(loadPositionPanel, BorderLayout.NORTH);
        //
        JPanel boardActionsPanel = new JPanel();
        boardActionsPanel.setBorder(BorderFactory.createTitledBorder("Board Actions"));
        boardActionsPanel.setLayout(new GridLayout(1,5));
        boardActionsPanel.add(buttonClearBoard);
        boardActionsPanel.add(buttonClearSquareSel);
        boardActionsPanel.add(buttonSetDefaultPos);
        boardActionsPanel.add(buttonGetTextBoard);
        boardActionsPanel.add(buttonAbout);
        boardPanel.add(boardActionsPanel, BorderLayout.SOUTH);
        contentPanel.add(boardPanel, BorderLayout.CENTER);
        //
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Board Options"));
        optionsPanel.setLayout(new GridLayout(4,1));

        JPanel castlePanel = new JPanel();
        castlePanel.setBorder(BorderFactory.createTitledBorder("Castle"));
        castlePanel.setLayout(new GridLayout(4,1));
        castlePanel.add(checkBoxWhiteShortCastle);
        castlePanel.add(checkBoxWhiteLongCastle);
        castlePanel.add(checkBoxBlackShortCastle);
        castlePanel.add(checkBoxBlackLongCastle);

        JPanel pieceSelection =  new JPanel();
        pieceSelection.setBorder(BorderFactory.createTitledBorder("Position Setup"));
        pieceSelection.setLayout(new GridLayout(3,1));
        JPanel pieceButtonsPanel = new JPanel();
        pieceButtonsPanel.setLayout(new GridLayout(1,6));
        pieceButtonsPanel.add(buttonKingPiece);
        pieceButtonsPanel.add(buttonQueenPiece);
        pieceButtonsPanel.add(buttonRookPiece);
        pieceButtonsPanel.add(buttonKnightPiece);
        pieceButtonsPanel.add(buttonBishopPiece);
        pieceButtonsPanel.add(buttonPawnPiece);
        pieceSelection.add(pieceButtonsPanel);
        pieceSelection.add(checkBoxWhitePiece);
        pieceSelection.add(checkBoxWhiteToMove);

        JPanel miscPanel = new JPanel();
        miscPanel.setLayout(new GridLayout(3,1));
        miscPanel.setBorder(BorderFactory.createTitledBorder("Misc Options"));
        miscPanel.add(checkBoxVisibleCoords);
        miscPanel.add(checkBoxHighlightCoords);
        miscPanel.add(checkBoxEnableBoardSelection);

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Board Messages"));
        textAreaOutput = new JTextArea();
        textAreaOutput.setEditable(false);
        textAreaOutput.setLineWrap(true);
        textAreaOutput.setWrapStyleWord(true);
        outputPanel.add(new JScrollPane(textAreaOutput),BorderLayout.CENTER);

        optionsPanel.add(castlePanel);
        optionsPanel.add(pieceSelection);
        optionsPanel.add(miscPanel);
        optionsPanel.add(outputPanel);

        boardPanel.add(optionsPanel, BorderLayout.EAST);

        setTitle("Processing 3 Chessboard");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                ps.stopThread();
            }
        });
        ps.startThread();
    }

    private void initWidgets(){
        textFieldStrFen = new JTextField(ForsythEdwardsPos.DEFAULT_POSITION);
        buttonSetFenPos = new JButton("Set Position");
        buttonGetFenStr = new JButton("Get FEN Position");
        buttonClearBoard = new JButton("Clear Board");
        buttonClearSquareSel = new JButton("Clear Square Selection");
        buttonSetDefaultPos = new JButton("Set Default Position");
        buttonGetTextBoard = new JButton("Board to Text");

        buttonKingPiece = new JButton();
        buttonQueenPiece = new JButton();
        buttonRookPiece = new JButton();
        buttonKnightPiece = new JButton();
        buttonBishopPiece = new JButton();
        buttonPawnPiece = new JButton();

        checkBoxWhiteShortCastle = new JCheckBox("White O-O");
        checkBoxWhiteLongCastle  = new JCheckBox("White O-O-O");
        checkBoxBlackShortCastle = new JCheckBox("Black O-O");
        checkBoxBlackLongCastle  = new JCheckBox("Black O-O-O");

        checkBoxWhitePiece = new JCheckBox("White piece");
        checkBoxWhiteToMove = new JCheckBox("White to Move");

        checkBoxVisibleCoords = new JCheckBox("Visible Coordinates");
        checkBoxHighlightCoords = new JCheckBox("Enable Highlight Squares");
        checkBoxEnableBoardSelection = new JCheckBox("Enable Board Selection");
        buttonAbout = new JButton("About");
    }

    private void configWidgetActions(){
        checkBoxWhitePiece.setSelected(true);
        checkBoxWhiteToMove.setSelected(true);
        checkBoxWhiteShortCastle.setSelected(true);
        checkBoxWhiteLongCastle.setSelected(true);
        checkBoxBlackShortCastle.setSelected(true);
        checkBoxBlackLongCastle.setSelected(true);
        checkBoxEnableBoardSelection.setSelected(true);
        setColorPieceSelection(true);

        checkBoxWhiteToMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = checkBoxWhiteToMove.isSelected();
                chessBoardSketch.getChessBoard().setWhiteToMove(flag);
            }
        });

        checkBoxWhitePiece.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = checkBoxWhitePiece.isSelected();
                setColorPieceSelection(flag);
            }
        });

        checkBoxWhiteShortCastle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = checkBoxWhiteShortCastle.isSelected();
                chessBoardSketch.getChessBoard().setWhiteCanShortCastle(flag);
            }
        });

        checkBoxWhiteLongCastle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = checkBoxWhiteLongCastle.isSelected();
                chessBoardSketch.getChessBoard().setWhiteCanLongCastle(flag);
            }
        });

        checkBoxBlackShortCastle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = checkBoxBlackShortCastle.isSelected();
                chessBoardSketch.getChessBoard().setBlackCanShortCastle(flag);
            }
        });

        checkBoxBlackLongCastle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = checkBoxBlackLongCastle.isSelected();
                chessBoardSketch.getChessBoard().setBlackCanLongCastle(flag);
            }
        });

        checkBoxVisibleCoords.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = checkBoxVisibleCoords.isSelected();
                chessBoardSketch.getChessBoard().setVisibleCoordinates(flag);
            }
        });

        checkBoxHighlightCoords.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = checkBoxHighlightCoords.isSelected();
                chessBoardSketch.getChessBoard().setSelectedHighlightSquares(flag);
            }
        });

        checkBoxEnableBoardSelection.addActionListener(event ->
                chessBoardSketch.getChessBoard().setSelectionEnabled(
                        checkBoxEnableBoardSelection.isSelected()));

        //
        buttonSetFenPos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    chessBoardSketch.getChessBoard().setPositionFromFENString(textFieldStrFen.getText());
                    syncBoardStateToControls();
                    showMessage("Position loaded successfully.");
                } catch (IllegalArgumentException exception) {
                    JOptionPane.showMessageDialog(
                            ChessBoardDemoFrame.this,
                            exception.getMessage(),
                            "Invalid FEN position",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonGetFenStr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fenposition = chessBoardSketch.getChessBoard().getFENStringFromCurrentPosition();
                textFieldStrFen.setText(fenposition);
            }
        });

        buttonClearBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chessBoardSketch.getChessBoard().setClearBoard();
                syncBoardStateToControls();
            }
        });

        buttonClearSquareSel.addActionListener(event ->
                chessBoardSketch.getChessBoard().clearSelection());

        buttonSetDefaultPos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chessBoardSketch.getChessBoard().setDefaultNewGamePosition();
                syncBoardStateToControls();
                textFieldStrFen.setText(chessBoardSketch.getChessBoard().getFENStringFromCurrentPosition());
            }
        });

        buttonGetTextBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage(chessBoardSketch.getChessBoard().toString());
            }
        });

        buttonKingPiece.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPieceForSelectedSquares("K");
            }
        });

        buttonQueenPiece.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPieceForSelectedSquares("Q");
            }
        });

        buttonRookPiece.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPieceForSelectedSquares("R");
            }
        });

        buttonKnightPiece.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPieceForSelectedSquares("N");
            }
        });

        buttonBishopPiece.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPieceForSelectedSquares("B");
            }
        });

        buttonPawnPiece.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               setPieceForSelectedSquares("P");
            }
        });


        buttonAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = "ChessBoard with Processing\n Author: Obed Isai Rios";
                String titleBar = "About: ChessBoard with Procesing";
                JOptionPane.showMessageDialog(null, message, titleBar, JOptionPane.INFORMATION_MESSAGE);
            }
        });

    }

    private void setPieceForSelectedSquares(String pieceCode){
        ChessBoard chessBoard = chessBoardSketch.getChessBoard();
        SquareBoard[] squares = chessBoard.getChessBoard();
        for(SquareBoard currentSquare : squares){
            if(currentSquare.isSelectedSquare()){
                currentSquare.setBoardPiece(pieceCode);
                currentSquare.setWhitePiece(checkBoxWhitePiece.isSelected());
            }
        }
    }

    private Image getPieceImage(int pcode, int w, int h, boolean isWhite){
        PImage img = pieceImages.getImagePiece(pcode, isWhite);
        img.resize(w, h);
        return img.getImage();
    }

    private void setColorPieceSelection(boolean isWhite){
        buttonKingPiece.setIcon(new ImageIcon(getPieceImage(BoardImagePieces.KING, 30, 30, isWhite)));
        buttonQueenPiece.setIcon(new ImageIcon(getPieceImage(BoardImagePieces.QUEEN, 30, 30, isWhite)));
        buttonRookPiece.setIcon(new ImageIcon(getPieceImage(BoardImagePieces.ROOK, 30, 30, isWhite)));
        buttonKnightPiece.setIcon(new ImageIcon(getPieceImage(BoardImagePieces.KNIGHT, 30, 30,isWhite)));
        buttonBishopPiece.setIcon(new ImageIcon(getPieceImage(BoardImagePieces.BISHOP, 30, 30, isWhite)));
        buttonPawnPiece.setIcon(new ImageIcon(getPieceImage(BoardImagePieces.PAWN, 30, 30, isWhite)));
    }

    private void syncBoardStateToControls() {
        ChessBoard board = chessBoardSketch.getChessBoard();
        checkBoxWhiteToMove.setSelected(board.isWhiteToMove());
        checkBoxWhiteShortCastle.setSelected(board.isWhiteCanShortCastle());
        checkBoxWhiteLongCastle.setSelected(board.isWhiteCanLongCastle());
        checkBoxBlackShortCastle.setSelected(board.isBlackCanShortCastle());
        checkBoxBlackLongCastle.setSelected(board.isBlackCanLongCastle());
    }

    private void showMessage(String message) {
        if (!textAreaOutput.getText().isEmpty()) {
            textAreaOutput.append(System.lineSeparator());
        }
        textAreaOutput.append(message);
        textAreaOutput.setCaretPosition(textAreaOutput.getDocument().getLength());
    }
}
