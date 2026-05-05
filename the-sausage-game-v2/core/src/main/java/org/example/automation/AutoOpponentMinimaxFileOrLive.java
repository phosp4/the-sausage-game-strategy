/**
 * dalo by sa to krajsie skonstruovat, vsetky tie podmienky a tak
 * ale malo by to fungovat, nateraz nechavam tak
 */

package org.example.automation;

import lombok.Getter;
import org.example.engine.GameSession;
import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.entities.Strategy;
import org.example.strategy_minimax.MinimaxBitboard;
import org.example.strategy_minimax.MinimaxMode;

import java.io.FileNotFoundException;
import java.util.Set;

public class AutoOpponentMinimaxFileOrLive implements AutoOpponent {

    @Getter private Strategy movesFromFile = null;
    @Getter private Strategy movesFromLive = null;
    private GameSession ctrl;

    public AutoOpponentMinimaxFileOrLive(int x, int y, boolean isFirst, GameSession gameSession) {
        this.ctrl = gameSession;
        try {
            this.movesFromFile = StrategyFilesRepository.getStrategy(x, y, isFirst);
            if (movesFromFile == null) {
                initiateLiveMode(new GameBoard(x, y), isFirst);
            }
        } catch (FileNotFoundException e) {
            System.err.println("The file " + e.getMessage() + " was not found.");
            initiateLiveMode(new GameBoard(x, y), isFirst);
        }
    }

    @Override
    public Sausage getNextMove(GameBoard g) {
        if (movesFromFile != null) {
            Sausage s = movesFromFile.getBestMoveFor(g);
            if (s != null) {
                System.out.println("INFO: Move " + s + "loaded from file.");
                return s;
            }
        }
        if (movesFromLive == null) {
            initiateLiveMode(g, movesFromFile.isForFirstPlayer());
        }

        Sausage s = movesFromLive.getBestMoveFor(g);
        if (s != null) {
            System.out.println("INFO: Move " + s + " loaded from live calculation.");
        } else {
            System.err.println("The move is null, problem..");
        }
        return s;
    }

    private void initiateLiveMode(GameBoard gameBoard, boolean isFirstWinner) {
        if (movesFromLive == null) {
            Set<Long> moves = getLiveStrategy(gameBoard, isFirstWinner ? 1 : -1);
            movesFromLive = new Strategy(gameBoard.getColumnsX(), gameBoard.getRowsY(), moves, isFirstWinner, true); // todo mozno nejako tento fakt nacitat zo suboru
            System.out.println("Live strategy loaded!");
        } else {
            System.out.println("Live strategy was loaded already!");
        }
    }

    private Set<Long> getLiveStrategy(GameBoard g, int knownWinnerNoPrune) {
        MinimaxBitboard mr = new MinimaxBitboard();

        // is max player?
        boolean isMaxPlayer = ctrl.getTurnManager().getCurrentPlayer().equals(ctrl.getTurnManager().getFirstPlayer());

        long start = System.nanoTime();
        System.out.println("INFO: Starting live calculation...");
        System.out.println("where winner is: " + knownWinnerNoPrune + " and isMaxPlayer is " + isMaxPlayer);
        int winner = mr.minimaxMemoStart(g, knownWinnerNoPrune, true, Integer.MAX_VALUE, MinimaxMode.LIVE, isMaxPlayer, 23);
        long end = System.nanoTime();
        long duration = end - start;
        long calls = (mr.getNodesInvestigatedMin() + mr.getNodesInvestigatedMax());

        System.out.println("INFO:");
        System.out.println("predicted winner: " + winner);
        System.out.println("duration (nanoseconds): " + duration);
        System.out.println("tt calls: " + mr.getTtCallsCount());
        System.out.println("calls together: " + calls);
        System.out.println("tt overwrites: " + mr.getTtOverwrites());

        return mr.getFinalSetOfMoves();
    }
}
