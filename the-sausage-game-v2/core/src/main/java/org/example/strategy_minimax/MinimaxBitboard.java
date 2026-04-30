package org.example.strategy_minimax;

import lombok.Getter;
import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.utils.BitEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MinimaxBitboard {

    private long[] allPossibleMoves;
    private TranspositionTable tt;

    // strategy - treba domysliet, ci tam ukladat move, lebo tu je move cela plocha...
    // najskor urobit funkciu BoardWithOneSausageToSausage
    @Getter private Map<Long,Long> strategyP1;
    @Getter private Map<Long,Long> strategyP2;

    // benchmarks
    @Getter private long ttCallsCount;
    @Getter private long nodesInvestigatedMax;
    @Getter private long nodesInvestigatedMin;

    public int minimaxMemoStart(GameBoard gameBoard) {
        // just for an empty grid - all options
        Set<Sausage> allPossibleMovesObjects = MoveGenerator.getPossibleMoves(gameBoard.getGrid());

        allPossibleMoves = new long[allPossibleMovesObjects.size()];
        int i = 0;
        for (Sausage s : allPossibleMovesObjects) {
            allPossibleMoves[i] = BitEncoder.sausageObjectToLongBitboard(s, gameBoard.getGrid());
            i++;
        }
        long bitGameBoard = BitEncoder.sausageGridToLongBitboard(gameBoard.getGrid());// konvertovat grid na long

        // treba to tu, aby sa to kazdym volanim resetovalo
        // na perune by to asi potiahlo aj 32
        tt = new TranspositionTable(25);
        ttCallsCount = 0;

        strategyP1 = new HashMap<>();
        strategyP2 = new HashMap<>();

        nodesInvestigatedMax = 0;
        nodesInvestigatedMin = 0;

        return minimaxMemo(bitGameBoard, true);
    }

    private int minimaxMemo(long gameBoard, boolean isMaximizingPlayer) {

//        // doplnok na behu v threade
//        if (Thread.currentThread().isInterrupted()) {
//            return -2; // specialna hodnota na oznacenie prerusenia
//        }

        // benchmarks
        if (isMaximizingPlayer)
            nodesInvestigatedMax++;
        else
            nodesInvestigatedMin++;

        // transposition table
        if (tt.contains(gameBoard)) {
            ttCallsCount++;
            return tt.getValue(gameBoard);
        }

        int returnVal;

        if (isMaximizingPlayer) {
            int bestValue = -1; // to je ako -infinity
            boolean atLeastOne = false;
            for (int i = 0; i<allPossibleMoves.length; i++) {
                long move = allPossibleMoves[i];

                if (BitEncoder.validateSausageForGrid(gameBoard, move)) {
//                    if (nodesInvestigated > 1_000_000_000) return -2;

                    long childGameBoard = BitEncoder.addSausage(gameBoard, move);

                    atLeastOne = true;
                    int value = minimaxMemo(childGameBoard, false);

                    if (value == -2) return -2;
                    bestValue = Math.max(value, bestValue);

                    if (bestValue == 1) {
                        strategyP1.put(gameBoard, move);
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

            for (int i = 0; i<allPossibleMoves.length; i++) {
                long move = allPossibleMoves[i];

                if (BitEncoder.validateSausageForGrid(gameBoard, move)) {

                    long childGameBoard = BitEncoder.addSausage(gameBoard, move);

                    atLeastOne = true;
                    int value = minimaxMemo(childGameBoard, true);

                    if (value == -2) return -2;
                    bestValue = Math.min(value, bestValue);

                    if (bestValue == -1) {
                        strategyP2.put(gameBoard, move);
//                        break; // jedina dalsia moznost je 1, ale to nam neuskodi - chceme byt pesimisticky (ale pri hladani konkretnej strategie to uz nemozme urobit)
                    }
                }
            }
            // game over check
            if (!atLeastOne) {
                return 1; // nema tah, teda vyhrava prvy
            }
            returnVal = bestValue;
        }

        tt.put(gameBoard, returnVal);
        return returnVal;
    }

//    public static void main(String[] args) {
//        MinimaxBitboard sm = new MinimaxBitboard();
//        GameBoard g = new GameBoard(9, 6);
//        int whoIsWinner = sm.minimaxMemoStart(g);
//        System.out.println("Winner: " + whoIsWinner);
//        System.out.println("number of TT calls: " + sm.getTtCallsCount());
//    }
}
