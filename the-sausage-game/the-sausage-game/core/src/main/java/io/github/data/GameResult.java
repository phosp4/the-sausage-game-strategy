package io.github.data;

public class GameResult {
    private long gameId;
    private String playerOne;
    private String playerTwo;
    private long date;
    private boolean playerOneWon;

    public GameResult(long gameId, String playerOne, String playerTwo, long date, boolean playerOneWon) {
        this.gameId = gameId;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.date = date;
        this.playerOneWon = playerOneWon;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isPlayerOneWon() {
        return playerOneWon;
    }

    public void setPlayerOneWon(boolean playerOneWon) {
        this.playerOneWon = playerOneWon;
    }
}
