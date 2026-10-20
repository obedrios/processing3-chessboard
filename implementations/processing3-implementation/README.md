# Processing 3 Chessboard

Desktop chessboard and position editor built with Java, Swing, and Processing 3.

![Processing 3 Chessboard](docs/images/chess_demo.png)

## Requirements

- JDK 17 or newer
- Maven 3.9 or newer

## Build and test

```shell
mvn clean verify
```

## Run

```shell
mvn exec:java
```

## Features

- Load and export complete six-field FEN positions.
- Validate piece placement, active color, castling rights, en-passant target,
  halfmove clock, and fullmove number.
- Select squares and place white or black pieces.
- Clear selections or reset the board to the initial chess position.
- Toggle coordinate labels and selected-square highlighting.

The module intentionally remains on `org.processing:core:3.3.7`, the newest
Processing 3 core artifact available from Maven Central.
