package org.example.automation;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.strategy.MinimaxLaunchers;
import org.example.utils.BitEncoder;
import org.example.utils.FileHandlingUtil;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.Map;

public class MinimaxOpponent implements AutonomousOpponent {

    private Map<Integer, Long> moves = null;

    public MinimaxOpponent(int x, int y) {
        // hm, ale prveho ci druheho je to strategia? to by sa hodilo vediet...
        try {
            int smaller = x;
            int bigger = y;
            if (x > y) {
                smaller = y;
                bigger = x;
            }
            String fileName = FileHandlingUtil.STRATEGY_PATH + "/strategy_" + smaller + "x" + bigger + ".csv";
            moves = FileHandlingUtil.loadStrategyCSV(fileName);
        } catch (IOException e) {
            System.err.println("The file was not found");
        }
    }

    @Override
    public Sausage getNextMove(GameBoard g) {
        return BitEncoder.decodeSausage(moves.get(g.hashCode()));
    }
}
