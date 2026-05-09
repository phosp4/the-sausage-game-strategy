package org.example.strategy_minimax;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.entities.Strategy;
import org.example.utils.BitEncoder;
import org.example.utils.CliRendererUtil;
import org.example.utils.FileHandlingUtil;
import org.example.utils.SymmetryUtil;

import java.util.HashSet;
import java.util.Set;

public class BenchmarkMerania {

    public static void main(String[] args) {
//        getEmptyBoardMovegeneratorUpToN(50);
//        getMaxTreeDepthTable(50);
//        saveStrategyFileAsTxt(9, 7, false);
        Set<Long> strategyBoards = getAndPrintStrategyFile(9,  6, true);
        System.out.println(strategyBoards);
//        Set<Long> allCanonnicalBoards = canonicalFormTester(9, 7);
//        allCanonnicalBoards.removeAll(strategyBoards);

        // nech bitovo pozera prieniky alebo tak
//        openingBookContainsReactionToEveryFirstMove();
//        poOpeningBookMaStaleStrategiuDruhy();
    }

    public static void poOpeningBookMaStaleStrategiuDruhy() {
        Set<Long> rawStrategy = FileHandlingUtil.loadStrategyBinaryFromFile(9, 7, false);
        GameBoard g = new GameBoard(9, 7);
        MinimaxBitboard mb;

        Set<Sausage> possibleMoves0 = MoveGenerator.getPossibleMoves(g.getGrid());

        int runCounter = 1;
        int totalRunCount = 724; // toto je predpocitane

        for (Sausage move0 : possibleMoves0) {
            g.addSausage(move0);

            Set<Sausage> possibleMoves1 = MoveGenerator.getPossibleMoves(g.getGrid());

            for (Sausage move1 : possibleMoves1) {
                g.addSausage(move1);

                long board = BitEncoder.sausageGridToLongBitboard(g.getGrid());
                long canonized = SymmetryUtil.canonize(board, 9, 7);
                if (rawStrategy.contains(canonized)) {
                    System.out.print("run " + runCounter + "/" + totalRunCount);

                    // tu treba spustit minimax pre zvysok plochy, ci vrati -1
                    mb = new MinimaxBitboard();
                    System.out.print(", board is: " + move0 + ", " + move1);
                    int winner = mb.minimaxMemoStart(g, 0, false, Integer.MAX_VALUE, MinimaxMode.LIVE, true, 28, CanonizeMode.NO_CANONIZE);
                    System.out.print(", result is " + winner);
                    if (winner == -1) {
                        System.out.println(", result is correct");
                    } else if (winner == 1) {
                        System.err.println(", PROBLEM, result is incorrect");
                    }
                    runCounter++;
                }
                g.removeSausage(move1);
            }
            g.removeSausage(move0);
        }
    }

    public static void openingBookContainsReactionToEveryFirstMove() {
        Set<Long> rawStrategy = FileHandlingUtil.loadStrategyBinaryFromFile(9, 7, false);
        GameBoard g = new GameBoard(9,7);
        Set<Sausage> possibleMoves0 = MoveGenerator.getPossibleMoves(g.getGrid());

        int didNotFindCount = 0;
        Set<Sausage> strategyMoves;
        int totalFoundCount = 0;
        for (Sausage move0 : possibleMoves0) {
            g.addSausage(move0);

            Set<Sausage> possibleMoves1 = MoveGenerator.getPossibleMoves(g.getGrid());
            strategyMoves = new HashSet<>();

            int foundCount = 0;
            for (Sausage move1 : possibleMoves1) {
                g.addSausage(move1);

                long board = BitEncoder.sausageGridToLongBitboard(g.getGrid());
                long canonized = SymmetryUtil.canonize(board, 9, 7);
                if (rawStrategy.contains(canonized)) {
                    foundCount++;
                    totalFoundCount++;
                    strategyMoves.add(move1);
                }
                g.removeSausage(move1);
            }
            if (foundCount == 0) {
                didNotFindCount++;
            }
            // pre zaujimavost
            if (foundCount > 1) {
                System.out.println(foundCount);
//                System.out.println(CliRendererUtil.bitboardToString(BitEncoder.sausageObjectToLongBitboard(move0, g.getGrid()), 9, 7));
                System.out.println(move0);
                System.out.println(strategyMoves);
                System.out.println("---------------------");
            }

            g.removeSausage(move0);
        }
        // zero is ideal
        System.out.println("Did not find reaction for " + didNotFindCount + " boards");
        System.out.println("total found count: " + totalFoundCount);
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

    public static Set<Long> canonicalFormTester(int x, int y) {

        GameBoard g = new GameBoard(x,y);
        Set<Sausage> moves = MoveGenerator.getPossibleMoves(g.getGrid());
        System.out.println(moves.size());

        Set<Long> canonized = new HashSet<>();
        for (Sausage move : moves) {
            long moveLong = BitEncoder.sausageObjectToLongBitboard(move, x, y);
            canonized.add(SymmetryUtil.canonize(moveLong, x, y));
        }
        System.out.println(canonized.size());
        return canonized;
    }

    public static Set<Long> getAndPrintStrategyFile(int x, int y, boolean isFirst) {
        Set<Long> rawStrategy = FileHandlingUtil.loadStrategyBinaryFromFile(x, y, isFirst);
        Strategy strategy = new Strategy(x, y, rawStrategy, isFirst);

//        strategy.writeStrategyToTxt();
        for (Long board : strategy.getLoosingBoards()) {
            System.out.println(CliRendererUtil.bitboardToString(board, x, y));
        }
        return rawStrategy;
    }

    public static void saveStrategyFileAsTxt(int x, int y, boolean isFirst) {
        Set<Long> rawStrategy = FileHandlingUtil.loadStrategyBinaryFromFile(x, y, isFirst);
        Strategy strategy = new Strategy(x, y, rawStrategy, isFirst);
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
