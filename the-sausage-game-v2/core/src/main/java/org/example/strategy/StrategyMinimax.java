/**
 * originalna implementacia
 */

package org.example.strategy;

import lombok.Getter;
import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Sausage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StrategyMinimax {

    private final Player p1 = new Player("A");
    private final Player p2 = new Player("B");

    private final Map<GameBoard,Integer> memoVals = new HashMap<>();
    @Getter private final Map<Long,Sausage> strategy = new HashMap<>();

    public int minimaxMemo(GameBoard gameBoardState, boolean isMaximizingPlayer) {

        // doplnok na behu v threade
        if (Thread.currentThread().isInterrupted()) {
            return -2; // specialna hodnota na oznacenie prerusenia
        }

        if (memoVals.containsKey(gameBoardState)) {
            return memoVals.get(gameBoardState);
        }

        int returnVal;

        if (gameBoardState.isGameOver()) {
            if (gameBoardState.isFirstPlayerWinner()) {
                return 1;
            } else {
                return -1;
            }
        }

        if (isMaximizingPlayer) {
            int bestValue = -1; // to je ako -infinity
            for (Sausage move : MoveGenerator.getAllPossibleMoves(gameBoardState.getGrid(), p1)) { // pojde to asi aj s O(1) priestorovou
                gameBoardState.addSausage(move);

                int value = minimaxMemo(gameBoardState, false);

                gameBoardState.removeLastSausage();
                bestValue = Math.max(value, bestValue);
                if (bestValue == 1) {
                    strategy.put(GridBitMask.encode(gameBoardState.getGrid()), move);
                    break;
                }
            }
            returnVal = bestValue;
        }

        else {
            int bestValue = 1; // to je ako +infinity
            for (Sausage move : MoveGenerator.getAllPossibleMoves(gameBoardState.getGrid(), p2)) {
                gameBoardState.addSausage(move);

                int value = minimaxMemo(gameBoardState, true);

                gameBoardState.removeLastSausage();
                bestValue = Math.min(value, bestValue);
                if (bestValue == -1) break; // mozeme *si trufnut* predpokladat, ze super si vyberie tuto cestu; jedina dalsia moznost je 1, ale to nam neuskodi - chceme byt pesimisticky (ale pri hladani konkretnej strategie to uz nemozme urobit)
            }
            returnVal = bestValue;
        }

//        memoVals.put(gameBoardState, returnVal);
        return returnVal;
    }

    /**
     * returns the optimal strategy for the first player IF EXISTS
     */
    public static Map<Long, Sausage> getFirstPlayerStrategy(int x, int y) {
        StrategyMinimax sm = new StrategyMinimax();
        GameBoard g = new GameBoard(x, y);
        int whoIsWinner = sm.minimaxMemo(g, true);
        Map<Long, Sausage> firstPlayerStrategy = sm.getStrategy();
        return firstPlayerStrategy;
    }
}
