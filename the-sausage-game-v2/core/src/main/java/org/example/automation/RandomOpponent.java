package org.example.automation;

import org.example.entities.Sausage;
import org.example.utils.MoveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomOpponent implements AutonomousOpponent {

    @Override
    public Sausage getAMove(Sausage[][] grid) {
        List<Sausage> possibleMoves = new ArrayList<>(MoveGenerator.getAllPossibleMoves(grid));
        if (possibleMoves.isEmpty()) {
            return null;
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(possibleMoves.size());
        return possibleMoves.get(randomIndex);
    }
}
