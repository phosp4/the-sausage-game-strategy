package org.example.automation;

import org.example.entities.Player;
import org.example.entities.Sausage;

import java.util.Map;

public class AiManager {

    private Player aiPlayer;
    private Map<Long, Long> strategy = null;

    public AiManager(int x, int y, Player aiPlayer) {
        this.aiPlayer = aiPlayer;
    }

    public Sausage getNextMoveMinimax(int x, ) {
        if
    }
}

enum StrategyType {
    RANDOM, MINIMAX
}
