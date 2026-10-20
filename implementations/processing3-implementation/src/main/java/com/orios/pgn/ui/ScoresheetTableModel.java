package com.orios.pgn.ui;

import com.orios.pgn.PlaybackState;
import com.orios.pgn.PgnMoveNode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public final class ScoresheetTableModel extends AbstractTableModel {
    private List<Row> rows = List.of();

    public void setPlaybackState(PlaybackState state) {
        String[] fen = state.game().initialFen().split("\\s+");
        int fullmove = Integer.parseInt(fen[5]);
        boolean white = fen[1].equals("w");
        List<RowBuilder> builders = new ArrayList<>();
        for (int index = 0; index < state.activeLine().size(); index++) {
            int moveNumber = fullmove + ((index + (white ? 0 : 1)) / 2);
            boolean whiteMove = ((index + (white ? 0 : 1)) % 2) == 0;
            RowBuilder row;
            if (builders.isEmpty() || builders.get(builders.size() - 1).number != moveNumber) {
                row = new RowBuilder(moveNumber);
                builders.add(row);
            } else {
                row = builders.get(builders.size() - 1);
            }
            PgnMoveNode node = state.activeLine().get(index);
            if (whiteMove) { row.white = node.san(); row.whiteIndex = index; }
            else { row.black = node.san(); row.blackIndex = index; }
        }
        rows = builders.stream().map(RowBuilder::build).toList();
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return 3; }
    @Override public String getColumnName(int column) {
        return switch (column) { case 0 -> "#"; case 1 -> "White"; default -> "Black"; };
    }
    @Override public Object getValueAt(int row, int column) {
        Row value = rows.get(row);
        return switch (column) { case 0 -> value.number; case 1 -> value.white; default -> value.black; };
    }

    public int moveIndexAt(int row, int column) {
        if (row < 0 || row >= rows.size()) return -2;
        return column == 1 ? rows.get(row).whiteIndex
                : column == 2 ? rows.get(row).blackIndex : -2;
    }

    public int[] cellForMove(int moveIndex) {
        for (int row = 0; row < rows.size(); row++) {
            if (rows.get(row).whiteIndex == moveIndex) return new int[]{row, 1};
            if (rows.get(row).blackIndex == moveIndex) return new int[]{row, 2};
        }
        return null;
    }

    private record Row(int number, String white, String black, int whiteIndex, int blackIndex) { }
    private static final class RowBuilder {
        private final int number;
        private String white = "";
        private String black = "";
        private int whiteIndex = -2;
        private int blackIndex = -2;
        private RowBuilder(int number) { this.number = number; }
        private Row build() { return new Row(number, white, black, whiteIndex, blackIndex); }
    }
}
