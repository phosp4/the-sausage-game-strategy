/**
 * jednoduchy objekt, ktory drzi data a vytvara vrstvu abstrakcie
 */

package org.example.entities;

import lombok.Data;
import org.example.strategy_minimax.MoveGenerator;
import org.example.strategy_minimax.SymmetryUtil;
import org.example.utils.BitEncoder;

import java.io.*;
import java.util.Set;

@Data
public class Strategy {

    private int boardX;
    private int boardY;

    /*
     * v tejto implementacii si ukladame loosing - "kriticke" pozicie
     * do tych chceme dostat supera
     */
    private Set<Long> loosingBoards;
    private boolean isForFirstPlayer; // mozno ani netreba??
    private boolean isCanonized;

    public Strategy(int x, int y, Set<Long> precalculatedMoves, boolean isForFirstPlayer) {
        this(x, y, precalculatedMoves, isForFirstPlayer, false);
    }

    public Strategy(int x, int y, Set<Long> precalculatedMoves, boolean isForFirstPlayer, boolean isCanonized) {
        boardX = x;
        boardY = y;
        this.loosingBoards = precalculatedMoves;
        this.isForFirstPlayer = isForFirstPlayer;
        this.isCanonized = isCanonized;
    }

    public Sausage getBestMoveFor(GameBoard g) {
//        long gridLong = BitEncoder.sausageGridToLongBitboard(g.getGrid());
        GameBoard g2 = g.clone();

        Set<Sausage> possibleMoves = MoveGenerator.getPossibleMoves(g2.getGrid());

        for (Sausage move : possibleMoves) {
            g2.addSausage(move);
            if (isThisPositionLoosing(g2)) {
                System.out.println("found a move: " + move.toString());
                return move;
            }
            g2.removeSausage(move);
        }

        System.err.println("cannot find a valid move");
        return null;
    }

    public boolean isThisPositionLoosing(GameBoard g) {
        long gridLong = BitEncoder.sausageGridToLongBitboard(g.getGrid());

        // toto je dolezite - kvoli tomuto mozeme kanonizovat pri ukladani strategie
        if (isCanonized) {
            gridLong = SymmetryUtil.canonize(gridLong, boardX, boardY);
        }

        return loosingBoards.contains(gridLong);
    }

    public void writeStrategyToTxt(int x, int y) {
        String fileName = "strategy_" + x + "x" + y + (isForFirstPlayer ? "_p1" : "_p2") + ".txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            for (Long boardPosition : loosingBoards) {
                writer.println(boardPosition);
            }
            System.out.println("Strategy written to file: " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing strategy to file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
