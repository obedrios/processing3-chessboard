package com.orios.demos;

import com.orios.javachessboard.BoardImagePieces;
import com.orios.javachessboard.ChessBoard;
import com.orios.javachessboard.SquareBoard;
import com.orios.javachessboard.utils.ForsythEdwardsPos;
import com.orios.pgn.GamePlaybackController;
import com.orios.pgn.PgnGame;
import com.orios.pgn.PgnGameReader;
import com.orios.pgn.ui.PgnGameSelectionDialog;
import com.orios.pgn.ui.PgnPlaybackPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

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

    private final GamePlaybackController playbackController = new GamePlaybackController();
    private final PgnGameReader pgnReader = new PgnGameReader();
    private PgnPlaybackPanel playbackPanel;
    private JPanel editorPanel;
    private JTabbedPane sideTabs;
    private JButton buttonPlaybackStart;
    private JButton buttonPlaybackPrevious;
    private JButton buttonPlaybackNext;
    private JButton buttonPlaybackEnd;
    private JMenuItem menuClosePgn;
    private String prePlaybackFen;
    private boolean prePlaybackSelectionEnabled;

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
        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.add(boardActionsPanel);
        JPanel transportPanel = new JPanel();
        transportPanel.setBorder(BorderFactory.createTitledBorder("Game navigation"));
        transportPanel.add(buttonPlaybackStart);
        transportPanel.add(buttonPlaybackPrevious);
        transportPanel.add(buttonPlaybackNext);
        transportPanel.add(buttonPlaybackEnd);
        southPanel.add(transportPanel);
        boardPanel.add(southPanel, BorderLayout.SOUTH);
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

        editorPanel = optionsPanel;
        playbackPanel = new PgnPlaybackPanel(playbackController);
        sideTabs = new JTabbedPane();
        sideTabs.addTab("Editor", editorPanel);
        sideTabs.addTab("Notation", playbackPanel.notationPanel());
        sideTabs.addTab("Scoresheet", playbackPanel.scoresheetPanel());
        sideTabs.setPreferredSize(new Dimension(430, ChessBoardDemoSketch.CANVAS_SIZE));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, boardPanel, sideTabs);
        splitPane.setResizeWeight(0.67);
        splitPane.setContinuousLayout(true);
        contentPanel.add(splitPane, BorderLayout.CENTER);
        setJMenuBar(createMenuBar());

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
        buttonPlaybackStart = new JButton("|< Start");
        buttonPlaybackPrevious = new JButton("< Previous");
        buttonPlaybackNext = new JButton("Next >");
        buttonPlaybackEnd = new JButton("End >|");
        setTransportEnabled(false, false);
    }

    private void configWidgetActions(){
        chessBoardSketch.setSwapConfirmation(this::confirmPieceSwap);
        textFieldStrFen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (playbackController.hasGame()) {
                    copyPlaybackFenToClipboard();
                }
            }
        });
        playbackController.addListener(state -> SwingUtilities.invokeLater(() -> {
            chessBoardSketch.getChessBoard().setPositionFromFENString(state.fen());
            textFieldStrFen.setText(state.fen());
            syncBoardStateToControls();
            playbackPanel.update(state);
            buttonPlaybackStart.setEnabled(!state.atStart());
            buttonPlaybackPrevious.setEnabled(!state.atStart());
            buttonPlaybackNext.setEnabled(!state.atEnd());
            buttonPlaybackEnd.setEnabled(!state.atEnd());
        }));

        buttonPlaybackStart.addActionListener(event -> playbackController.goToStart());
        buttonPlaybackPrevious.addActionListener(event -> playbackController.previous());
        buttonPlaybackNext.addActionListener(event -> playbackController.next());
        buttonPlaybackEnd.addActionListener(event -> playbackController.goToEnd());

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

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openPgn = new JMenuItem("Open PGN…");
        menuClosePgn = new JMenuItem("Close game");
        menuClosePgn.setEnabled(false);
        openPgn.addActionListener(event -> choosePgnFile());
        menuClosePgn.addActionListener(event -> closePlayback());
        fileMenu.add(openPgn);
        fileMenu.add(menuClosePgn);
        menuBar.add(fileMenu);
        return menuBar;
    }

    private void choosePgnFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open PGN file");
        chooser.setFileFilter(new FileNameExtensionFilter("Portable Game Notation (*.pgn)", "pgn"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path selectedPath = chooser.getSelectedFile().toPath();
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        new SwingWorker<List<PgnGame>, Void>() {
            @Override protected List<PgnGame> doInBackground() throws Exception {
                return pgnReader.read(selectedPath);
            }

            @Override protected void done() {
                setCursor(java.awt.Cursor.getDefaultCursor());
                try {
                    List<PgnGame> games = get();
                    PgnGameSelectionDialog.choose(ChessBoardDemoFrame.this, games)
                            .ifPresent(ChessBoardDemoFrame.this::startPlayback);
                } catch (Exception exception) {
                    Throwable cause = exception.getCause() == null ? exception : exception.getCause();
                    JOptionPane.showMessageDialog(ChessBoardDemoFrame.this,
                            cause.getMessage(), "Unable to open PGN", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void startPlayback(PgnGame game) {
        if (!playbackController.hasGame()) {
            ChessBoard board = chessBoardSketch.getChessBoard();
            prePlaybackFen = board.getFENStringFromCurrentPosition();
            prePlaybackSelectionEnabled = board.isSelectionEnabled();
        }
        setEditorModeEnabled(false);
        chessBoardSketch.getChessBoard().setSelectionEnabled(false);
        menuClosePgn.setEnabled(true);
        sideTabs.setSelectedIndex(1);
        playbackController.load(game);
        showMessage("Loaded PGN: " + game.white() + " – " + game.black()
                + " " + game.result());
    }

    private void closePlayback() {
        if (!playbackController.hasGame()) return;
        playbackController.clear();
        playbackPanel.clear();
        ChessBoard board = chessBoardSketch.getChessBoard();
        board.setPositionFromFENString(prePlaybackFen);
        board.setSelectionEnabled(prePlaybackSelectionEnabled);
        textFieldStrFen.setText(prePlaybackFen);
        syncBoardStateToControls();
        setEditorModeEnabled(true);
        menuClosePgn.setEnabled(false);
        setTransportEnabled(false, false);
        sideTabs.setSelectedIndex(0);
        showMessage("PGN game closed; the previous editor position was restored.");
    }

    private void setTransportEnabled(boolean backward, boolean forward) {
        if (buttonPlaybackStart == null) return;
        buttonPlaybackStart.setEnabled(backward);
        buttonPlaybackPrevious.setEnabled(backward);
        buttonPlaybackNext.setEnabled(forward);
        buttonPlaybackEnd.setEnabled(forward);
    }

    private void setEditorModeEnabled(boolean enabled) {
        setEnabledRecursively(editorPanel, enabled);
        textFieldStrFen.setEnabled(true);
        textFieldStrFen.setEditable(enabled);
        textFieldStrFen.setToolTipText(enabled
                ? null : "Click to copy the current FEN position");
        textFieldStrFen.setCursor(enabled
                ? java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.TEXT_CURSOR)
                : java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        buttonSetFenPos.setEnabled(enabled);
        buttonClearBoard.setEnabled(enabled);
        buttonClearSquareSel.setEnabled(enabled);
        buttonSetDefaultPos.setEnabled(enabled);
    }

    private void copyPlaybackFenToClipboard() {
        String fen = textFieldStrFen.getText();
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(fen), null);
            textFieldStrFen.selectAll();
            showMessage("FEN copied to clipboard: " + fen);
        } catch (IllegalStateException exception) {
            showMessage("Clipboard is currently unavailable. Try again.");
        }
    }

    private static void setEnabledRecursively(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                setEnabledRecursively(child, enabled);
            }
        }
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
        chessBoard.clearSelection();
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

    private boolean confirmPieceSwap(String sourceCoordinate, String destinationCoordinate) {
        if (SwingUtilities.isEventDispatchThread()) {
            return showSwapDialog(sourceCoordinate, destinationCoordinate);
        }

        AtomicBoolean confirmed = new AtomicBoolean(false);
        try {
            SwingUtilities.invokeAndWait(() -> confirmed.set(
                    showSwapDialog(sourceCoordinate, destinationCoordinate)));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            String detail = cause == null ? exception.getMessage() : cause.getMessage();
            SwingUtilities.invokeLater(() ->
                    showMessage("Unable to request piece exchange: " + detail));
        }
        return confirmed.get();
    }

    private boolean showSwapDialog(String sourceCoordinate, String destinationCoordinate) {
        Object[] options = {"Swap pieces", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "The destination square " + destinationCoordinate + " is occupied.\n"
                        + "Do you want to exchange the pieces on " + sourceCoordinate
                        + " and " + destinationCoordinate + "?",
                "Occupied destination",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        boolean confirmed = choice == 0;
        showMessage(confirmed
                ? "Pieces exchanged: " + sourceCoordinate + " ↔ " + destinationCoordinate
                : "Move cancelled: " + sourceCoordinate + " → " + destinationCoordinate);
        return confirmed;
    }
}
