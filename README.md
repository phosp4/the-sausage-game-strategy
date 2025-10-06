# Project Android

This repository contains the source code for the **The Sausage Game** app developed as part of the VMA course at UPJS. It uses Java with libGDX game engine.

## Structure

For practical purposes, this repository is structured as a monorepo containing multiple separate projects, which are now wired to the same game engine:

- `the-sausage-game/backend` – the platform agnostic game engine, responsible for the board state, move validation and the turn loop.
- `the-sausage-game/core` – the LibGDX based graphical frontend that consumes the backend and renders the experience.
- `the-sausage-game/cli` – a lightweight command line client that shares the exact same backend, useful for testing the rules without a graphical environment.

### Running the clients

- Graphical client: `cd the-sausage-game/the-sausage-game` then `./gradlew :lwjgl3:run`
- Command line client: `cd the-sausage-game/the-sausage-game` then `./gradlew :cli:run`
