package org.example.strategy_minimax;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Point;
import org.example.entities.Sausage;
import org.example.utils.BitEncoder;
import org.example.utils.CliRendererUtil;

import java.util.*;

public class MinimaxBitboard {

    private long[] allPossibleMoves;
    private TranspositionTable tt;

    // strategy - treba domysliet, ci tam ukladat move, lebo tu je move cela plocha...
    // najskor urobit funkciu BoardWithOneSausageToSausage
//    @Getter private Long2LongOpenHashMap strategyP1;
//    @Getter private Long2LongOpenHashMap strategyP2;

    // db mode
    private StrategyWriter strategyP1Writer;
    private StrategyWriter strategyP2Writer;
    private int maxDepthSaveToSave;

    // live mode
//    private SetStrategyWriter setStrategyWriterP1;
//    private SetStrategyWriter setStrategyWriterP2;
    @Getter private Set<Long> finalSetOfMoves;

    // benchmarks
    @Getter private long ttCallsCount;
    @Getter private long nodesInvestigatedMax;
    @Getter private long nodesInvestigatedMin;
    @Getter private long ttOverwrites;
    @Getter private long strategyP1LinesCount;
    @Getter private long strategyP2LinesCount;

    @Setter private int knownWinner;
    @Getter private boolean saveStrategy;
    private int nodesPrintCount;

    public int minimaxMemoStart(GameBoard gameBoard) {
        return minimaxMemoStart(gameBoard, 0, false, Integer.MAX_VALUE, MinimaxMode.DATABASE, true); // defaultne ho nepozname
    }

    /**
     * @param gameBoard - ak chceme zacat s lubovolnou plochou
     * @param winner - ak uz vieme vitaza vopred
     * @param saveStrategy - ci ju chceme realne ukladat, alebo iba ziskavat statistiku ulozeni
     * @param maxDepthSaveToSave
     * @param minimaxMode - database je klasicky (vytvara subor), live je pre UI (vracia Set)
     * @param startWithMaximizer - toto je pre pripady, ked nehladame od zaciatku, teda od prazdnej plochy
     * @return
     */
    public int minimaxMemoStart(GameBoard gameBoard, int winner, boolean saveStrategy, int maxDepthSaveToSave, MinimaxMode minimaxMode, boolean startWithMaximizer) {

        // possible moves
        // just for an empty (or initial) grid - all options
        List<Sausage> allPossibleMovesObjects = new ArrayList<>(MoveGenerator.getPossibleMoves(gameBoard.getGrid()));

//        double boardCenterX = (gameBoard.getColumnsX() - 1) / 2.0;
//        double boardCenterY = (gameBoard.getRowsY() - 1) / 2.0;
//
//        // podla testov to nezrychluje nic
//        allPossibleMovesObjects.sort((s1, s2) -> {
//            double dist1 = calculateDistanceToCenter(s1, boardCenterX, boardCenterY);
//            double dist2 = calculateDistanceToCenter(s2, boardCenterX, boardCenterY);
//            return Double.compare(dist2, dist1);
//        });

        allPossibleMoves = new long[allPossibleMovesObjects.size()];
        int i = 0;
        for (Sausage s : allPossibleMovesObjects) {
            allPossibleMoves[i] = BitEncoder.sausageObjectToLongBitboard(s, gameBoard.getGrid());
            System.out.println(CliRendererUtil.bitboardToString(allPossibleMoves[i], gameBoard.getColumnsX(), gameBoard.getRowsY()));
            i++;
        }
        long bitGameBoard = BitEncoder.sausageGridToLongBitboard(gameBoard.getGrid());// konvertovat grid na long

        // treba to tu, aby sa to kazdym volanim resetovalo
        // na perune by to asi potiahlo aj 32
        tt = new TranspositionTable(28);
        ttCallsCount = 0;

        this.maxDepthSaveToSave = maxDepthSaveToSave;
        String depthText = maxDepthSaveToSave == Integer.MAX_VALUE ? "FULL" : String.valueOf(maxDepthSaveToSave);

        if (minimaxMode.equals(MinimaxMode.DATABASE)) {
            if (saveStrategy) {
                if (winner == 1) {
//                strategyP1Writer = new DiskStrategyWriter("strategy_" + gameBoard.getColumnsX() + "x" + gameBoard.getRowsY() + "_p1_dsw_depth" + depthText + ".bin");
                    strategyP1Writer = new DiskStrategyWriter("strategy_" + gameBoard.getColumnsX() + "x" + gameBoard.getRowsY() + "_p1" + ".bin"); // for production
                } else if (winner == -1) {
//                strategyP2Writer = new DiskStrategyWriter("strategy_" + gameBoard.getColumnsX() + "x" + gameBoard.getRowsY() + "_p2_dsw_depth" + depthText + ".bin");
                    strategyP2Writer = new DiskStrategyWriter("strategy_" + gameBoard.getColumnsX() + "x" + gameBoard.getRowsY() + "_p2" + ".bin"); // for production
                }
            }
        } else if (minimaxMode.equals(MinimaxMode.LIVE)) {
            if (saveStrategy) {
                if (winner == 1) {
                    strategyP1Writer = new SetStrategyWriter();
                } else if (winner == -1) {
                    strategyP2Writer = new SetStrategyWriter();
                }
            }
        }

        nodesInvestigatedMax = 0;
        nodesInvestigatedMin = 0;
        ttOverwrites = 0;

        strategyP1LinesCount = 0;
        strategyP2LinesCount = 0;
        nodesPrintCount = 0;

        knownWinner = winner;
        this.saveStrategy = saveStrategy;

        int result = minimaxMemo(bitGameBoard, startWithMaximizer, 0);

        if (strategyP1Writer != null) strategyP1Writer.close();
        if (strategyP2Writer != null) strategyP2Writer.close();

        if (minimaxMode.equals(MinimaxMode.LIVE)) {
            if (knownWinner == 1) {
                finalSetOfMoves = ((SetStrategyWriter) strategyP1Writer).getFullStrategy();
                System.out.println("Strategy for player 1 saved as set!");
            } else if (knownWinner == -1) {
                finalSetOfMoves = ((SetStrategyWriter) strategyP2Writer).getFullStrategy();
                System.out.println("Strategy for player 2 saved as set!");
            }
            if (finalSetOfMoves == null) {
                System.err.println("Set of moves equals null, something is wrong!");
            }
        }

        return result;
    }

    private int minimaxMemo(long gameBoard, boolean isMaximizingPlayer, int depth) {

//        // doplnok na behu v threade
//        if (Thread.currentThread().isInterrupted()) {
//            return -2; // specialna hodnota na oznacenie prerusenia
//        }

        nodesPrintCount++;
        if (nodesPrintCount > 10_000_000) {
            System.out.println("winner: ??");
            System.out.println("tt calls: " + getTtCallsCount());
            System.out.println("max calls: " + getNodesInvestigatedMax());
            System.out.println("min calls: " + getNodesInvestigatedMin());
            System.out.println("calls together: " + (nodesInvestigatedMin + nodesInvestigatedMax));
            System.out.println("tt overwrites: " + getTtOverwrites());
            System.out.println("strategy P1 lines: " + getStrategyP1LinesCount());
            System.out.println("strategy P1 size: " + ((getStrategyP1LinesCount() * 128) / 8) / 1_000_000.0 + " MB");
            System.out.println("strategy P2 lines: " + getStrategyP2LinesCount());
            System.out.println("strategy P2 size: " + ((getStrategyP2LinesCount() * 128) / 8) / 1_000_000.0 + " MB");
            System.out.println("________________________________________________");

            nodesPrintCount = 0;
        }

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
                long moveInBoard = allPossibleMoves[i];

                if (BitEncoder.validateSausageForGrid(gameBoard, moveInBoard)) {
//                    if (nodesInvestigated > 1_000_000_000) return -2;

                    long childGameBoard = BitEncoder.addSausage(gameBoard, moveInBoard);

                    atLeastOne = true;
                    int value = minimaxMemo(childGameBoard, false, depth + 1);

                    if (value == -2) return -2;
                    bestValue = Math.max(value, bestValue);

                    if (bestValue == 1 && knownWinner == 1) {
                        break;
                    } else if (bestValue == 1 && knownWinner == 0) {
                        break;
                    } else if (bestValue == 1 && depth > maxDepthSaveToSave) {
                        break;
                    }
                }
            }
            returnVal = bestValue;

            // game over check
            if (!atLeastOne) {
                returnVal = -1; // nema tah, teda vyhrava druhy
            }
        }

        else {
            int bestValue = 1; // to je ako +infinity
            boolean atLeastOne = false;

            for (int i = 0; i<allPossibleMoves.length; i++) {
                long moveInBoard = allPossibleMoves[i];

                if (BitEncoder.validateSausageForGrid(gameBoard, moveInBoard)) {

                    long childGameBoard = BitEncoder.addSausage(gameBoard, moveInBoard);

                    atLeastOne = true;
                    int value = minimaxMemo(childGameBoard, true, depth + 1);

                    if (value == -2) return -2;
                    bestValue = Math.min(value, bestValue);

                    if (bestValue == -1 && knownWinner == -1) {
                        break;
                    } else if (bestValue == -1 && knownWinner == 0) {
                        break;
                    } else if (bestValue == -1 && depth > maxDepthSaveToSave) {
                        break;
                    }
                }
            }
            returnVal = bestValue;

            // game over check
            if (!atLeastOne) {
                returnVal = 1; // nema tah, teda vyhrava prvy
            }
        }

        // saving to TT
        if (tt.contains(gameBoard)) ttOverwrites++;
        tt.put(gameBoard, returnVal);

        // saving the strategy here
        if (depth <= maxDepthSaveToSave) {
            // teda plocha, do ktorej sa chce dostat P1
            if (returnVal == 1 && knownWinner == 1 && !isMaximizingPlayer) {
                strategyP1LinesCount++;
                if (saveStrategy) strategyP1Writer.put(gameBoard);
            }
            // teda plocha, do ktorej sa chce dostat P2
            if (returnVal == -1 && knownWinner == -1 && isMaximizingPlayer) {
                strategyP2LinesCount++;
                if (saveStrategy) strategyP2Writer.put(gameBoard);
            }
        }

        return returnVal;
    }

    private double calculateDistanceToCenter(Sausage sausage, double boardCenterX, double boardCenterY) {
        double sumX = 0;
        double sumY = 0;
        int count = 0;

        for (Point p : sausage.getThreePoints()) {
            sumX += p.getX();
            sumY += p.getY();
            count++;
        }

        double sausageCenterX = sumX / count;
        double sausageCenterY = sumY / count;

        double dx = sausageCenterX - boardCenterX;
        double dy = sausageCenterY - boardCenterY;

        return (dx * dx) + (dy * dy);
    }
}
