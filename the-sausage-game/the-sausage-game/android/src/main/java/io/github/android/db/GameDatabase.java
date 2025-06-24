package io.github.android.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GameResultEntity.class}, version = 1)
public abstract class GameDatabase extends RoomDatabase {
    public abstract GameResultDao gameResultDao();

    private static volatile GameDatabase INSTANCE;

    public static GameDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GameDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GameDatabase.class, "game_results.db").build();
                }
            }
        }
        return INSTANCE;
    }
}
