package io.github.data;

import java.util.ArrayList;
import java.util.List;

public class DummyGameRepository implements GameRepository {
    private final List<GameResult> results = new ArrayList<>();

    @Override
    public List<GameResult> getAllGameResults() {
        return new ArrayList<>(results);
    }

    @Override
    public void insertGameResult(GameResult result) {
        results.add(result);
    }
}
