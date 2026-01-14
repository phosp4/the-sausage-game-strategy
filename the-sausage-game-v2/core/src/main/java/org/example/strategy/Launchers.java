package org.example.strategy;

import org.example.entities.GameBoard;
import org.example.utils.CsvWriterUtil;

import java.util.Arrays;
import java.util.concurrent.*;

public class Launchers {

    public static final String PATH_PREFIX = "minimax_";

    public static void main(String[] args) {
        test3();
    }

    public static void test1() {
        try {
            StrategyMinimax sfm = new StrategyMinimax();

            int[][] results = new int[7][7];
            int res;

            for (int i = 0; i < results.length; i++) {
                for (int j = i; j < results[0].length; j++) {
                    GameBoard g = new GameBoard(i + 1, j + 1);
                    res = sfm.minimaxMemo(g, true);
//                    res = sfm.minimaxAB(g, true, -1, 1);
//                    res = sfm.minimax(g, true);

                    results[j][i] = res;
                    results[i][j] = res;
                }
            }
            CsvWriterUtil.writeIntArrayToCSV(results);
            for (int i = 0; i < results.length; i++) {
                System.out.println(Arrays.toString(results[i]));
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println("Exited with throwable: " + t);
        }
    }

    // generated with chatgpt to add timeout
    public static void test3() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int n = 15;
        int timeout = 200;

        try {
            StrategyMinimax sfm = new StrategyMinimax();
            int[][] results = new int[n][n];
            String[][] resultsColors = new String[n][n];
            String color;

            for (int i = 0; i < results.length; i++) {
                for (int j = i; j < results[0].length; j++) {
                    GameBoard g = new GameBoard(i + 1, j + 1);

                    Future<Integer> future = executor.submit(() -> sfm.minimaxMemo(g, true));
                    int res;

                    try {
                        res = future.get(timeout, TimeUnit.MILLISECONDS); // wait max 500 ms
                    } catch (TimeoutException e) {
                        future.cancel(true); // interrupt if possible
                        System.out.println("Timeout at (" + (i+1) + "," + (j+1) + ")");
                        res = -2; // or whatever default you want
                    }

                    if (res == 1) color = "\uD83D\uDFE9";
                    else if (res == -1) color = "\uD83D\uDFE5";
                    else color = "⬛";

                    results[j][i] = res;
                    results[i][j] = res;
                    resultsColors[i][j] = color;
                    resultsColors[j][i] = color;
                }
            }

            CsvWriterUtil.writeIntArrayToCSV(results);
//            for (int i = 0; i < results.length; i++) {
//                System.out.println(Arrays.toString(results[i]));
//            }
            StringBuilder line;
            for (int i = 0; i < resultsColors.length; i++) {
                line = new StringBuilder();
                for (int j = 0; j < resultsColors[0].length; j++) {
                    line.append(resultsColors[i][j]);
                }
                System.out.println(line);
            }

        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println("Exited with throwable: " + t);
        } finally {
            executor.shutdownNow();
        }
    }

}
