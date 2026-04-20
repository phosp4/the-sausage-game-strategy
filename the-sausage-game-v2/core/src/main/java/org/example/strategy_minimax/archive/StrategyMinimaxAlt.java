package org.example.strategy_minimax.archive;

import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Sausage;
import org.example.strategy_minimax.MoveGenerator;

public class StrategyMinimaxAlt {

    private Player p1 = new Player("A");
    private Player p2 = new Player("B");

    /**
     * povodny minimax, plus pruning
     */
    public int minimax(GameBoard gameBoard, boolean isMaximizingPlayer) {

        if (gameBoard.isGameOver()) {
            if (isMaximizingPlayer) {
                return 1;
            } else {
                return -1;
            }
        }

        if (isMaximizingPlayer) {
            int bestValue = -1; // to je ako -infinity
            for (Sausage move : MoveGenerator.getPossibleMoves(gameBoard.getGrid(), p1)) { // pojde to asi aj s O(1) priestorovou
                gameBoard.addSausage(move);
                int value = minimax(gameBoard, false);
                gameBoard.removeSausage(move);
                bestValue = Math.max(value, bestValue);
                if (bestValue == 1) break;
            }
            return bestValue;
        }

        else {
            int bestValue = 1; // to je ako +infinity
            for (Sausage move : MoveGenerator.getPossibleMoves(gameBoard.getGrid(), p2)) {
                gameBoard.addSausage(move);
                int value = minimax(gameBoard, true);
                gameBoard.removeSausage(move);
                bestValue = Math.min(value, bestValue);
                // tu nedavame podmienku - chceme hladat pre vsetky
            }
            return bestValue;
        }
    }

    /**
     * toto by malo ist, ale je to zbytocne vseobecne pre nase ucely
     */
    public int minimaxAB(GameBoard gameBoard, boolean isMaximizingPlayer, int alpha, int beta) {

        if (gameBoard.isGameOver()) {
            if (isMaximizingPlayer) {
                return 1;
            } else {
                return -1;
            }
        }

        if (isMaximizingPlayer) {
            int bestValue = -1; // to je ako -infinity
            for (Sausage move : MoveGenerator.getPossibleMoves(gameBoard.getGrid(), p1)) { // pojde to asi aj s O(1) priestorovou
                gameBoard.addSausage(move);
                int value = minimaxAB(gameBoard, false, alpha, beta);
                gameBoard.removeSausage(move);
                bestValue = Math.max(value, bestValue);
                alpha = Math.max(alpha, value);
                if (beta <= alpha) break;
                if (bestValue == 1) break; // jednoducha optimalizacia -staci nam jeden sposob na minimalizaciu / maximalizaciu
            }
            return bestValue;
        }

        else {
            int bestValue = 1; // to je ako +infinity
            for (Sausage move : MoveGenerator.getPossibleMoves(gameBoard.getGrid(), p2)) {
                gameBoard.addSausage(move);
                int value = minimaxAB(gameBoard, true, alpha, beta);
                gameBoard.removeSausage(move);
                bestValue = Math.min(value, bestValue);
                beta = Math.min(beta, value);
                if (beta <= alpha) break;
//                if (bestValue == -1) break; // mozeme *si trufnut* predpokladat, ze super si vyberie tuto cestu; jedina dalsia moznost je 1, ale to nam neuskodi - chceme byt pesimisticky
            }
            return bestValue;
        }
    }
}
