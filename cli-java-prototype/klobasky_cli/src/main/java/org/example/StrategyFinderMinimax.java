package org.example;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;

public class StrategyFinderMinimax {

    public boolean minimax(GameBoard gameBoardState, boolean isMaximizingPlayer) {

        if (gameBoardState.isFull()) {
            return gameBoardState.isFirstPlayerWinner();
        }

        if (isMaximizingPlayer) {
            boolean bestValue = false; // to je ako -infinity
            for (Sausage move : gameBoardState.getAllPossibleMoves()) { // pojde to asi aj s O(1) priestorovou
                gameBoardState.addSausage(move);
                boolean value = minimax(gameBoardState, false);
                gameBoardState.removeLastSausage();
                bestValue = bestValue || value; // maximize
            }
            return bestValue;
        }

        else {
            boolean bestValue = true; // to je ako +infinity
            for (Sausage move : gameBoardState.getAllPossibleMoves()) {
                gameBoardState.addSausage(move);
                boolean value = minimax(gameBoardState, true);
                gameBoardState.removeLastSausage();
                bestValue = bestValue && value; // minimize
            }
            return bestValue;
        }
    }
}
