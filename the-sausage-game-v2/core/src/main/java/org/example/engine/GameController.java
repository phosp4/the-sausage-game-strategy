package org.example.engine;

import lombok.Data;
import org.example.automation.AutonomousOpponent;
import org.example.entities.*;
import org.example.exceptions.*;
import org.example.strategy.MoveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Data
/** Owns the game state. No printing, no LibGDX. */
public class GameController {
    private final GameBoard gameBoard;
    private final TurnManager turnManager;
    private AutonomousOpponent auto;
    private Player autonomousPlayer;

    private String lastError = null; // todo toto asi dat inak

    public GameController(int width, int height, Player p1, Player p2, Player auto) {
        this.gameBoard = new GameBoard(width, height);
        this.turnManager = new TurnManager(p1, p2);
//        this.autonomousPlayer = auto;
//        this.auto = new RandomOpponent();
    }

    /** Called by a UI when a player attempts a move. Returns true if applied. */
    public boolean tryApplyMove(Sausage move) {
        lastError = null;
        move.setPlayer(getCurrentPlayer());
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

    public boolean isOver() { return gameBoard.isGameOver(); }
    public Player getWinner() { return gameBoard.getWinner(); }
    public Player getCurrentPlayer() { return turnManager.getCurrentPlayer(); }
    public String getLastError() { return lastError; }
    public Sausage[][] snapshotGrid() { return gameBoard.getGrid(); } // or expose read-only fields you already have
}
