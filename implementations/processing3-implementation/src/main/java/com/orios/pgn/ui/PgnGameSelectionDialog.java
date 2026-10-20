package com.orios.pgn.ui;

import com.orios.pgn.PgnGame;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public final class PgnGameSelectionDialog extends JDialog {
    private final PgnGameTableModel model;
    private final JTable table;
    private PgnGame selected;

    private PgnGameSelectionDialog(Window owner, List<PgnGame> games) {
        super(owner, "Select PGN game", ModalityType.APPLICATION_MODAL);
        model = new PgnGameTableModel(games);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        JButton accept = new JButton("OK");
        accept.setEnabled(false);
        JButton cancel = new JButton("Cancel");
        table.getSelectionModel().addListSelectionListener(event ->
                accept.setEnabled(table.getSelectedRow() >= 0));
        accept.addActionListener(event -> acceptSelection());
        cancel.addActionListener(event -> dispose());
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && table.getSelectedRow() >= 0) acceptSelection();
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancel);
        buttons.add(accept);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(900, 450));
        pack();
        setLocationRelativeTo(owner);
    }

    private void acceptSelection() {
        int viewRow = table.getSelectedRow();
        if (viewRow >= 0) {
            selected = model.gameAt(table.convertRowIndexToModel(viewRow));
            dispose();
        }
    }

    public static Optional<PgnGame> choose(Component owner, List<PgnGame> games) {
        Window window = owner == null ? null : SwingUtilities.getWindowAncestor(owner);
        PgnGameSelectionDialog dialog = new PgnGameSelectionDialog(window, games);
        dialog.setVisible(true);
        return Optional.ofNullable(dialog.selected);
    }
}
