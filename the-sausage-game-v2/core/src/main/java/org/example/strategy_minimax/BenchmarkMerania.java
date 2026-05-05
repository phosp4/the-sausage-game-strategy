package org.example.strategy_minimax;

import org.example.automation.AutoOpponentMinimaxFromFile;
import org.example.entities.GameBoard;
import org.example.entities.Point;
import org.example.entities.Sausage;
import org.example.entities.Strategy;
import org.example.utils.BitEncoder;
import org.example.utils.CliRendererUtil;
import org.example.utils.FileHandlingUtil;

import java.util.Scanner;
import java.util.Set;

public class BenchmarkMerania {

    public static void main(String[] args) {
//        getEmptyBoardMovegeneratorUpToN(50);
//        getMaxTreeDepthTable(50);
//        printStrategyFile(9,  5, true);
//        saveStrategyFileAsTxt(9, 5, true);

//        for (int i = 0; i < allPossibleMoves.length; i++) {
//            long s1 = allPossibleMoves[i];
//            for (int j = i+1; j<allPossibleMoves.length; j++) {
//                long s2 = allPossibleMoves[j];
//                for (int k = j+1; k < allPossibleMoves.length; k++) {
//                    long s3 = allPossibleMoves[k];
//                    if (((s1 & s2) & s3) == 0L) {
//    //                    System.out.println(CliRendererUtil.bitboardToString(s1, x, y));
//    //                    System.out.println(CliRendererUtil.bitboardToString(s2, x, y));
//    //                    System.out.println("-------------------------------------------");
//                        size++;
//                    }
//                }
//            }
//        }
    }

    public static void zistiPocetKombinaciiPre9x7() {
        int x = 9;
        int y = 7;
        GameBoard g = new GameBoard(x,y);
        Set<Sausage> allPossibleMovesObjects = MoveGenerator.getPossibleMoves(g.getGrid());

        int size = 0;

        long[] allPossibleMoves = new long[allPossibleMovesObjects.size()];
        int ii = 0;
        for (Sausage s : allPossibleMovesObjects) {
            allPossibleMoves[ii] = BitEncoder.sausageObjectToLongBitboard(s, g.getGrid());
            ii++;
        }

        for (int i = 0; i < allPossibleMoves.length; i++) {
            long s1 = allPossibleMoves[i];
            for (int j = i+1; j<allPossibleMoves.length; j++) {
                long s2 = allPossibleMoves[j];
                if (((s1 & s2)) == 0L) {
//                    System.out.println(CliRendererUtil.bitboardToString(s1, x, y));
//                    System.out.println(CliRendererUtil.bitboardToString(s2, x, y));
//                    System.out.println("-------------------------------------------");
                    size++;
                }
            }
        }
        System.out.println(size);
    }

    public static void printStrategyFile(int x, int y, boolean isFirst) {
        Set<Long> rawStrategy = FileHandlingUtil.loadStrategyBinaryFromFile(x, y, isFirst);
        Strategy strategy = new Strategy(rawStrategy, isFirst);

//        strategy.writeStrategyToTxt();
        for (Long board : strategy.getWinningBoards()) {
            System.out.println(CliRendererUtil.bitboardToString(board, x, y));
        }
    }

    public static void saveStrategyFileAsTxt(int x, int y, boolean isFirst) {
        Set<Long> rawStrategy = FileHandlingUtil.loadStrategyBinaryFromFile(x, y, isFirst);
        Strategy strategy = new Strategy(rawStrategy, isFirst);
        strategy.writeStrategyToTxt(x, y);
    }

    public static void getMaxTreeDepthTable(int n) {
        long[][] matrice = new long[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int x = i + 1;
                int y = j + 1;

                System.out.println("Board: " + x + "x" + y);

                matrice[j][i] = getMaximumTreeDepthForBoard(x, y) / 3; // celociselne
            }
        }

        FileHandlingUtil.writeArrayToCSV(matrice, "max_tree_depth.csv");
    }

    public static int getMaximumTreeDepthForBoard(int x, int y) {
        return (int) Math.ceil(x*y / 2.0);
    }

    public static void getEmptyBoardMovegeneratorUpToN(int n) {

        long[][] matrice = new long[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int x = i + 1;
                int y = j + 1;

                System.out.println("Board: " + x + "x" + y);

//                // do 64 policok
//                if (x * y > n) {
//                    System.out.println("Skipping...");
//                    continue;
//                }

                GameBoard g = new GameBoard(x, y);
                int size = MoveGenerator.getPossibleMoves(g.getGrid()).size();
                matrice[j][i] = size;
            }
        }

        FileHandlingUtil.writeArrayToCSV(matrice, "empty_board_movegenerator.csv");
    }
}
