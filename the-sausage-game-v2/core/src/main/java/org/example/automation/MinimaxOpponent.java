package org.example.automation;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.utils.BitEncoder;
import org.example.utils.FileHandlingUtil;

import java.io.IOException;
import java.util.Map;

public class MinimaxOpponent implements AutonomousOpponent {

    private Map<Long, Long> moves = null;

    public MinimaxOpponent(int x, int y) {
        // hm, ale prveho ci druheho je to strategia? to by sa hodilo vediet...
        try {
            String fileName = FileHandlingUtil.STRATEGY_PATH + "/strategy_" + x + "x" + y + ".csv";
            moves = FileHandlingUtil.loadStrategyCSV(fileName);
        } catch (IOException e) {
            System.err.println("The file was not found");
        }
    }

    @Override
    public Sausage getNextMove(GameBoard g) {
        return BitEncoder.decodeSausageWithOffsets(moves.get(BitEncoder.sausageGridToLongBitboard(g.getGrid())));
    }
}
