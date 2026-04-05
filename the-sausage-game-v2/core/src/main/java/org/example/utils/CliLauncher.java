package org.example.utils;

import org.example.engine.GameEngine;
import org.example.entities.Player;
import org.example.entities.Sausage;

public class CliLauncher {
    public static void main(String[] args) {
        GameEngine ctrl = new GameEngine();
        CliInputHandler cih = new CliInputHandler();

        System.out.println("Welcome to the Sausage Game!");
        System.out.println(CliRendererUtil.gridToString(ctrl.getGameBoard().getGrid()));

        while (!ctrl.getGameBoard().isGameOver()) {
            Player current = ctrl.getTurnManager().getCurrentPlayer();
            System.out.println("Current player: " + current.getName());

//            Sausage move = current.getName().equals("auto")
//                ? ctrl.pickRandomLegalMove()
//                : new CliInputHandler().nacitajSausage();
            Sausage move = cih.nacitajSausage();

            // toto vyzera kostrbato, ale aspon sa sausage vytvara na jednom mieste
            if (!ctrl.tryApplyMove(
                move.getThreePoints().get(0),
                move.getThreePoints().get(1),
                move.getThreePoints().get(2))) {
                System.out.println(ctrl.getLastError() + " Try again.");
                continue;
            }

            System.out.println(CliRendererUtil.gridToString(ctrl.getGameBoard().getGrid()));
        }
        System.out.println("Game over! Winner: " + ctrl.getTurnManager().getNotCurrentPlayer());
    }

}
