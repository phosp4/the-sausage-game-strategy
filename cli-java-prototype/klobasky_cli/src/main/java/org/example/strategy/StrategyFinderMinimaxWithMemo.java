/**
 * Poznámky:
 * toto bolo implementované len rýchlo a chvatne, viac vecí len skopírovaných
 * nebrať to teda moc vážne a radšej to urobiť ešte raz a poriadnejšie
 * respektíve poriadne si to prejsť a či to je dobre - minimax aj memoizácia
 * tiež tam ešte nie implementované alpha beta pruning
 * a tu mi to chat kritizoval: https://chatgpt.com/c/68e99ab6-3334-8331-a87a-ecde1d230074
 * každopádne, výpočet pre 6x6 to zrýchlilo - vrátilo false
 */

package org.example.strategy;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.utils.CliRendererUtil;
import org.example.utils.MoveGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StrategyFinderMinimaxWithMemo {

    @EqualsAndHashCode
    @AllArgsConstructor
    private class MemoCall {
        GameBoard gb;
        Boolean isMax;
    }

    private Map<MemoCall,Boolean> memoVals = new HashMap<>();

    public static void main(String[] args) {
        GameBoard gameBoard = new GameBoard(6,6);
        StrategyFinderMinimaxWithMemo sfm = new StrategyFinderMinimaxWithMemo();

        boolean canFirstPlayerWin = sfm.minimax(gameBoard, true);
        System.out.println("Can first player win? " + canFirstPlayerWin);
    }

    public boolean minimax(GameBoard gameBoardState, boolean isMaximizingPlayer) {

        if (gameBoardState.isFull()) {
            return gameBoardState.isFirstPlayerWinner();
        }

        if (isMaximizingPlayer) {
            boolean bestValue = false; // to je ako -infinity
            Set<Sausage> allPossibleMoves = MoveGenerator.getAllPossibleMoves(gameBoardState.getGrid());
            for (Sausage move : allPossibleMoves) { // pojde to asi aj s O(1) priestorovou
                gameBoardState.addSausage(move);
                System.out.println(CliRendererUtil.gridToString(gameBoardState.getGrid()));

                boolean value;
                MemoCall call = new MemoCall(gameBoardState, !isMaximizingPlayer);
                if (memoVals.get(call) != null) {
                    value = memoVals.get(call);
                } else {
                    value = minimax(gameBoardState, false);
                    memoVals.put(call, value);
                }
                gameBoardState.removeLastSausage();
                bestValue = bestValue || value; // maximize
            }
            return bestValue;
        }

        else {
            boolean bestValue = true; // to je ako +infinity
            Set<Sausage> allPossibleMoves = MoveGenerator.getAllPossibleMoves(gameBoardState.getGrid());
            for (Sausage move : allPossibleMoves) {
                gameBoardState.addSausage(move);
                System.out.println(CliRendererUtil.gridToString(gameBoardState.getGrid()));

                boolean value;
                MemoCall call = new MemoCall(gameBoardState, true);
                if (memoVals.get(call) != null) {
                    value = memoVals.get(call);
                } else {
                    value = minimax(gameBoardState, true);
                    memoVals.put(call, value);
                }
                gameBoardState.removeLastSausage();
                bestValue = bestValue && value; // minimize
            }
            return bestValue;
        }
    }
}
