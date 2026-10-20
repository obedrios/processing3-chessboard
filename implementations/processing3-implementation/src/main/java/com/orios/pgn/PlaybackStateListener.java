package com.orios.pgn;

@FunctionalInterface
public interface PlaybackStateListener {
    void playbackStateChanged(PlaybackState state);
}
