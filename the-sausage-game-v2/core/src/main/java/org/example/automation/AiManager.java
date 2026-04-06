package org.example.automation;

import org.example.entities.Player;
import org.example.entities.Sausage;

public class AiManager {

    private AutonomousOpponent ao;
    private Player aiPlayer;

    public AiManager(AutonomousOpponent ao, Player aiPlayer) {
        this.aiPlayer = aiPlayer;
        this.ao = ao;
    }

//    public Sausage getNextMoveForPlayer(Player p) {
//        if (!p.equals(aiPlayer)) {
//
//        }
//    }
}
