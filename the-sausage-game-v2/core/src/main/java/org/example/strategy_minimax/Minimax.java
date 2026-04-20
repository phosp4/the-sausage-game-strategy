package org.example.strategy_minimax;

import lombok.Getter;
import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Sausage;
import org.example.utils.BitEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Minimax {

    private final Player p1 = new Player("A");
    private final Player p2 = new Player("B");

    private final Map<GameBoard,Integer> memoVals = new HashMap<>();
    // tu technicky mozem pouzit Strategy triedu
    @Getter private final Map<Long,Long> strategyP1 = new HashMap<>();
    @Getter private final Map<Long,Long> strategyP2 = new HashMap<>();
    private Set<Sausage> allPossibleMoves = null;
//    private Set<Sausage> currentlyPossibleMoves = null;

    // testing
    @Getter private int ttCallsCount = 0;

    public int minimaxMemoStart(GameBoard gameBoard) {
        allPossibleMoves = MoveGenerator.getPossibleMoves(gameBoard.getGrid());
//        currentlyPossibleMoves = allPossibleMoves;
        return minimaxMemo(gameBoard, true);
    }

    private int minimaxMemo(GameBoard gameBoard, boolean isMaximizingPlayer) {

        // doplnok na behu v threade
        if (Thread.currentThread().isInterrupted()) {
            return -2; // specialna hodnota na oznacenie prerusenia
        }

        if (memoVals.containsKey(gameBoard)) {
            ttCallsCount++;
            return memoVals.get(gameBoard);
        }

        int returnVal;

        if (isMaximizingPlayer) {
            int bestValue = -1; // to je ako -infinity
            boolean atLeastOne = false;
            for (Sausage move : allPossibleMoves) {
                if (gameBoard.tryAddingSausageMinimax(move)) {
                    atLeastOne = true;

                    int value = minimaxMemo(gameBoard, false);
                    gameBoard.removeSausage(move);
                    bestValue = Math.max(value, bestValue);

                    if (bestValue == 1) {
                        strategyP1.put(BitEncoder.sausageGridToLongBitboard(gameBoard.getGrid()), BitEncoder.encodeSausageWithOffsets(move));
                        break;
                    }
                }
            }
            // game over check
            if (!atLeastOne) {
                return -1; // nema tah, teda vyhrava druhy
            }
            returnVal = bestValue;
        }

        else {
            int bestValue = 1; // to je ako +infinity
            boolean atLeastOne = false;

            for (Sausage move : allPossibleMoves) {
                if (gameBoard.tryAddingSausageMinimax(move)) {
                    atLeastOne = true;

                    int value = minimaxMemo(gameBoard, true);
                    gameBoard.removeSausage(move);
                    bestValue = Math.min(value, bestValue);

                    if (bestValue == -1) {
                        strategyP2.put(BitEncoder.sausageGridToLongBitboard(gameBoard.getGrid()), BitEncoder.encodeSausageWithOffsets(move));
                        break; // mozeme *si trufnut* predpokladat, ze super si vyberie tuto cestu; jedina dalsia moznost je 1, ale to nam neuskodi - chceme byt pesimisticky (ale pri hladani konkretnej strategie to uz nemozme urobit)
                    }
                }
            }
            // game over check
            if (!atLeastOne) {
                return 1; // nema tah, teda vyhrava prvy
            }
            returnVal = bestValue;
        }

        memoVals.put(gameBoard, returnVal);
        return returnVal;
    }
}
