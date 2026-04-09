package org.example.strategy;

import lombok.Getter;
import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Sausage;
import org.example.utils.BitEncoder;
import org.example.utils.CliRendererUtil;
import org.example.utils.ZobristHasher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MinimaxBitboard {

    // v bitboard reprezentacii musim pouzivat len primitivne longy, objekty zatazia heap a GC
//    private final Map<Long,Integer> memoVals = new HashMap<>();
//    @Getter private final Map<Long,Long> strategyP1 = new HashMap<>();
//    @Getter private final Map<Long,Long> strategyP2 = new HashMap<>();
    private long[] allPossibleMoves;

    // testing
    @Getter private int ttCallsCount = 0;

    public int minimaxMemoStart(GameBoard gameBoard) {
        Set<Sausage> allPossibleMovesObjects = MoveGenerator.getPossibleMoves(gameBoard.getGrid());

        allPossibleMoves = new long[allPossibleMovesObjects.size()];
        int i = 0;
        for (Sausage s : allPossibleMovesObjects) {
            allPossibleMoves[i] = BitEncoder.sausageObjectToLongBitboard(s, gameBoard.getGrid());
            i++;
        }

        long bitGameBoard = BitEncoder.sausageGridToLongBitboard(gameBoard.getGrid());// konvertovat grid na long

        return minimaxMemo(bitGameBoard, true);
    }

    private int minimaxMemo(long gameBoard, boolean isMaximizingPlayer) {

        // doplnok na behu v threade
        if (Thread.currentThread().isInterrupted()) {
            return -2; // specialna hodnota na oznacenie prerusenia
        }

//        if (memoVals.containsKey(gameBoard)) {
//            ttCallsCount++;
//            return memoVals.get(gameBoard);
//        }

        int returnVal;

        if (isMaximizingPlayer) {
            int bestValue = -1; // to je ako -infinity
            boolean atLeastOne = false;
            for (int i = 0; i<allPossibleMoves.length; i++) {
                long move = allPossibleMoves[i];

                if (BitEncoder.validateSausageForGrid(gameBoard, move)) {
                    long childGameBoard = BitEncoder.addSausage(gameBoard, move);

                    atLeastOne = true;
                    int value = minimaxMemo(childGameBoard, false);

                    bestValue = Math.max(value, bestValue);

                    if (bestValue == 1) {
//                        strategyP1.put(gameBoard, move);
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

                    bestValue = Math.min(value, bestValue);

                    if (bestValue == -1) {
//                        strategyP2.put(gameBoard, move);
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

//        memoVals.put(gameBoard, returnVal);
        return returnVal;
    }

    public static void main(String[] args) {
        MinimaxBitboard sm = new MinimaxBitboard();
        GameBoard g = new GameBoard(9, 6);
        int whoIsWinner = sm.minimaxMemoStart(g);
        System.out.println("Winner: " + whoIsWinner);
        System.out.println("number of TT calls: " + sm.getTtCallsCount());
    }
}
