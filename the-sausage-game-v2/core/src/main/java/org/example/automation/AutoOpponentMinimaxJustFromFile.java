package org.example.automation;

import lombok.Getter;
import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.entities.Strategy;
import org.example.exceptions.StrategyMoveNotFoundException;

import java.io.FileNotFoundException;

public class AutoOpponentMinimaxJustFromFile implements AutoOpponent {

    @Getter private Strategy moves = null;

    public AutoOpponentMinimaxJustFromFile(int x, int y, boolean isFirst) {
        try {
            this.moves = StrategyFilesRepository.getStrategy(x, y, isFirst);
        } catch (FileNotFoundException e) {
//            System.err.println("The strategy for board " + x + "x" + y + ", for player " + isFirst + " (true for first) was not found");
            System.err.println("The file " + e.getMessage() + " was not found.");
        }
    }

    @Override
    public Sausage getNextMove(GameBoard g) throws StrategyMoveNotFoundException {
        return moves.getBestMoveFor(g);
    }
}
