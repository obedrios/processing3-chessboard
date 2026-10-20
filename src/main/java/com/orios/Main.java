package com.orios;

import com.orios.demos.ChessBoardDemoFrame;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author obedrios
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);

                // Create your Frame
                ChessBoardDemoFrame frame = new ChessBoardDemoFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
            }
        });
    }

}
