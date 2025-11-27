package org.example.automation;

import org.example.engine.Game;
import org.example.entities.GameBoard;
import org.example.entities.Sausage;

public interface AutonomousOpponent {

    public Sausage getAMove(Sausage[][] grid);
}
