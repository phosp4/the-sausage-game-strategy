/**
 * jednoduchy objekt, ktory drzi data a vytvara vrstvu abstrakcie
 */

package org.example.entities;

import lombok.Data;
import org.example.strategy_minimax.MoveGenerator;
import org.example.utils.BitEncoder;

import java.io.*;
import java.util.Set;

@Data
public class Strategy {

    private Set<Long> winningBoards;
    private boolean isForFirstPlayer; // mozno ani netreba??

    public Strategy(Set<Long> precalculatedMoves, boolean isForFirstPlayer) {
        this.winningBoards = precalculatedMoves;
        this.isForFirstPlayer = isForFirstPlayer;
    }

    public boolean hasMoveFor(GameBoard g) {
        long gridLong = BitEncoder.sausageGridToLongBitboard(g.getGrid());
        return winningBoards.contains(gridLong);
    }

    public Sausage getBestMoveFor(GameBoard g) {
//        long gridLong = BitEncoder.sausageGridToLongBitboard(g.getGrid());
        GameBoard g2 = g.clone();

        Set<Sausage> possibleMoves = MoveGenerator.getPossibleMoves(g2.getGrid());

        for (Sausage move : possibleMoves) {
            g2.addSausage(move);
            if (hasMoveFor(g2)) {
                System.out.println("found a move: " + move.toString());
                return move;
            }
            g2.removeSausage(move);
        }

        System.err.println("cannot find a valid move");
        return null;

//        Long encodedMove = precalculatedMoves.get(gridLong);
//
//        if (encodedMove != null) {
//            return BitEncoder.decodeSausageWithOffsets(encodedMove);
//        } else {
//            throw new StrategyMoveNotFoundException(g);
//        }
    }

    public void writeStrategyToTxt(int x, int y) {
        String fileName = "strategy_" + x + "x" + y + (isForFirstPlayer ? "_p1" : "_p2") + ".txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            for (Long boardPosition : winningBoards) {
                writer.println(boardPosition);
            }
            System.out.println("Strategy written to file: " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing strategy to file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
