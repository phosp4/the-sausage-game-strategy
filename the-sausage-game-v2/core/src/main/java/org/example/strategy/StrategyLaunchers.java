package org.example.strategy;

import org.example.entities.GameBoard;
import org.example.utils.CsvWriterUtil;

public class StrategyLaunchers {

    public static final String PATH = "output.csv";

    public static void main(String[] args) {
        test1();
    }

    public static void test1() {
        try {
            StrategyFinderMinimax sfm = new StrategyFinderMinimax();

            int[][] results = new int[5][5];
            int res;

            for (int i = 0; i < results.length; i++) {
                for (int j = i; j < results[0].length; j++) {
                    GameBoard g = new GameBoard(i + 1, j + 1);
                    res = sfm.minimax(g, true);
//                    res = sfm.minimaxAB(g, true, -1, 1);
//                    res = sfm.minimax(g, true);

                    results[j][i] = res;
                    results[i][j] = res;
                }
            }
            CsvWriterUtil.writeIntArrayToCSV(results);
//            for (int i = 0; i < results.length; i++) {
//                System.out.println(Arrays.toString(results[i]));
//            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println("Exited with throwable: " + t);
        }
    }

    public static void test2() {
        StrategyFinderMinimax sfm = new StrategyFinderMinimax();
        //         6,1 - musi vyhrat prvy; 11,1 - moze vyhrat prvy, ak nechce nemusi; 15,1 - nemoze vyhrat prvy
        GameBoard g = new GameBoard(7,9);
        System.out.println(sfm.minimax(g, true)); // ak tu je false, je to "samomat"
    }
}
