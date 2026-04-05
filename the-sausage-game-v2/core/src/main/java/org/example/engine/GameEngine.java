/**
 * teoreticky, tu by sa dal pridat deque sausages, ak by sme sa chceli v hre hybat
 * zatial to ale neriesim, nie je to nevyhnutne
 */

package org.example.engine;

import lombok.Getter;
import org.example.entities.*;
import org.example.exceptions.*;
import org.example.strategy.MoveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
/**
 * Owns the game state. No printing, no LibGDX.
 * pred tym sa to volalo game controller, zvazoval som aj GameSession
*/
public class GameEngine {
    private final GameBoard gameBoard;
    private final TurnManager turnManager;

//    private AutonomousOpponent auto;
//    private Player autonomousPlayer;

    private String lastError = null; // todo toto asi dat inak

    public GameEngine() {

        // hlavne miesto, kde sa to nastavuje
        int columns = 9;
        int rows = 7;
        Player p1 = new Player("P1");
        Player p2 = new Player("P2");

        this.gameBoard = new GameBoard(columns, rows);
        this.turnManager = new TurnManager(p1, p2);
//        this.autonomousPlayer = auto;
//        this.auto = new RandomOpponent();
    }

    /** Called by a UI when a player attempts a move. Returns true if applied. */
    public boolean tryApplyMove(Point p1, Point p2, Point p3) {
        lastError = null;

        Sausage move = new Sausage(getTurnManager().getCurrentPlayer(), p1, p2, p3);

        try {
            gameBoard.addSausage(move);
            turnManager.nextTurn();
            return true;
        } catch (InvalidPointForGridException e) {
            lastError = "Invalid sausage placement.";
            return false;
        } catch (IntersectingSausagesException e) {
            lastError = "Sausage intersects with another sausage.";
            return false;
        }
    }

    /** Optional helper for AI/auto player. */
    public Sausage pickRandomLegalMove() {
        List<Sausage> possible = new ArrayList<>(MoveGenerator.getAllPossibleMoves(gameBoard.getGrid()));
        if (possible.isEmpty()) return null;
        return possible.get(ThreadLocalRandom.current().nextInt(possible.size()));
    }
}
