/**
 * teoreticky, tu by sa dal pridat deque sausages, ak by sme sa chceli v hre hybat
 * zatial to ale neriesim, nie je to nevyhnutne
 */

package org.example.engine;

import com.badlogic.gdx.graphics.Color;
import lombok.Getter;
import org.example.automation.StrategyAgent;
import org.example.automation.StrategyAgentMinimax;
import org.example.automation.StrategyAgentRandom;
import org.example.automation.StrategyAgentSemiRandomMinimax;
import org.example.entities.*;
import org.example.exceptions.*;
import org.example.utils.CliRendererUtil;

@Getter
/**
 * Owns the game state. No printing, no LibGDX.
 * pred tym sa to volalo game controller, zvazoval som aj GameSession
*/
public class GameEngine {
    private final GameBoard gameBoard;
    private final TurnManager turnManager;
    private boolean isGameOver = false;
//    private StrategyAgent auto;
//    private Player autoPlayer = null;

    private String lastError = null; // todo toto asi dat inak

    public GameEngine() {

        // hlavne miesto, kde sa to nastavuje
        int width = 9;
        int height = 6;
        // https://rgbcolorpicker.com/0-1
        Player p1 = new Player(
            "P1",
            new Color(0.173F, 0.733F, 0.941F, 1f),
            new StrategyAgentMinimax(width, height, true)
//            null
        );
        Player p2 = new Player(
            "P2",
            new Color(1F, 0.369F, 0.369F, 1f),
//            new StrategyAgentMinimax(width, height, false)
            null
        );

//        auto = new StrategyAgentMinimax(width, height);
//        autoPlayer = p2;

        this.gameBoard = new GameBoard(width, height);
        this.turnManager = new TurnManager(p1, p2);
    }

    /** Called by a UI when a player attempts a move. Returns true if applied. */
    public boolean tryApplyMove(Point p1, Point p2, Point p3) {
        lastError = null;

        Sausage move = new Sausage(getTurnManager().getCurrentPlayer(), p1, p2, p3);

        try {
            gameBoard.addSausage(move);
            turnManager.nextTurn();

//            System.out.println(CliRendererUtil.gridToString(gameBoard.getGrid()));
            System.out.println(CliRendererUtil.gridToStringAsArray(gameBoard.getGrid()));
            System.out.println(gameBoard.hashCode());
            if (gameBoard.isGameOver()) {
                isGameOver = true;
                String winnerName = turnManager.getNotCurrentPlayer().getName();
                System.out.println("Game over! Winner: " + winnerName);
//                    GameOverDialog dialog = new GameOverDialog(game, winnerName);
//                    dialog.showOn(stage);
            }
            System.out.println(gameBoard.getSausages());

            return true;
        } catch (InvalidPointForGridException e) {
            lastError = "Invalid sausage placement.";
            return false;
        } catch (IntersectingSausagesException e) {
            lastError = "Sausage intersects with another sausage.";
            return false;
        }
    }
}
