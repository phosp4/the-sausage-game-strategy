package org.example.engine;

import org.example.entities.Player;
import org.example.entities.Sausage;
import org.example.utils.CliInputHandler;
import org.example.utils.CliRendererUtil;

public class CliLauncher {

    public static void main(String[] args) {
        twoPlayers();
//        aiTester();
    }

//    public static void aiTester() {
//        GameEngine ctrl = new GameEngine();
//
//        System.out.println("Welcome to the Sausage Game!");
//        System.out.println(CliRendererUtil.gridToString(ctrl.getGameBoard().getGrid()));
//
//        while (!ctrl.getGameBoard().isGameOver()) {
//
//            Player current = ctrl.getTurnManager().getCurrentPlayer();
//            System.out.println("Current player: " + current.getName());
//
//            if (ctrl.getTurnManager().getCurrentPlayer().equals(ctrl.getAutoPlayer())) {
//                //todo - not so ez
//            }
//
//            System.out.println(CliRendererUtil.gridToString(ctrl.getGameBoard().getGrid()));
//        }
//        System.out.println("Game over! Winner: " + ctrl.getTurnManager().getNotCurrentPlayer());
//    }

    public static void twoPlayers() {

        GameSession ctrl = new GameSession(9,7, false);
        CliInputHandler cih = new CliInputHandler();

        System.out.println("Welcome to the Sausage Game!");
        System.out.println(CliRendererUtil.gridToString(ctrl.getGameBoard().getGrid()));

        while (!ctrl.getGameBoard().isBoardFull()) {
            Player current = ctrl.getTurnManager().getCurrentPlayer();
            System.out.println("Current player: " + current.getName());

            Sausage move = cih.nacitajSausage();
            ctrl.tryApplyMove(move);

            System.out.println(CliRendererUtil.gridToString(ctrl.getGameBoard().getGrid()));
        }
        System.out.println("Game over! Winner: " + ctrl.getTurnManager().getNotCurrentPlayer());
    }

}
