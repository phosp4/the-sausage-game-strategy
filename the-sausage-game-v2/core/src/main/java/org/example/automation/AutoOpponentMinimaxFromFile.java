package org.example.automation;

import lombok.Getter;
import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.entities.Strategy;

import java.io.FileNotFoundException;

public class AutoOpponentMinimaxFromFile implements AutoOpponent {

    @Getter private Strategy moves = null;

    public AutoOpponentMinimaxFromFile(int x, int y, boolean isFirst) {
        try {
            this.moves = StrategyRepository.getStrategy(x, y, isFirst);
        } catch (FileNotFoundException e) {
//            System.err.println("The strategy for board " + x + "x" + y + ", for player " + isFirst + " (true for first) was not found");
            System.err.println("The file " + e.getMessage() + " was not found.");
        }
    }

    @Override
    public Sausage getNextMove(GameBoard g) {
        return moves.getBestMoveFor(g);
    }
}
