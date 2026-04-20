package org.example.automation;

import org.example.engine.GameEngine;
import org.example.entities.Player;
import org.example.entities.Point;
import org.example.entities.Sausage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiManager {

    private final GameEngine ctrl;
    private final Map<Player, StrategyAgent> aiPlayers = new HashMap<>();

    public AiManager(GameEngine ctrl) {
        this.ctrl = ctrl;
    }

    public void registerAiPlayer(Player player, StrategyAgent agent) {
        aiPlayers.put(player, agent);
    }

    public boolean isCurrentPlayerAi() {
        return aiPlayers.containsKey(ctrl.getTurnManager().getCurrentPlayer());
    }

    /**
     * executes move for a current player to move
     */
    public void executeAiMove() {
        Player currentPlayer = ctrl.getTurnManager().getCurrentPlayer();
        StrategyAgent agent = aiPlayers.get(currentPlayer);

        if (agent != null) {
            Sausage move = agent.getNextMove(ctrl.getGameBoard());
            List<Point> pts = move.getThreePoints();
            ctrl.tryApplyMove(pts.get(0), pts.get(1), pts.get(2));
        } else {
            throw new RuntimeException("Current player does not have an agent associated.");
        }
    }
}
