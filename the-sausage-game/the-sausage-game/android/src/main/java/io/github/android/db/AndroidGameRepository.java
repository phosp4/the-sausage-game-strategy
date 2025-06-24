package io.github.android.db;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.github.data.GameRepository;
import io.github.data.GameResult;

public class AndroidGameRepository implements GameRepository {
    private final GameResultDao dao;

    public AndroidGameRepository(Context context) {
        dao = GameDatabase.getInstance(context).gameResultDao();
    }

    @Override
    public List<GameResult> getAllGameResults() {
        List<GameResultEntity> entities = dao.getAll();
        List<GameResult> results = new ArrayList<>();
        for (GameResultEntity entity : entities) {
            results.add(entity.toGameResult());
        }
        return results;
    }

    @Override
    public void insertGameResult(GameResult result) {
        dao.insert(GameResultEntity.fromGameResult(result));
    }
}
