package com.orios.pgn;

import com.github.bhlangonijr.chesslib.Board;
import com.orios.javachessboard.utils.ForsythEdwardsPos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads standard-chess PGN files into an immutable, variation-aware model. */
public final class PgnGameReader {
    private static final Pattern TAG = Pattern.compile("^\\s*\\[([A-Za-z0-9_]+)\\s+\"((?:\\\\.|[^\"])*)\"\\]\\s*$");
    private static final Pattern MOVE_NUMBER = Pattern.compile("^\\d+\\.(?:\\.\\.)?");
    private static final String STANDARD_START = ForsythEdwardsPos.DEFAULT_POSITION;

    public List<PgnGame> read(Path path) throws PgnReadException {
        Objects.requireNonNull(path, "path must not be null");
        final String content;
        try {
            content = Files.readString(path, StandardCharsets.UTF_8).replace("\uFEFF", "");
        } catch (IOException exception) {
            throw new PgnReadException("Unable to read PGN file: " + path, exception);
        }

        List<RawGame> rawGames = splitGames(content);
        if (rawGames.isEmpty()) {
            throw new PgnReadException("The selected PGN file contains no games");
        }

        List<PgnGame> games = new ArrayList<>(rawGames.size());
        for (int index = 0; index < rawGames.size(); index++) {
            try {
                games.add(parseGame(rawGames.get(index)));
            } catch (RuntimeException exception) {
                throw new PgnReadException("Invalid PGN game " + (index + 1) + ": "
                        + rootMessage(exception), exception);
            }
        }
        return List.copyOf(games);
    }

    private static List<RawGame> splitGames(String content) throws PgnReadException {
        List<RawGame> games = new ArrayList<>();
        Map<String, String> tags = new LinkedHashMap<>();
        StringBuilder moves = new StringBuilder();
        boolean sawMoves = false;

        for (String sourceLine : content.split("\\R", -1)) {
            String line = sourceLine.trim();
            Matcher matcher = TAG.matcher(line);
            if (matcher.matches()) {
                if (sawMoves) {
                    games.add(new RawGame(tags, moves.toString()));
                    tags = new LinkedHashMap<>();
                    moves = new StringBuilder();
                    sawMoves = false;
                }
                tags.put(matcher.group(1), unescapeTag(matcher.group(2)));
            } else if (!line.isEmpty()) {
                if (tags.isEmpty()) {
                    throw new PgnReadException("Move text was found before a PGN tag section");
                }
                sawMoves = true;
                moves.append(sourceLine).append('\n');
            }
        }
        if (!tags.isEmpty()) {
            games.add(new RawGame(tags, moves.toString()));
        }
        return games;
    }

    private static PgnGame parseGame(RawGame raw) {
        String variant = raw.tags.getOrDefault("Variant", "Standard");
        if (!variant.isBlank() && !variant.equalsIgnoreCase("standard")
                && !variant.equalsIgnoreCase("normal")) {
            throw new IllegalArgumentException("Unsupported chess variant: " + variant);
        }

        String initialFen = raw.tags.getOrDefault("FEN", STANDARD_START);
        if ("1".equals(raw.tags.get("SetUp")) && !raw.tags.containsKey("FEN")) {
            throw new IllegalArgumentException("SetUp=1 requires a FEN tag");
        }
        new ForsythEdwardsPos(initialFen).getDecodedPositionFromFen();

        MutableNode root = MutableNode.root(initialFen);
        Board board = boardAt(initialFen);
        List<Token> tokens = tokenize(raw.moveText);
        Cursor cursor = new Cursor();
        parseSequence(tokens, cursor, root, board, false);
        if (cursor.index != tokens.size()) {
            throw new IllegalArgumentException("Unexpected token near the end of the game");
        }
        return new PgnGame(raw.tags, initialFen, freezeChildren(root));
    }

    private static void parseSequence(List<Token> tokens, Cursor cursor, MutableNode parent,
                                      Board board, boolean variation) {
        MutableNode current = parent;
        MutableNode lastMove = null;
        List<String> pendingComments = new ArrayList<>();
        while (cursor.index < tokens.size()) {
            Token token = tokens.get(cursor.index++);
            switch (token.type) {
                case CLOSE -> {
                    if (!variation) {
                        throw new IllegalArgumentException("Unmatched ')' in move text");
                    }
                    return;
                }
                case OPEN -> {
                    MutableNode branch = lastMove == null ? current : lastMove.parent;
                    Board variationBoard = boardAt(branch.fenAfter);
                    parseSequence(tokens, cursor, branch, variationBoard, true);
                }
                case COMMENT -> {
                    if (lastMove == null) {
                        pendingComments.add(token.text);
                    } else {
                        lastMove.comments.add(token.text);
                    }
                }
                case NAG -> {
                    if (lastMove != null) {
                        lastMove.nags.add(token.text);
                    }
                }
                case RESULT -> { }
                case MOVE -> {
                    String symbol = stripMoveNumber(token.text);
                    if (symbol.isEmpty()) {
                        continue;
                    }
                    String annotation = trailingAnnotation(symbol);
                    String san = annotation.isEmpty()
                            ? symbol : symbol.substring(0, symbol.length() - annotation.length());
                    if (!board.doMove(san)) {
                        throw new IllegalArgumentException("Illegal move: " + san);
                    }
                    MutableNode node = new MutableNode(current, san, board.getFen());
                    node.comments.addAll(pendingComments);
                    pendingComments.clear();
                    if (!annotation.isEmpty()) {
                        node.nags.add(annotation);
                    }
                    current.children.add(node);
                    current = node;
                    lastMove = node;
                }
            }
        }
        if (variation) {
            throw new IllegalArgumentException("Unclosed '(' in move text");
        }
    }

    private static List<Token> tokenize(String moveText) {
        List<Token> tokens = new ArrayList<>();
        int index = 0;
        while (index < moveText.length()) {
            char c = moveText.charAt(index);
            if (Character.isWhitespace(c)) {
                index++;
            } else if (c == '{') {
                int end = moveText.indexOf('}', index + 1);
                if (end < 0) throw new IllegalArgumentException("Unclosed PGN comment");
                tokens.add(new Token(TokenType.COMMENT,
                        moveText.substring(index + 1, end).trim()));
                index = end + 1;
            } else if (c == ';') {
                int end = moveText.indexOf('\n', index + 1);
                if (end < 0) end = moveText.length();
                tokens.add(new Token(TokenType.COMMENT,
                        moveText.substring(index + 1, end).trim()));
                index = end;
            } else if (c == '(' || c == ')') {
                tokens.add(new Token(c == '(' ? TokenType.OPEN : TokenType.CLOSE,
                        Character.toString(c)));
                index++;
            } else {
                int end = index + 1;
                while (end < moveText.length()) {
                    char next = moveText.charAt(end);
                    if (Character.isWhitespace(next) || next == '{' || next == '}'
                            || next == '(' || next == ')' || next == ';') break;
                    end++;
                }
                String word = moveText.substring(index, end);
                TokenType type = word.startsWith("$") ? TokenType.NAG
                        : isResult(word) ? TokenType.RESULT : TokenType.MOVE;
                tokens.add(new Token(type, word));
                index = end;
            }
        }
        return tokens;
    }

    private static String stripMoveNumber(String value) {
        String result = value;
        Matcher matcher = MOVE_NUMBER.matcher(result);
        if (matcher.find()) result = result.substring(matcher.end());
        return result.equals("...") ? "" : result;
    }

    private static String trailingAnnotation(String san) {
        int index = san.length();
        while (index > 0 && (san.charAt(index - 1) == '!' || san.charAt(index - 1) == '?')) index--;
        return san.substring(index);
    }

    private static boolean isResult(String value) {
        return value.equals("1-0") || value.equals("0-1")
                || value.equals("1/2-1/2") || value.equals("*");
    }

    private static Board boardAt(String fen) {
        Board board = new Board();
        board.loadFromFen(fen);
        return board;
    }

    private static List<PgnMoveNode> freezeChildren(MutableNode node) {
        List<PgnMoveNode> result = new ArrayList<>();
        for (MutableNode child : node.children) {
            result.add(new PgnMoveNode(child.san, child.fenAfter, child.comments,
                    child.nags, freezeChildren(child)));
        }
        return List.copyOf(result);
    }

    private static String unescapeTag(String value) {
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) current = current.getCause();
        return current.getMessage() == null ? current.getClass().getSimpleName() : current.getMessage();
    }

    private record RawGame(Map<String, String> tags, String moveText) {
        private RawGame {
            tags = Map.copyOf(tags);
        }
    }

    private record Token(TokenType type, String text) { }
    private enum TokenType { MOVE, COMMENT, NAG, OPEN, CLOSE, RESULT }
    private static final class Cursor { private int index; }

    private static final class MutableNode {
        private final MutableNode parent;
        private final String san;
        private final String fenAfter;
        private final List<String> comments = new ArrayList<>();
        private final List<String> nags = new ArrayList<>();
        private final List<MutableNode> children = new ArrayList<>();

        private MutableNode(MutableNode parent, String san, String fenAfter) {
            this.parent = parent;
            this.san = san;
            this.fenAfter = fenAfter;
        }

        private static MutableNode root(String fen) {
            return new MutableNode(null, "", fen);
        }
    }
}
