package com.orios.pgn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GamePlaybackControllerTest {
    @Test
    void navigatesMainLineAndSelectedVariation(@TempDir Path directory) throws Exception {
        Path pgn = directory.resolve("game.pgn");
        Files.writeString(pgn, """
                [Event "Playback"]
                [White "A"]
                [Black "B"]
                [Result "*"]

                1. e4 e5 (1... c5 2. Nf3) 2. Nf3 *
                """);
        PgnGame game = new PgnGameReader().read(pgn).get(0);
        GamePlaybackController controller = new GamePlaybackController();
        controller.load(game);

        assertTrue(controller.state().atStart());
        controller.next();
        controller.next();
        assertEquals("e5", controller.state().currentMove().san());
        controller.previous();
        PgnMoveNode c5 = game.continuations().get(0).continuations().get(1);
        controller.selectVariation(c5);
        assertEquals("c5", controller.state().currentMove().san());
        assertEquals(3, controller.state().activeLine().size());
        controller.goToEnd();
        assertEquals("Nf3", controller.state().currentMove().san());
        assertTrue(controller.state().atEnd());
    }
}
