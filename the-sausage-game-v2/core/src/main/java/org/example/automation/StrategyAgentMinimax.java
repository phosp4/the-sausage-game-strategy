package org.example.automation;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.exceptions.StrategyMoveNotFoundException;
import org.example.utils.BitEncoder;
import org.example.utils.FileHandlingUtil;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

public class StrategyAgentMinimax implements StrategyAgent {

    private Map<Long, Long> moves = null;

    public StrategyAgentMinimax(int x, int y, boolean isFirst) {
        
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
            throw new StrategyMoveNotFoundException(g);
        }
    }
}
