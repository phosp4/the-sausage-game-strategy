/**
 * vtedy raz to zahralo strategiu zle - hodilo by sa zistit, ze preco
 */

package org.example.strategy;

import lombok.Getter;
import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Sausage;
import org.example.utils.BitEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MinimaxRunner {

    private final Player p1 = new Player("A");
    private final Player p2 = new Player("B");

    private final Map<GameBoard,Integer> memoVals = new HashMap<>();
    @Getter private final Map<Integer,Long> strategyP1 = new HashMap<>();
    @Getter private final Map<Integer,Long> strategyP2 = new HashMap<>();

    // testing
    @Getter private int ttCallsCount = 0;
//    @Getter

    public int minimaxMemo(GameBoard gameBoard, boolean isMaximizingPlayer) {

//        System.out.println("gamestate: " + CliRendererUtil.gridToString(gameState.getGrid()) + ", isMax: " + isMaximizingPlayer);

        // doplnok na behu v threade
        if (Thread.currentThread().isInterrupted()) {
            return -2; // specialna hodnota na oznacenie prerusenia
        }

        if (memoVals.containsKey(gameBoard)) {
            ttCallsCount++;
            return memoVals.get(gameBoard);
        }

        int returnVal;

        Set<Sausage> moves = MoveGenerator.getPossibleMoves(gameBoard.getGrid(), isMaximizingPlayer ? p1 : p2);

        if (moves.isEmpty()) {
            if (isMaximizingPlayer) {
                return -1; // nema tah, teda vyhrava druhy
            } else {
                return 1;
            }
        }

        if (isMaximizingPlayer) {
            int bestValue = -1; // to je ako -infinity
            for (Sausage move : moves) { // pojde to asi aj s O(1) priestorovou
//                System.out.println(Possible);
                gameBoard.addSausage(move);

                int value = minimaxMemo(gameBoard, false);

                gameBoard.removeSausage(move);
                bestValue = Math.max(value, bestValue);
                if (bestValue == 1) {
                    strategyP1.put(gameBoard.hashCode(), BitEncoder.encodeSausage(move));
                    break;
                }
            }
            returnVal = bestValue;
        }

        else {
            int bestValue = 1; // to je ako +infinity
            for (Sausage move : moves) {
                gameBoard.addSausage(move);

                int value = minimaxMemo(gameBoard, true);

                gameBoard.removeSausage(move);
                bestValue = Math.min(value, bestValue);
                if (bestValue == -1) {
                    strategyP2.put(gameBoard.hashCode(), BitEncoder.encodeSausage(move));
//                    break; // mozeme *si trufnut* predpokladat, ze super si vyberie tuto cestu; jedina dalsia moznost je 1, ale to nam neuskodi - chceme byt pesimisticky (ale pri hladani konkretnej strategie to uz nemozme urobit)
                }
            }
            returnVal = bestValue;
        }

        memoVals.put(gameBoard, returnVal);
        return returnVal;
    }
}
