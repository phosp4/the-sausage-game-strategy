/**
 * TODO dokoncit treba
 */

package org.example.automation;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.entities.Strategy;
import org.example.strategy_minimax.MinimaxBitboard;

public class AutoOpponentMinimaxLive implements AutoOpponent {

    private Strategy moves;

    public AutoOpponentMinimaxLive(GameBoard gameBoard, int winner) {
        MinimaxBitboard mb = new MinimaxBitboard();
//        mb.minimaxMemoStart(gameBoard, 0, winner, , Integer.MAX_VALUE);
    }

    @Override
    public Sausage getNextMove(GameBoard g) {
        return null;
    }
}
