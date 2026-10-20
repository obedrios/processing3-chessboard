package com.orios.pgn.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.orios.javachessboard.utils.ForsythEdwardsPos;
import com.orios.pgn.PgnGame;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PgnGameTableModelTest {
    @Test
    void exposesSelectionColumnsInTheRequestedOrder() {
        PgnGame game = new PgnGame(Map.of(
                "White", "Kasparov,Garry", "Black", "Comp Deep Blue",
                "Result", "1-0", "Date", "1996.02.10", "ECO", "B22",
                "Site", "Philadelphia", "Event", "Philadelphia m"),
                ForsythEdwardsPos.DEFAULT_POSITION, List.of());
        PgnGameTableModel model = new PgnGameTableModel(List.of(game));

        assertEquals(List.of("Number", "White", "Black", "Result", "Year", "ECO", "Site", "Event"),
                java.util.stream.IntStream.range(0, model.getColumnCount())
                        .mapToObj(model::getColumnName).toList());
        assertEquals(1, model.getValueAt(0, 0));
        assertEquals("Kasparov,Garry", model.getValueAt(0, 1));
        assertEquals("1996", model.getValueAt(0, 4));
    }
}
