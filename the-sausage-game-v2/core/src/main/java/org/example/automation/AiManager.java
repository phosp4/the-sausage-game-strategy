package org.example.automation;

import org.example.engine.GameSession;
import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Point;
import org.example.entities.Sausage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiManager {

    private final Map<Player, AutoOpponent> aiPlayers = new HashMap<>();

//    public AiManager(PlayerType p1, PlayerType p2) {
//        registerAiPlayer();
//    }

    public void registerAiPlayer(Player player, AutoOpponent agent) {
        aiPlayers.put(player, agent);
    }

    public boolean isPlayerAi(Player player) {
        return aiPlayers.containsKey(player);
    }

    /**
     * executes move for a current player to move
     * zmena - ctrl odstraniť, nech to len vracia klobásku
     */
    public Sausage getAiMoveForPlayer(Player player, GameBoard g) {
        AutoOpponent agent = aiPlayers.get(player);

        if (agent != null) {
            return agent.getNextMove(g);
        } else {
            throw new RuntimeException("Current player does not have an agent associated.");
        }
    }
}
