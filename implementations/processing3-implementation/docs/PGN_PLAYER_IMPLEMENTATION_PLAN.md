# PGN Player Implementation Plan

This document tracks the five milestones for adding PGN playback to the
Processing 3 chessboard.

- [x] Milestone 1: immutable PGN model and UTF-8 multi-game reader.
- [x] Milestone 2: variation-aware playback controller and FEN synchronization.
- [x] Milestone 3: PGN file chooser and game-selection dialog.
- [x] Milestone 4: editor/notation/scoresheet tabs and transport controls.
- [x] Milestone 5: automated tests, integration verification, and documentation.

## Supported scope

- Standard chess PGNs, including comments, NAGs, nested RAV variations, and
  games beginning from a `SetUp`/`FEN` position.
- Manual start/previous/next/end navigation.
- Clickable annotated notation and active-line scoresheet.
- The existing position editor is preserved and disabled only while a game is
  loaded.

Chess960 and other chess variants are rejected with a user-facing error.
