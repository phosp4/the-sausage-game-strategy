/**
 * teoreticky, tu by sa dal pridat deque sausages, ak by sme sa chceli v hre hybat
 * zatial to ale neriesim, nie je to nevyhnutne
 */

package org.example.engine;

import com.badlogic.gdx.graphics.Color;
import lombok.Getter;
import org.example.automation.*;
import org.example.entities.*;
import org.example.exceptions.*;
import org.example.utils.BitEncoder;
import org.example.utils.CliRendererUtil;

@Getter
/**
 * Owns the game state. No printing, no LibGDX.
 * pred tym sa to volalo game controller, zvazoval som aj GameSession
*/
public class GameSession {
    private final GameBoard gameBoard;
    private final TurnManager turnManager;
    private final AiManager aiManager;
    private boolean isGameOver = false;

    public GameSession(int width, int height) {
        // potom dat do konstruktora aj tu AI volbu

        // temp natvrdo
        width = 13;
        height = 5;

        // hlavne miesto, kde sa to nastavuje (zatial)
        // https://rgbcolorpicker.com/0-1
        Player p1 = new Player("P1", new Color(0.173F, 0.733F, 0.941F, 1f));
        Player p2 = new Player("P2", new Color(1F, 0.369F, 0.369F, 1f));

        this.gameBoard = new GameBoard(width, height);
        this.turnManager = new TurnManager(p1, p2);
        this.aiManager = new AiManager();

        // feature - ai player bude mat inu farbu:DD
//        aiManager.registerAiPlayer(p1, new AutoOpponentMinimax(width, height, true));
        //aiManager.registerAiPlayer(p2, new StrategyAgentMinimax(width, height, false));
    }

    /** Called by a UI when a player attempts a move. Returns true if applied. */
    public boolean tryApplyMove(Point p1, Point p2, Point p3) {

        Sausage move = new Sausage(getTurnManager().getCurrentPlayer(), p1, p2, p3);
        return tryApplyMove(move);
    }

    public boolean tryApplyMove(Sausage move) {
        try {
            move.setPlayer(turnManager.getCurrentPlayer());
            gameBoard.addSausage(move);
            turnManager.nextTurn();

//            System.out.println(CliRendererUtil.gridToString(gameBoard.getGrid()));
            System.out.println(CliRendererUtil.gridToStringAsArray(gameBoard.getGrid()));
//            System.out.println(BitEncoder.sausageGridToLongBitboard(gameBoard.getGrid()));

            if (gameBoard.isBoardFull()) {
                isGameOver = true;
                String winnerName = turnManager.getNotCurrentPlayer().getName();
                System.out.println("Game over! Winner: " + winnerName);
//                    GameOverDialog dialog = new GameOverDialog(game, winnerName);
//                    dialog.showOn(stage);
            }
            System.out.println(gameBoard.getSausages());

            return true;
        } catch (InvalidPointForGridException e) {
            System.err.println("Invalid sausage placement.");
            return false;
        } catch (IntersectingSausagesException e) {
            System.err.println("Sausage intersects with another sausage.");
            return false;
        }
    }
}
