/**
 * dalo by sa to krajsie skonstruovat, vsetky tie podmienky a tak
 * ale malo by to fungovat, nateraz nechavam tak
 * tiez by sa mohlo premenovat na AutoOpponentMinimax
 */

package org.example.automation;

import lombok.Getter;
import org.example.engine.GameSession;
import org.example.entities.GameBoard;
import org.example.entities.Sausage;
import org.example.entities.Strategy;
import org.example.exceptions.StrategyMoveNotFoundException;
import org.example.strategy_minimax.CanonizeMode;
import org.example.strategy_minimax.MinimaxBitboard;
import org.example.strategy_minimax.MinimaxMode;
import org.example.utils.CliInputHandler;
import org.example.utils.CliRendererUtil;
import org.example.utils.ValidatorUtil;

import java.io.FileNotFoundException;
import java.util.Set;

/**
 * Autonomous opponent taking strategy from a minimax algorithm,
 * either from a file or live (compared to AutoOpponentMinimaxJustFromFile).
 * Could be also called just AutoOpponentMinimax.
 */
public class AutoOpponentMinimaxFromFileOrLive implements AutoOpponent {

    @Getter private Strategy movesFromFile = null;
    @Getter private Strategy movesFromLive = null;
    private final boolean isForFirstPlayer; // dolezite - ratame s tym, ze tah budeme stale hladat len pre plochy kedy je na tahu tento

    public AutoOpponentMinimaxFromFileOrLive(int x, int y, boolean isFirst) {
        this.isForFirstPlayer = isFirst;
        try {
            this.movesFromFile = StrategyFilesRepository.getStrategy(x, y, isForFirstPlayer);
        } catch (FileNotFoundException e) {
            System.err.println("The file " + e.getMessage() + " was not found.");
        }
    }

    /**
     * pozor - rátame s tým, že toto sa volá, iba keď je na ťahu hráč, ktorému prislúcha táto trieda,
     * teda v zavislosti premennej isForFirstPlayer prvý alebo druhý.
     * Inak povedané, ak isForFirstPlayer = true, plocha musí mať párny počet klobások,
     * a ak isForFirstPlayer = false, tak nepárny počet klobások
     */
    @Override
    public Sausage getNextMove(GameBoard g) throws StrategyMoveNotFoundException {
        if (movesFromLive == null) {
            try {
                Sausage move = movesFromFile.getBestMoveFor(g);
                System.out.println("INFO: Move " + move + " loaded from file.");
                return move;
            } catch (StrategyMoveNotFoundException e) {
                System.out.println("problem with getting next move, initializing live mode");
                initiateLiveMode(g); // lazy pristup
            }
        }

        // movesFromLive by uz nemal byt null
        Sausage move = movesFromLive.getBestMoveFor(g);
        System.out.println("INFO: Move " + move + " loaded from live calculation.");
        return move;
    }

    private void initiateLiveMode(GameBoard gameBoard) {
        if (movesFromLive == null) {
            Set<Long> moves = getLiveStrategy(gameBoard, isForFirstPlayer ? 1 : -1);
            movesFromLive = new Strategy(gameBoard.getColumnsX(), gameBoard.getRowsY(), moves, isForFirstPlayer, ValidatorUtil.shouldCanonize(gameBoard)); // todo mozno nejako tento fakt nacitat zo suboru
            System.out.println("Live strategy loaded! Number of entries: " + moves.size());
        } else {
            System.out.println("Live strategy was loaded already!");
        }
    }

    private Set<Long> getLiveStrategy(GameBoard g, int knownWinnerNoPrune) {
        MinimaxBitboard mr = new MinimaxBitboard();

        // pred tym som to robil takto
//        boolean isMaxPlayer = ctrl.getTurnManager().getCurrentPlayer().equals(ctrl.getTurnManager().getFirstPlayer());
        // ale staci to predsa takto
        boolean isMaxTurn = g.getSausages().size() % 2 == 0;

        long start = System.nanoTime();
        System.out.println("INFO: Starting live calculation from the current board...");
        System.out.println("where winner is: " + knownWinnerNoPrune + " and isMaxPlayer is " + isForFirstPlayer);
        int winner = mr.minimaxMemoStart(g, knownWinnerNoPrune, true, Integer.MAX_VALUE, MinimaxMode.LIVE, isMaxTurn, 23, ValidatorUtil.shouldCanonize(g) ? CanonizeMode.TT_CANONIZE : CanonizeMode.NO_CANONIZE);
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
