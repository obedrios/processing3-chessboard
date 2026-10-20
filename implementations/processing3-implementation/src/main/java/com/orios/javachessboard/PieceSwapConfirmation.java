package com.orios.javachessboard;

/** Requests confirmation before exchanging two occupied board squares. */
@FunctionalInterface
public interface PieceSwapConfirmation {

    /**
     * @param sourceCoordinate coordinate selected as the move origin
     * @param destinationCoordinate occupied destination coordinate
     * @return {@code true} to exchange both pieces; {@code false} to cancel
     */
    boolean confirmSwap(String sourceCoordinate, String destinationCoordinate);
}
