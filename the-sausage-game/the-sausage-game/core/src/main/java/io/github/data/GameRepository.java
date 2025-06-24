package io.github.data;

import java.util.List;

public interface GameRepository {
    List<GameResult> getAllGameResults();
    void insertGameResult(GameResult result);
}
