/**
 * vtedy raz to zahralo strategiu zle - hodilo by sa zistit, ze preco
 */

package org.example.strategy;

import lombok.Getter;
import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Sausage;
import org.example.utils.BitEncoder;
import org.example.utils.CliRendererUtil;
import org.example.utils.ZobristHasher;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MinimaxRunner {

    private final Player p1 = new Player("A");
    private final Player p2 = new Player("B");

    private final Map<Long,Integer> memoVals = new HashMap<>();
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

        if (memoVals.containsKey(gameBoard.getZobristHash())) {
            ttCallsCount++;
            return memoVals.get(gameBoard.getZobristHash());
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
                        strategyP1.put(gameBoard.getZobristHash(), BitEncoder.encodeSausage(move));
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
                        strategyP2.put(gameBoard.getZobristHash(), BitEncoder.encodeSausage(move));
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

        memoVals.put(gameBoard.getZobristHash(), returnVal);
        return returnVal;
    }
}
