package org.example;

import org.example.entities.Grid;
import org.example.entities.Sausage;

public class StrategyFinderMinimax {

    public boolean minimax(Grid gridState, boolean isMaximizingPlayer) {

        if (gridState.isFull()) {
            return gridState.maxPlayerWins();
        }

        if (isMaximizingPlayer) {
            boolean bestValue = false; // to je ako -infinity
            for (Sausage move : gridState.getAllPossibleMoves()) { // pojde to asi aj s O(1) priestorovou
                gridState.addSausage(move);
                boolean value = minimax(gridState, false);
                gridState.removeLastSausage();
                bestValue = bestValue || value; // maximize
            }
            return bestValue;
        }

        else {
            boolean bestValue = true; // to je ako +infinity
            for (Sausage move : gridState.getAllPossibleMoves()) {
                gridState.addSausage(move);
                boolean value = minimax(gridState, true);
                gridState.removeLastSausage();
                bestValue = bestValue && value; // minimize
            }
            return bestValue;
        }
    }
}
