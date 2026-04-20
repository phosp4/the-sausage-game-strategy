/**
 * nieco je tu asi zle - stale mi to vyberalo randomly...
 * ale nevadi, toto je dodatok, ktory tam ani nemusi byt!
 */

package org.example.automation;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.strategy.MoveGenerator;
import org.example.utils.BitEncoder;
import org.example.utils.FileHandlingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class StrategyAgentSemiRandomMinimax implements StrategyAgent {

    private Map<Long, Long> moves = null;

    public StrategyAgentSemiRandomMinimax(int x, int y, boolean isFirst) {
        String fileName = FileHandlingUtil.STRATEGY_PATH + "/strategy_" + x + "x" + y;
        if (isFirst) fileName += "_p1.csv";
        else fileName += "_p2.csv";
        try {
            moves = FileHandlingUtil.loadStrategyCSV(fileName);
        } catch (IOException e) {
            System.err.println("The file " + fileName  + " was not found");
        }
    }

    @Override
    public Sausage getNextMove(GameBoard g) {
        long gridLong = BitEncoder.sausageGridToLongBitboard(g.getGrid());
        if (moves.containsKey(gridLong)) {
            return BitEncoder.decodeSausageWithOffsets(moves.get(gridLong));
        } else {
            // namiesto vynimky vyberieme nahodny tah
            List<Sausage> moves = new ArrayList<>(MoveGenerator.getPossibleMoves(g.getGrid()));
            int idx = ThreadLocalRandom.current().nextInt(moves.size());
            System.out.println("Randomly selected!");
            return moves.get(idx);
        }
    }
}
