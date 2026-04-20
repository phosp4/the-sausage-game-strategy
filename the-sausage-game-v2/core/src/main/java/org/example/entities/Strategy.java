/**
 * jednoduchy objekt, ktory drzi data a vytvara vrstvu abstrakcie
 */

package org.example.entities;

import lombok.Data;
import org.example.exceptions.StrategyMoveNotFoundException;
import org.example.utils.BitEncoder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Strategy {

    private Map<Long,Long> precalculatedMoves;
    private boolean isForFirstPlayer; // mozno ani netreba??

    public Strategy(Map<Long, Long> precalculatedMoves, boolean isForFirstPlayer) {
        this.precalculatedMoves = precalculatedMoves;
        this.isForFirstPlayer = isForFirstPlayer;
    }

    public boolean hasMoveFor(GameBoard g) {
        long gridLong = BitEncoder.sausageGridToLongBitboard(g.getGrid());
        return precalculatedMoves.containsKey(gridLong);
    }

    public Sausage getBestMoveFor(GameBoard g) {
        long gridLong = BitEncoder.sausageGridToLongBitboard(g.getGrid());
        Long encodedMove = precalculatedMoves.get(gridLong);

        if (encodedMove != null) {
            return BitEncoder.decodeSausageWithOffsets(encodedMove);
        } else {
            throw new StrategyMoveNotFoundException(g);
        }
    }
}
