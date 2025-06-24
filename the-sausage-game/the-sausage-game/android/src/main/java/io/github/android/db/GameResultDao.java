package io.github.android.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameResultDao {
    @Query("SELECT * FROM game_results")
    List<GameResultEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GameResultEntity result);
}
