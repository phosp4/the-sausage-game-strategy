package org.example.strategy;

import org.example.entities.GameBoard;
import org.example.utils.CsvUtil;

import java.util.concurrent.*;

public class Launchers {

    public static final String PATH_PREFIX = "minimax_";
    public static final String GROUND_TRUTH = "minimax_results/truth/truth.csv";

    public static void main(String[] args) {
        test4(20, 2000);
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
                for (int j = 0; j < n; j++) {
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

                    // radsej nesymetricky - kvoli kontrole
                    results[i][j] = res;
//                    results[j][i] = res;
                    resultsColors[i][j] = color;
//                    resultsColors[j][i] = color;

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
            String path = CsvUtil.writeIntArrayToCSV(results);
            for (int i = 0; i < resultsColors.length; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < resultsColors[0].length; j++) {
                    line.append(resultsColors[i][j] != null ? resultsColors[i][j] : "⬛");
                }
                System.out.println(line);
            }

            // compare with ground truth
            CsvUtil.CompareCSVsOnesMinusOnes(path, GROUND_TRUTH);

            // check the symmetry
            CsvUtil.isSymmetricCSV(path);

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }

}
