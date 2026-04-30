package org.example.automation;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.strategy_minimax.MoveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AutoOpponentRandom implements AutoOpponent {

    @Override
    public Sausage getNextMove(GameBoard g) {
        List<Sausage> moves = new ArrayList<>(MoveGenerator.getPossibleMoves(g.getGrid()));
        int idx = ThreadLocalRandom.current().nextInt(moves.size());
        return moves.get(idx);
    }
}
