package org.example.utils;

import org.example.engine.GameController;
import org.example.entities.Player;
import org.example.entities.Sausage;

public class CliLauncher {
    public static void main(String[] args) {
        Player p1 = new Player("P1");
        Player p2 = new Player("auto");
        GameController ctrl = new GameController(5, 5, p1, p2, null);

        System.out.println("Welcome to the Sausage Game!");
        System.out.println(CliRendererUtil.gridToString(ctrl.getGameState().getGrid()));

        while (!ctrl.getGameState().isGameOver()) {
            Player current = ctrl.getTurnManager().getCurrentPlayer();
            System.out.println("Current player: " + current.getName());

            Sausage move = current.getName().equals("auto")
                ? ctrl.pickRandomLegalMove()
                : new CliInputHandler().nacitajSausage();

            if (!ctrl.tryApplyMove(move)) {
                System.out.println(ctrl.getLastError() + " Try again.");
                continue;
            }

            System.out.println(CliRendererUtil.gridToString(ctrl.getGameState().getGrid()));
        }
        System.out.println("Game over! Winner: " + ctrl.getTurnManager().getNotCurrentPlayer());
    }

}
