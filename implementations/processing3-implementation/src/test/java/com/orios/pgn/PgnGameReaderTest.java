package com.orios.pgn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PgnGameReaderTest {
    private final PgnGameReader reader = new PgnGameReader();

    @Test
    void readsAllBundledPgnFilesAndTheirMetadata() throws Exception {
        Path folder = Path.of("docs", "pgns");
        for (Path path : Files.list(folder).filter(p -> p.toString().endsWith(".pgn")).toList()) {
            List<PgnGame> games = reader.read(path);
            assertFalse(games.isEmpty(), path.toString());
            assertFalse(games.get(0).white().isBlank());
            assertFalse(games.get(0).black().isBlank());
            assertFalse(games.get(0).continuations().isEmpty());
        }
    }

    @Test
    void buildsNavigableNestedVariationsAndAnnotations(@TempDir Path directory) throws Exception {
        Path pgn = directory.resolve("variations.pgn");
        Files.writeString(pgn, """
                [Event "Tree"]
                [Site "Test"]
                [Date "2026.10.20"]
                [Round "1"]
                [White "White"]
                [Black "Black"]
                [Result "*"]

                1. e4 {main} e5 (1... c5 $1 (1... e6)) 2. Nf3 *
                """);

        PgnGame game = reader.read(pgn).get(0);
        PgnMoveNode e4 = game.continuations().get(0);
        assertEquals("main", e4.comments().get(0));
        assertEquals(List.of("e5", "c5", "e6"),
                e4.continuations().stream().map(PgnMoveNode::san).toList());
        assertEquals("$1", e4.continuations().get(1).nags().get(0));
        assertEquals("Nf3", e4.continuations().get(0).continuations().get(0).san());
    }

    @Test
    void supportsSetupFen(@TempDir Path directory) throws Exception {
        Path pgn = directory.resolve("fen.pgn");
        Files.writeString(pgn, """
                [Event "FEN"]
                [White "A"]
                [Black "B"]
                [Result "1-0"]
                [SetUp "1"]
                [FEN "5q1k/2p3Rp/3rnP2/1p3p2/2Q5/PP2P3/1B3P1P/7K w - - 0 1"]

                1. Qxe6 Rxe6 2. f7 1-0
                """);
        PgnGame game = reader.read(pgn).get(0);
        assertTrue(game.initialFen().startsWith("5q1k"));
        assertEquals("Qxe6", game.continuations().get(0).san());
    }

    @Test
    void rejectsUnsupportedVariantsWithoutReturningPartialGames(@TempDir Path directory) throws Exception {
        Path pgn = directory.resolve("960.pgn");
        Files.writeString(pgn, """
                [Event "Unsupported"]
                [Variant "Chess960"]
                [White "A"]
                [Black "B"]
                [Result "*"]

                *
                """);
        assertThrows(PgnReadException.class, () -> reader.read(pgn));
    }
}
