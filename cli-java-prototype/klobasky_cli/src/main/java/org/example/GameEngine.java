package org.example;

import org.example.entities.Grid;

public class GameEngine {
    Grid grid;
    TurnManager turnManager;

    public GameEngine(Grid grid, TurnManager turnManager) {
        this.grid = grid;
        this.turnManager = turnManager;
    }

    public void start() {
        boolean gameOver = false;
        while (!gameOver) {
            // render grid and UI
            // handle user input
            // update game state
            // check for win condition
            // ...
        }
    }
}
