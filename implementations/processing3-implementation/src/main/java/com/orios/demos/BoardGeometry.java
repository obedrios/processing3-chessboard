package com.orios.demos;

/** Responsive square-board geometry inside a potentially rectangular canvas. */
public record BoardGeometry(float x, float y, float squareSize, float boardSize) {
    private static final float MAX_MARGIN = 60.0f;
    private static final float MIN_MARGIN = 12.0f;

    public static BoardGeometry fit(int canvasWidth, int canvasHeight) {
        if (canvasWidth <= 0 || canvasHeight <= 0) {
            throw new IllegalArgumentException("Canvas dimensions must be positive");
        }
        float shortestSide = Math.min(canvasWidth, canvasHeight);
        float margin = Math.min(MAX_MARGIN, Math.max(MIN_MARGIN, shortestSide * 0.10f));
        float boardSize = Math.max(8.0f, shortestSide - margin * 2.0f);
        float squareSize = boardSize / 8.0f;
        return new BoardGeometry(
                (canvasWidth - boardSize) / 2.0f,
                (canvasHeight - boardSize) / 2.0f,
                squareSize,
                boardSize);
    }
}
