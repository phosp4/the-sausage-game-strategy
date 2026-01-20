/**
 * originalna implementacia
 */

package org.example.strategy;

import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Sausage;

import java.util.HashMap;
import java.util.Map;

public class StrategyMinimax {

    private Player p1 = new Player("A");
    private Player p2 = new Player("B");

    private Map<MemoCall,Integer> memoVals = new HashMap<>();

    public Map<MemoCall,Integer> minimaxMemoVals(GameBoard gameBoardState, boolean isMaximizingPlayer) {
        memoVals.clear();
        minimaxMemo(gameBoardState, isMaximizingPlayer);
        return memoVals;
    }

    public int minimaxMemo(GameBoard gameBoardState, boolean isMaximizingPlayer) {

        // doplnok na behu v threade
        if (Thread.currentThread().isInterrupted()) {
            return -2; // specialna hodnota na oznacenie prerusenia
        }

        MemoCall mc = new MemoCall(gameBoardState, isMaximizingPlayer);
        if (memoVals.containsKey(mc)) {
            return memoVals.get(mc);
        }

        int returnVal;

        if (gameBoardState.isGameOver()) { // toto je tu mozno zbytocne...
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
                if (bestValue == 1) break; // jednoducha optimalizacia -staci nam jeden sposob na minimalizaciu / maximalizaciu
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

        memoVals.put(mc, returnVal);
        return returnVal;
    }

}
