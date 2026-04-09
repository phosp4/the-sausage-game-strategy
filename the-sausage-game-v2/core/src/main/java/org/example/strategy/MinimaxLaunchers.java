package org.example.strategy;

import org.example.entities.GameBoard;
import org.example.utils.CliInputHandler;
import org.example.utils.FileHandlingUtil;

import java.util.Map;
import java.util.concurrent.*;

public class MinimaxLaunchers {

    public static void main(String[] args) {
        getResultsTable(20, 3000);
//        getResultForBoard(9,6);
//        getStrategyForBoard(9, 6);
//        getAndSaveStrategyForBoardCSV(9,6);
//        fixChybyTemp();
    }

//    /**
//     * mozno vyskusat inu - jednoduchsiu implementaciu minimaxu?
//     */
//    public static void fixChybyTemp() {
//        Minimax mr = new Minimax();
//        GameBoard g = new GameBoard(6,9);
//        CliInputHandler cih = new CliInputHandler();
//
//        g.addSausage(CliInputHandler.spracujRiadokVstupu("1,1 2,2 1,3"));
//        g.addSausage(CliInputHandler.spracujRiadokVstupu("1,7 2,6 2,4"));
//        g.addSausage(CliInputHandler.spracujRiadokVstupu("5,3 5,5 5,7"));
//        g.addSausage(CliInputHandler.spracujRiadokVstupu("4,0 4,2 4,4"));
//
//        // tu nastava zmena
//        g.addSausage(CliInputHandler.spracujRiadokVstupu("3,5 3,7 4,6"));
////        g.addSausage(CliInputHandler.spracujRiadokVstupu("0,6 0,8 2,8"));
//
//        System.out.println("Winner: " + mr.minimaxMemoStart(g));
//    }

    public static Map<Long, Long> getStrategyForBoard(int x, int y) {
        Minimax mr = new Minimax();
        GameBoard g = new GameBoard(x, y);
        int winner = mr.minimaxMemoStart(g);
        System.out.println(winner);
        if (winner == 1) {
            System.out.println(mr.getStrategyP1());
            return mr.getStrategyP1();
        } else if (winner == -1) {
            System.out.println(mr.getStrategyP2());
            return mr.getStrategyP2();
        }
        System.err.println("Problem loading a strategy...");
        return null;
    }

    // skor na testing
    public static void getAndSaveStrategyForBoardCSV(int x, int y) {
        int smaller = x;
        int bigger = y;
        if (x > y) {
            smaller = y;
            bigger = x;
        }

        String fileName = FileHandlingUtil.STRATEGY_PATH + "/strategy_" + smaller + "x" + bigger + ".csv";
        Map<Long, Long> strategy = getStrategyForBoard(x, y);
        if (strategy != null) {
            FileHandlingUtil.saveStrategyCSV(strategy, fileName);
        } else {
            System.err.println("Error saving the file...");
        }
    }

    public static void getResultForBoard(int x, int y) {
        Minimax sm = new Minimax();
        GameBoard g = new GameBoard(x, y);
        int whoIsWinner = sm.minimaxMemoStart(g);
        System.out.println("Winner: " + whoIsWinner);
        System.out.println("number of TT calls: " + sm.getTtCallsCount());
    }

//    public static void test5(int x, int y) {
//        Map<Integer, Sausage> firstPlayerStrategy = MinimaxRunner.getFirstPlayerStrategy(x, y);
//        System.out.println(firstPlayerStrategy);
//
////        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("strategy.ser"))) {
////            oos.writeObject(firstPlayerStrategy);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }

    // generated with aistudio to add timeout and skip logic
    public static void getResultsTable(int n, int timeout) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            MinimaxBitboard sfm = new MinimaxBitboard();
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
                        Future<Integer> future = executor.submit(() -> sfm.minimaxMemoStart(g));

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
            String path = FileHandlingUtil.writeIntArrayToCSV(results);
            for (int i = 0; i < resultsColors.length; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < resultsColors[0].length; j++) {
                    line.append(resultsColors[i][j] != null ? resultsColors[i][j] : "⬛");
                }
                System.out.println(line);
            }

            // compare with ground truth
            FileHandlingUtil.CompareCSVsOnesMinusOnes(path, FileHandlingUtil.GROUND_TRUTH);

            // check the symmetry
            FileHandlingUtil.isSymmetricCSV(path);

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }

}
