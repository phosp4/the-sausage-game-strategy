package org.example.automation;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;

// pred tym to bolo StrategyAgent
public interface AutoOpponent {
    public Sausage getNextMove(GameBoard g);
}
