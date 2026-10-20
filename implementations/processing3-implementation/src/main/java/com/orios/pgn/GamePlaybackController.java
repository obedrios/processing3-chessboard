package com.orios.pgn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Owns the active PGN route and all navigation state, independently of Swing. */
public final class GamePlaybackController {
    private final List<PlaybackStateListener> listeners = new CopyOnWriteArrayList<>();
    private PgnGame game;
    private List<PgnMoveNode> activeLine = List.of();
    private int currentIndex = -1;

    public void load(PgnGame game) {
        this.game = Objects.requireNonNull(game, "game must not be null");
        this.activeLine = defaultLine(game.continuations());
        this.currentIndex = -1;
        notifyListeners();
    }

    public void clear() {
        game = null;
        activeLine = List.of();
        currentIndex = -1;
    }

    public boolean hasGame() {
        return game != null;
    }

    public PlaybackState state() {
        if (game == null) {
            throw new IllegalStateException("No PGN game is loaded");
        }
        PgnMoveNode current = currentIndex < 0 ? null : activeLine.get(currentIndex);
        String fen = current == null ? game.initialFen() : current.fenAfter();
        return new PlaybackState(game, fen, current, currentIndex, activeLine);
    }

    public void goToStart() {
        requireGame();
        currentIndex = -1;
        notifyListeners();
    }

    public void previous() {
        requireGame();
        if (currentIndex >= 0) {
            currentIndex--;
            notifyListeners();
        }
    }

    public void next() {
        requireGame();
        if (currentIndex + 1 < activeLine.size()) {
            currentIndex++;
            notifyListeners();
        }
    }

    public void goToEnd() {
        requireGame();
        currentIndex = activeLine.size() - 1;
        notifyListeners();
    }

    public void goToIndex(int index) {
        requireGame();
        if (index < -1 || index >= activeLine.size()) {
            throw new IndexOutOfBoundsException("Move index is outside the active line: " + index);
        }
        currentIndex = index;
        notifyListeners();
    }

    /** Activates the route containing {@code target}, then jumps to that move. */
    public void selectVariation(PgnMoveNode target) {
        requireGame();
        Objects.requireNonNull(target, "target must not be null");
        List<PgnMoveNode> path = new ArrayList<>();
        if (!findPath(game.continuations(), target, path)) {
            throw new IllegalArgumentException("The selected move does not belong to the loaded game");
        }
        int targetIndex = path.size() - 1;
        PgnMoveNode tail = target;
        while (!tail.continuations().isEmpty()) {
            tail = tail.continuations().get(0);
            path.add(tail);
        }
        activeLine = List.copyOf(path);
        currentIndex = targetIndex;
        notifyListeners();
    }

    public void addListener(PlaybackStateListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener must not be null"));
    }

    public void removeListener(PlaybackStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        PlaybackState state = state();
        for (PlaybackStateListener listener : listeners) {
            listener.playbackStateChanged(state);
        }
    }

    private void requireGame() {
        if (game == null) throw new IllegalStateException("No PGN game is loaded");
    }

    private static List<PgnMoveNode> defaultLine(List<PgnMoveNode> roots) {
        List<PgnMoveNode> line = new ArrayList<>();
        List<PgnMoveNode> choices = roots;
        while (!choices.isEmpty()) {
            PgnMoveNode node = choices.get(0);
            line.add(node);
            choices = node.continuations();
        }
        return List.copyOf(line);
    }

    private static boolean findPath(List<PgnMoveNode> nodes, PgnMoveNode target,
                                    List<PgnMoveNode> path) {
        for (PgnMoveNode node : nodes) {
            path.add(node);
            if (node == target || findPath(node.continuations(), target, path)) return true;
            path.remove(path.size() - 1);
        }
        return false;
    }
}
