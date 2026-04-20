package org.example.automation;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.entities.Strategy;
import org.example.exceptions.StrategyMoveNotFoundException;
import org.example.utils.BitEncoder;
import org.example.utils.FileHandlingUtil;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class StrategyAgentMinimax implements StrategyAgent {

    private Strategy moves = null;

    public StrategyAgentMinimax(int x, int y, boolean isFirst) {
        try {
            this.moves = StrategyRepository.getStrategy(x, y, isFirst);
        } catch (FileNotFoundException e) {
            System.err.println("The strategy for board " + x + "x" + y + ", for player " + isFirst + " (true for first) was not found");
        }
    }

    @Override
    public Sausage getNextMove(GameBoard g) {
        return moves.getBestMoveFor(g);
    }
}
