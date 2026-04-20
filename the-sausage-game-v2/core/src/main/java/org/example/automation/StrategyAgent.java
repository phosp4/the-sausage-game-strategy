package org.example.automation;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;

public interface StrategyAgent {
    public Sausage getNextMove(GameBoard g);
}
