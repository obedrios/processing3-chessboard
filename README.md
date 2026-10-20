# Processing 3 Chessboard

An experimental repository for building a desktop chessboard, position editor,
and PGN game viewer while exploring different languages and UI technologies.

The current reference implementation is written in Java 17 with Swing and
Processing 3. It provides an interactive, responsive chessboard together with
FEN editing and variation-aware PGN playback.

![Processing 3 Chessboard](implementations/processing3-implementation/docs/images/chess_demo.png)

## Current implementation

The actively maintained implementation lives in
[`implementations/processing3-implementation`](implementations/processing3-implementation/).

Its main capabilities include:

- Loading, validating, editing, and exporting complete six-field FEN
  positions.
- Selecting squares, placing pieces, moving pieces, and confirming exchanges
  on occupied destination squares.
- A responsive board whose squares, pieces, labels, and interaction geometry
  resize while preserving a centered 1:1 aspect ratio.
- Loading UTF-8 PGN files containing one or multiple games.
- Selecting games from a sortable metadata table.
- Replaying main lines and nested variations with synchronized board,
  notation, and scoresheet views.
- Navigating games manually with start, previous, next, and end controls.
- Supporting comments, NAGs, RAV variations, and `SetUp`/`FEN` starting
  positions for standard chess.
- Copying the current playback position as a FEN string with a single click.

See the [implementation README](implementations/processing3-implementation/README.md)
for detailed usage and supported PGN behavior.

## Repository layout

```text
.
├── implementations/
│   └── processing3-implementation/   Java, Swing, and Processing 3 application
├── docs/                             Shared project documentation
├── LICENSE                           GNU General Public License v3
└── README.md                         Repository overview
```

The Processing 3 implementation contains its own source code, tests, bundled
piece assets, screenshots, and sample Deep Blue–Kasparov PGN files.

## Requirements

- JDK 17 or newer
- Maven 3.9 or newer
- A graphical desktop environment for running the Swing application

## Build and test

From the repository root:

```shell
cd implementations/processing3-implementation
mvn clean verify
```

## Run

```shell
cd implementations/processing3-implementation
mvn exec:java
```

To try the PGN player, open the application's **File** menu, choose
**Open PGN…**, and select one of the files under
`implementations/processing3-implementation/docs/pgns`.

## Technology

- Java 17
- Swing
- Processing 3.3.7
- chesslib 1.3.7
- Maven
- JUnit

Processing intentionally remains on the 3.x line so this implementation can
serve as a focused example of embedding a Processing canvas inside a Swing
desktop application.

## Future ideas

- Explore connecting the chessboard to an open-source chess engine for analysis and computer play.
- Research alternative implementations using Kotlin, Clojure, and Angular.

## License

This repository is licensed under the
[GNU General Public License v3.0](LICENSE).
