package org.example.strategy_minimax;

import org.example.automation.AutoOpponentMinimaxFromFile;
import org.example.entities.GameBoard;
import org.example.entities.Point;
import org.example.entities.Sausage;
import org.example.entities.Strategy;
import org.example.utils.BitEncoder;
import org.example.utils.CliRendererUtil;
import org.example.utils.FileHandlingUtil;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class BenchmarkMerania {

    public static void main(String[] args) {
//        getEmptyBoardMovegeneratorUpToN(50);
//        getMaxTreeDepthTable(50);
        printStrategyFile(9,  7, true);
//        saveStrategyFileAsTxt(9, 5, true);
//        canonicalFormTester(9, 7);
    }

    public static void canonicalFormTester(int x, int y) {
        SymmetryUtil su = new SymmetryUtil(x,y);

        GameBoard g = new GameBoard(x,y);
        Set<Sausage> moves = MoveGenerator.getPossibleMoves(g.getGrid());
        System.out.println(moves.size());

        Set<Long> canonized = new HashSet<>();
        for (Sausage move : moves) {
            long moveLong = BitEncoder.sausageObjectToLongBitboard(move, g.getGrid());
            canonized.add(su.canonize(moveLong));
        }
        System.out.println(canonized.size());
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
