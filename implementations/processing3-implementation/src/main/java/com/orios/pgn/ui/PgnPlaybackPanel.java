package com.orios.pgn.ui;

import com.orios.pgn.GamePlaybackController;
import com.orios.pgn.PlaybackState;
import com.orios.pgn.PgnGame;
import com.orios.pgn.PgnMoveNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.HyperlinkEvent;
import javax.swing.table.DefaultTableCellRenderer;

/** Contains synchronized annotated notation and scoresheet views. */
public final class PgnPlaybackPanel {
    private final GamePlaybackController controller;
    private final JEditorPane notation = new JEditorPane("text/html", "");
    private final ScoresheetTableModel scoresheetModel = new ScoresheetTableModel();
    private final JTable scoresheet = new JTable(scoresheetModel);
    private final JPanel notationPanel = new JPanel(new BorderLayout());
    private final JPanel scoresheetPanel = new JPanel(new BorderLayout());
    private final Map<String, PgnMoveNode> notationLinks = new java.util.HashMap<>();
    private PlaybackState state;

    public PgnPlaybackPanel(GamePlaybackController controller) {
        this.controller = controller;
        notation.setEditable(false);
        notation.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        notation.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        notation.addHyperlinkListener(event -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                PgnMoveNode node = notationLinks.get(event.getDescription());
                if (node != null) controller.selectVariation(node);
            }
        });
        scoresheet.setFillsViewportHeight(true);
        scoresheet.setDefaultRenderer(Object.class, new CurrentMoveRenderer());
        scoresheet.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) {
                int row = scoresheet.rowAtPoint(event.getPoint());
                int column = scoresheet.columnAtPoint(event.getPoint());
                int index = scoresheetModel.moveIndexAt(row, column);
                if (index >= 0) controller.goToIndex(index);
            }
        });
        notationPanel.add(new JScrollPane(notation), BorderLayout.CENTER);
        scoresheetPanel.add(new JScrollPane(scoresheet), BorderLayout.CENTER);
    }

    public JPanel notationPanel() { return notationPanel; }
    public JPanel scoresheetPanel() { return scoresheetPanel; }

    public void update(PlaybackState state) {
        this.state = state;
        scoresheetModel.setPlaybackState(state);
        renderNotation(state);
        int[] cell = scoresheetModel.cellForMove(state.currentIndex());
        if (cell != null) {
            scoresheet.changeSelection(cell[0], cell[1], false, false);
            scoresheet.scrollRectToVisible(scoresheet.getCellRect(cell[0], cell[1], true));
        } else {
            scoresheet.clearSelection();
        }
        scoresheet.repaint();
    }

    public void clear() {
        state = null;
        notation.setText("");
        notationLinks.clear();
    }

    private void renderNotation(PlaybackState state) {
        notationLinks.clear();
        IdentityHashMap<PgnMoveNode, String> ids = new IdentityHashMap<>();
        StringBuilder html = new StringBuilder("<html><body style='font-family:sans-serif'>");
        PgnGame game = state.game();
        html.append("<b>").append(escape(game.white())).append(" – ")
                .append(escape(game.black())).append(" ")
                .append(escape(game.result())).append("</b><br>")
                .append(escape(game.event())).append(" · ").append(escape(game.site()))
                .append(" · ").append(escape(game.tag("Date"))).append("<hr>");
        String[] fen = game.initialFen().split("\\s+");
        int ply = (Integer.parseInt(fen[5]) - 1) * 2 + (fen[1].equals("b") ? 1 : 0);
        if (!game.continuations().isEmpty()) {
            renderLine(html, game.continuations().get(0), ply, state.currentMove(), ids);
            for (int i = 1; i < game.continuations().size(); i++) {
                html.append("<div style='margin-left:16px'>(");
                renderLine(html, game.continuations().get(i), ply, state.currentMove(), ids);
                html.append(")</div>");
            }
        }
        html.append(" <b>").append(escape(game.result())).append("</b></body></html>");
        notation.setText(html.toString());
        String currentId = state.currentMove() == null ? null : ids.get(state.currentMove());
        if (currentId == null) notation.setCaretPosition(0);
        else javax.swing.SwingUtilities.invokeLater(() -> notation.scrollToReference(currentId));
    }

    private void renderLine(StringBuilder html, PgnMoveNode first, int ply,
                            PgnMoveNode current, IdentityHashMap<PgnMoveNode, String> ids) {
        PgnMoveNode node = first;
        int currentPly = ply;
        while (node != null) {
            if (currentPly % 2 == 0) html.append(currentPly / 2 + 1).append(". ");
            else if (node == first) html.append(currentPly / 2 + 1).append("... ");
            String id = ids.computeIfAbsent(node, ignored -> "move:" + ids.size());
            notationLinks.put(id, node);
            html.append("<a name='").append(id).append("'></a><a href='").append(id).append("'");
            if (node == current) html.append(" style='background:#ffe082;font-weight:bold'");
            html.append(">").append(escape(node.san())).append("</a>");
            if (!node.nags().isEmpty()) html.append(" ").append(escape(String.join(" ", node.nags())));
            for (String comment : node.comments()) {
                html.append(" <span style='color:#2455a4'>{").append(escape(comment)).append("}</span>");
            }
            for (int index = 1; index < node.continuations().size(); index++) {
                html.append(" <span style='color:#555'>(");
                renderLine(html, node.continuations().get(index), currentPly + 1, current, ids);
                html.append(")</span>");
            }
            html.append(' ');
            currentPly++;
            node = node.continuations().isEmpty() ? null : node.continuations().get(0);
        }
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    private final class CurrentMoveRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                       boolean focus, int row, int column) {
            Component component = super.getTableCellRendererComponent(
                    table, value, selected, focus, row, column);
            int index = scoresheetModel.moveIndexAt(row, column);
            if (!selected && state != null && index == state.currentIndex()) {
                component.setBackground(new Color(255, 224, 130));
            } else if (!selected) {
                component.setBackground(table.getBackground());
            }
            return component;
        }
    }
}
