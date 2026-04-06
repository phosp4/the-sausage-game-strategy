package org.example.automation;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.strategy.MinimaxLaunchers;
import org.example.utils.BitEncoder;

import java.util.Map;

public class MinimaxOpponent implements AutonomousOpponent {

    private final Map<Integer, Long> moves;

    public MinimaxOpponent(int x, int y) {
        // hm, ale prveho ci druheho je to strategia? to by sa hodilo vediet...
        moves = MinimaxLaunchers.getStrategyForBoard(x, y);
    }

    @Override
    public Sausage getNextMove(GameBoard g) {
        return BitEncoder.decodeSausage(moves.get(g.hashCode()));
    }
}
