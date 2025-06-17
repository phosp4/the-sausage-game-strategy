package org.example;

public class TurnManager {
    private Player player1;
    private Player player2;
    private boolean isPlayer1Turn;

    public TurnManager(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.isPlayer1Turn = true; // or randomize for fairness
    }

    public Player getCurrentPlayer() {
        return isPlayer1Turn ? player1 : player2;
    }

    public void nextTurn() {
        isPlayer1Turn = !isPlayer1Turn;
    }
//    public boolean isGameOver(Grid grid) {
////        // delegate to some WinChecker or RuleEngine
////        return grid.checkWin() || grid.isFull();
//        // todo
//        return false;
//    }
}