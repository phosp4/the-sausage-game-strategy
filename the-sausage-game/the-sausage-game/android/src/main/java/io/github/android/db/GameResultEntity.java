package io.github.android.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import io.github.data.GameResult;

@Entity(tableName = "game_results")
public class GameResultEntity {
    @PrimaryKey
    public long gameId;
    public String playerOne;
    public String playerTwo;
    public long date;
    public boolean playerOneWon;

    public GameResultEntity(long gameId, String playerOne, String playerTwo, long date, boolean playerOneWon) {
        this.gameId = gameId;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.date = date;
        this.playerOneWon = playerOneWon;
    }

    public GameResult toGameResult() {
        return new GameResult(gameId, playerOne, playerTwo, date, playerOneWon);
    }

    public static GameResultEntity fromGameResult(GameResult result) {
        return new GameResultEntity(result.getGameId(), result.getPlayerOne(), result.getPlayerTwo(), result.getDate(), result.isPlayerOneWon());
    }
}
