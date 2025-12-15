/**
 * originalna implementacia
 */

package org.example.strategy;

import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Sausage;
import org.example.utils.MoveGenerator;

import java.util.HashMap;
import java.util.Map;

public class StrategyFinderMinimax {

    private Player p1 = new Player("A");
    private Player p2 = new Player("B");

    private Map<MemoCall,Integer> memoVals = new HashMap<>();

    /**
     * @param gameBoardState
     * @param isMaximizingPlayer
     * @return vyhra prvy hrac? 1 je ano, 0 je nie
     * technicky s if-kami to je vlastne alpha beta pruning, mozno sa oplati este jedno bez toho
     */
    public int minimax(GameBoard gameBoardState, boolean isMaximizingPlayer) {

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
//                System.out.println(CliRendererUtil.gridToString(gameBoardState.getGrid()));
                int value = minimax(gameBoardState, false);
                gameBoardState.removeLastSausage();
                bestValue = Math.max(value, bestValue);
                if (bestValue == 1) break;
            }
            return bestValue;
        }

        else {
            int bestValue = 1; // to je ako +infinity
            for (Sausage move : MoveGenerator.getAllPossibleMoves(gameBoardState.getGrid(), p2)) {
                gameBoardState.addSausage(move);
//                System.out.println(CliRendererUtil.gridToString(gameBoardState.getGrid()));
                int value = minimax(gameBoardState, true);
                gameBoardState.removeLastSausage();
                bestValue = Math.min(value, bestValue);
                if (bestValue == -1) break; // mozeme *si trufnut* predpokladat, ze super si vyberie tuto cestu; jedina dalsia moznost je 1, ale to nam neuskodi - chceme byt pesimisticky
            }
            return bestValue;
        }
    }

    public int minimaxMemo(GameBoard gameBoardState, boolean isMaximizingPlayer) {

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

    public int minimaxAB(GameBoard gameBoardState, boolean isMaximizingPlayer, int alpha, int beta) {

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
                int value = minimaxAB(gameBoardState, false, alpha, beta);
                gameBoardState.removeLastSausage();
                bestValue = Math.max(value, bestValue);
                alpha = Math.max(alpha, value);
                if (beta <= alpha) break;
                if (bestValue == 1) break; // jednoducha optimalizacia -staci nam jeden sposob na minimalizaciu / maximalizaciu
            }
            return bestValue;
        }

        else {
            int bestValue = 1; // to je ako +infinity
            for (Sausage move : MoveGenerator.getAllPossibleMoves(gameBoardState.getGrid(), p2)) {
                gameBoardState.addSausage(move);
                int value = minimaxAB(gameBoardState, true, alpha, beta);
                gameBoardState.removeLastSausage();
                bestValue = Math.min(value, bestValue);
                beta = Math.min(beta, value);
                if (beta <= alpha) break;
                if (bestValue == -1) break; // mozeme *si trufnut* predpokladat, ze super si vyberie tuto cestu; jedina dalsia moznost je 1, ale to nam neuskodi - chceme byt pesimisticky
            }
            return bestValue;
        }
    }
}
