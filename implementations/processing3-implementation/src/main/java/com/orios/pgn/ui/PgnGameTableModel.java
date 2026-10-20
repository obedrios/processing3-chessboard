package com.orios.pgn.ui;

import com.orios.pgn.PgnGame;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public final class PgnGameTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "Number", "White", "Black", "Result", "Year", "ECO", "Site", "Event"
    };
    private final List<PgnGame> games;

    public PgnGameTableModel(List<PgnGame> games) {
        this.games = List.copyOf(games);
    }

    @Override public int getRowCount() { return games.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int column) { return COLUMNS[column]; }
    @Override public Class<?> getColumnClass(int column) { return column == 0 ? Integer.class : String.class; }

    @Override
    public Object getValueAt(int row, int column) {
        PgnGame game = games.get(row);
        return switch (column) {
            case 0 -> row + 1;
            case 1 -> game.white();
            case 2 -> game.black();
            case 3 -> game.result();
            case 4 -> game.year();
            case 5 -> game.eco();
            case 6 -> game.site();
            case 7 -> game.event();
            default -> throw new IndexOutOfBoundsException(column);
        };
    }

    public PgnGame gameAt(int row) {
        return games.get(row);
    }
}
