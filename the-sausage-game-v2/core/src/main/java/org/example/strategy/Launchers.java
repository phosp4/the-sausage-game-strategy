package org.example.strategy;

import org.example.entities.GameBoard;
import org.example.entities.Point;
import org.example.utils.CsvWriterUtil;

import java.util.*;
import java.util.concurrent.*;

public class Launchers {

    public static final String PATH_PREFIX = "minimax_";

    public static void main(String[] args) {
        test4(40, 2000);
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
        int timeout = 100;

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

    // generated with aistudio to add timeout and skip logic
    public static void test4(int n, int timeout) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            StrategyMinimax sfm = new StrategyMinimax();
            int[][] results = new int[n][n];
            String[][] resultsColors = new String[n][n];
            // Pomocné pole na sledovanie timeoutov
            boolean[][] timedOut = new boolean[n][n];

            for (int i = 0; i < n; i++) {
                for (int j = i; j < n; j++) {
                    int res;

                    // LOGIKA PRESKAKOVANIA:
                    // Ak už pre menší rozmer (buď menej riadkov i-1, alebo menej stĺpcov j-1)
                    // nastal timeout, tento rozmer automaticky označíme ako timeout.
                    boolean skipDueToPreviousTimeout =
                        (i > 0 && timedOut[i-1][j]) ||
                            (j > i && timedOut[i][j-1]);

                    if (skipDueToPreviousTimeout) {
                        timedOut[i][j] = true;
                        res = -3;
                    } else {
                        GameBoard g = new GameBoard(i + 1, j + 1);
                        Future<Integer> future = executor.submit(() -> sfm.minimaxMemo(g, true));

                        try {
                            res = future.get(timeout, TimeUnit.MILLISECONDS);
                        } catch (TimeoutException e) {
                            future.cancel(true);
                            System.out.println("Timeout at (" + (i + 1) + "," + (j + 1) + ")");
                            timedOut[i][j] = true; // Poznačíme si timeout
                            res = -2;
                        } catch (Exception e) {
                            res = -2;
                        }
                    }

                    // Priradenie farby
                    String color;
                    if (res == 1) color = "\uD83D\uDFE9"; // Zelená
                    else if (res == -1) color = "\uD83D\uDFE5"; // Červená
                    else if (res == -3) color = "⬜"; // Biela pre preskočené
                    else color = "⬛"; // Čierna pre timeout/remízu

                    // Symetrické uloženie (keďže doska 3x5 je z hľadiska minimaxu rovnaká ako 5x3)
                    results[i][j] = res;
                    results[j][i] = res;
                    resultsColors[i][j] = color;
                    resultsColors[j][i] = color;

                    // Ak v tomto riadku i nastal timeout pre rozmer j,
                    // všetky nasledujúce j v tomto riadku budú tiež timeouty (voliteľná optimalizácia)
                    if (res == -2) {
                        // Tento break ukončí vnútorný cyklus (j), takže pre toto i už nebude skúšať väčšie j
                        // timedOut[i][j] už je true, takže v ďalších riadkoch i+1 to skipDueToPreviousTimeout zachytí
                        // results a resultsColors pre zvyšok riadku ostanú v predvolenom stave (napr. 0 / null),
                        // preto je dobré ich v cykle "doplniť" alebo inicializovať pole na čiernu.
                    }
                }
            }

            // Výpis výsledkov (nezmenený)
            CsvWriterUtil.writeIntArrayToCSV(results);
            for (int i = 0; i < resultsColors.length; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < resultsColors[0].length; j++) {
                    line.append(resultsColors[i][j] != null ? resultsColors[i][j] : "⬛");
                }
                System.out.println(line);
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }

}
