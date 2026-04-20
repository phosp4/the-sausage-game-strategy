package org.example.strategy_minimax;

import org.example.entities.GameBoard;
import org.example.utils.FileHandlingUtil;

import java.util.Map;
import java.util.concurrent.*;

public class MinimaxLaunchers {

    public static void main(String[] args) {
//        getResultsTable(20, 3000);
//        getResultForBoard(9,7);
//        getStrategyForBoard(7, 4);
        getAndSaveStrategyForBoardCSV(9,6);
//        fixChybyTemp();

//        for (int i = 3; i < 9; i++) {
//            int size = MoveGenerator.getPossibleMoves(new GameBoard(i,i).getGrid()).size();
//            System.out.println("rozmer " + i + " velkost " + size);
//        }

//        MinimaxRunConfig config = new MinimaxRunConfig().builder
//            .width()
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
//            System.out.println(mr.getStrategyP1());
            return mr.getStrategyP1();
        } else if (winner == -1) {
//            System.out.println(mr.getStrategyP2());
            return mr.getStrategyP2();
        }
        System.err.println("Problem loading a strategy...");
        return null;
    }

    // skor na testing
    public static void getAndSaveStrategyForBoardCSV(int x, int y) {

        Minimax mr = new Minimax();
        GameBoard g = new GameBoard(x, y);
        int winner = mr.minimaxMemoStart(g);
        System.out.println(winner);

        Map<Long, Long> strategyP1 = mr.getStrategyP1();
        Map<Long, Long> strategyP2 = mr.getStrategyP2();

        // files naming
        String fileNameP1 = FileHandlingUtil.STRATEGY_PATH + "/strategy_" + x + "x" + y + "_p1.csv";
        String fileNameP2 = FileHandlingUtil.STRATEGY_PATH + "/strategy_" + x + "x" + y + "_p2.csv";

        FileHandlingUtil.saveStrategyCSV(strategyP1, fileNameP1);
        FileHandlingUtil.saveStrategyCSV(strategyP2, fileNameP2);
    }

    public static void getResultForBoard(int x, int y) {
        MinimaxBitboard sm = new MinimaxBitboard();
        GameBoard g = new GameBoard(x, y);
        int whoIsWinner = sm.minimaxMemoStart(g);
        System.out.println("Winner: " + whoIsWinner);
        System.out.println("number of TT calls: " + sm.getTtCallsCount());
        System.out.println("number of nodes investigated: " + sm.getNodesInvestigated());
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

public static void getResultsTable(int n, int timeout) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    try {
        MinimaxBitboard sfm = new MinimaxBitboard();
        int[][] results = new int[n][n];
        String[][] resultsColors = new String[n][n];
        boolean[][] timedOut = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (i + j > 64) {
                    System.out.println((i + 1) + "," + (j + 1) + " has more than 64 points, skipping...");
                    continue;
                }

                // OPRAVA 2: Správna logika - ak menší rozmer zlyhal, väčší zlyhá tiež.
                boolean skipDueToPreviousTimeout =
                    (i > 0 && timedOut[i-1][j]) ||
                        (j > 0 && timedOut[i][j-1]); // Odstránené "j > i"

                int res;

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
                        timedOut[i][j] = true;
                        res = -2;
                    } catch (Exception e) {
                        future.cancel(true); // Pre istotu prerušíme bežiace vlákno
                        System.out.println("Exception at (" + (i + 1) + "," + (j + 1) + "): " + e.getMessage());
                        timedOut[i][j] = true; // OPRAVA 3: Musíme označiť, že to zlyhalo!
                        res = -2;
                    }
                }

                // Priradenie farby
                String color;
                if (res == 1) color = "\uD83D\uDFE9"; // Zelená
                else if (res == -1) color = "\uD83D\uDFE5"; // Červená
                else if (res == -3) color = "⬜"; // Biela pre preskočené
                else color = "⬛"; // Čierna pre timeout / chybu

                results[i][j] = res;
                resultsColors[i][j] = color;

                // OPRAVA 1 a 4: Správne implementovaný break
                if (res == -2) {
                    // Ak nastal timeout, ručne vyplníme zvyšok riadku bielymi štvorcami,
                    // aby ďalšie riadky pod týmto vedeli, že tieto rozmery majú tiež preskočiť.
                    for (int k = j + 1; k < n; k++) {
                        timedOut[i][k] = true;
                        results[i][k] = -3;
                        resultsColors[i][k] = "⬜";
                    }
                    break; // Teraz môžeme bezpečne ukončiť cyklus pre 'j'
                }
            }
        }

        // Výpis výsledkov
        String fileName = FileHandlingUtil.PATH_PREFIX + results.length + "x" + results[0].length + "_" + timeout + ".csv";
        String path = FileHandlingUtil.writeIntArrayToCSV(results, fileName);
        for (int i = 0; i < resultsColors.length; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < resultsColors[0].length; j++) {
                line.append(resultsColors[i][j] != null ? resultsColors[i][j] : "⬛");
            }
            System.out.println(line);
        }

        FileHandlingUtil.CompareCSVsOnesMinusOnes(path, FileHandlingUtil.GROUND_TRUTH);
        FileHandlingUtil.isSymmetricCSV(path);

    } catch (Throwable t) {
        t.printStackTrace();
    } finally {
        executor.shutdownNow();
    }
}

    // generated with aistudio to add timeout and skip logic
//    public static void getResultsTable(int n, int timeout) {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//
//        try {
//            MinimaxBitboard sfm = new MinimaxBitboard();
//            int[][] results = new int[n][n];
//            String[][] resultsColors = new String[n][n];
//            // Pomocné pole na sledovanie timeoutov
//            boolean[][] timedOut = new boolean[n][n];
//
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < n; j++) {
//                    int res;
//
//                    // LOGIKA PRESKAKOVANIA:
//                    // Ak už pre menší rozmer (buď menej riadkov i-1, alebo menej stĺpcov j-1)
//                    // nastal timeout, tento rozmer automaticky označíme ako timeout.
//                    boolean skipDueToPreviousTimeout =
//                        (i > 0 && timedOut[i-1][j]) ||
//                            (j > i && timedOut[i][j-1]);
//
//                    if (skipDueToPreviousTimeout) {
//                        timedOut[i][j] = true;
//                        res = -3;
//                    } else {
//                        GameBoard g = new GameBoard(i + 1, j + 1);
//                        Future<Integer> future = executor.submit(() -> sfm.minimaxMemoStart(g));
//
//                        try {
//                            res = future.get(timeout, TimeUnit.MILLISECONDS);
//                        } catch (TimeoutException e) {
//                            future.cancel(true);
//                            System.out.println("Timeout at (" + (i + 1) + "," + (j + 1) + ")");
//                            timedOut[i][j] = true; // Poznačíme si timeout
//                            res = -2;
//                        } catch (Exception e) {
//                            res = -2;
//                        }
//                    }
//
//                    // Priradenie farby
//                    String color;
//                    if (res == 1) color = "\uD83D\uDFE9"; // Zelená
//                    else if (res == -1) color = "\uD83D\uDFE5"; // Červená
//                    else if (res == -3) color = "⬜"; // Biela pre preskočené
//                    else color = "⬛"; // Čierna pre timeout
//
//                    // radsej nesymetricky - kvoli kontrole
//                    results[i][j] = res;
////                    results[j][i] = res;
//                    resultsColors[i][j] = color;
////                    resultsColors[j][i] = color;
//
//                    // Ak v tomto riadku i nastal timeout pre rozmer j,
//                    // všetky nasledujúce j v tomto riadku budú tiež timeouty (voliteľná optimalizácia)
//                    if (res == -2) {
//                        // Tento break ukončí vnútorný cyklus (j), takže pre toto i už nebude skúšať väčšie j
//                        // timedOut[i][j] už je true, takže v ďalších riadkoch i+1 to skipDueToPreviousTimeout zachytí
//                        // results a resultsColors pre zvyšok riadku ostanú v predvolenom stave (napr. 0 / null),
//                        // preto je dobré ich v cykle "doplniť" alebo inicializovať pole na čiernu.
//                    }
//                }
//            }
//
//            // Výpis výsledkov (nezmenený)
//            String path = FileHandlingUtil.writeIntArrayToCSV(results);
//            for (int i = 0; i < resultsColors.length; i++) {
//                StringBuilder line = new StringBuilder();
//                for (int j = 0; j < resultsColors[0].length; j++) {
//                    line.append(resultsColors[i][j] != null ? resultsColors[i][j] : "⬛");
//                }
//                System.out.println(line);
//            }
//
//            // compare with ground truth
//            FileHandlingUtil.CompareCSVsOnesMinusOnes(path, FileHandlingUtil.GROUND_TRUTH);
//
//            // check the symmetry
//            FileHandlingUtil.isSymmetricCSV(path);
//
//        } catch (Throwable t) {
//            t.printStackTrace();
//        } finally {
//            executor.shutdownNow();
//        }
//    }

}
