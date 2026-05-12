package org.example.automation;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.exceptions.StrategyMoveNotFoundException;

// pred tym to bolo StrategyAgent
public interface AutoOpponent {
    public Sausage getNextMove(GameBoard g) throws StrategyMoveNotFoundException;
}
