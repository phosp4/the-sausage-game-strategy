package org.example.strategy_minimax;

import org.example.entities.GameBoard;
import org.example.utils.FileHandlingUtil;

public class BenchmarkMerania {

    public static void main(String[] args) {
//        getEmptyBoardMovegeneratorUpToN(50);
        getMaxTreeDepthTable(50);
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
