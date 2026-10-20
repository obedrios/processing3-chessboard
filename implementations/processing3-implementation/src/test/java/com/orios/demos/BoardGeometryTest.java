package com.orios.demos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BoardGeometryTest {
    @Test
    void preservesASquareBoardAndCentersItInWideCanvas() {
        BoardGeometry geometry = BoardGeometry.fit(900, 600);
        assertEquals(480.0f, geometry.boardSize());
        assertEquals(60.0f, geometry.squareSize());
        assertEquals(210.0f, geometry.x());
        assertEquals(60.0f, geometry.y());
    }

    @Test
    void growsAndShrinksWithShortestCanvasDimension() {
        BoardGeometry small = BoardGeometry.fit(400, 500);
        BoardGeometry large = BoardGeometry.fit(1000, 800);
        assertEquals(small.squareSize() * 8.0f, small.boardSize());
        assertEquals(large.squareSize() * 8.0f, large.boardSize());
        assertTrue(large.squareSize() > small.squareSize());
    }
}
