package org.example;

import org.example.entities.GameBoard;

public class GameEngine {
    GameBoard gameBoard;
    TurnManager turnManager;

    public GameEngine(GameBoard grid, TurnManager turnManager) {
        this.gameBoard = grid;
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
