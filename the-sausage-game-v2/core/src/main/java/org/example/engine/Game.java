// toto vlastne netreba

package org.example.engine;

import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Sausage;
import org.example.exceptions.IntersectingSausagesException;
import org.example.exceptions.InvalidPointForGridException;
import org.example.utils.CliRendererUtil;
import org.example.strategy.MoveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * este to nie je uplne vseobecne, ale funguje ako ukazka
 * rozmery tiez by mohlo brat ako input a podmienky nejako lepsie by mohli byt poriesene - asi nejake catche...
 */
public class Game {
    GameBoard gameBoard;
    TurnManager turnManager;
    InputHandler inputHandler = new InputHandlerCli();

    public Game(int x, int y) {
        System.out.println("Welcome to the Sausage Game!");
//        Player player1 = new Player(inputHandler.getPlayerName());
//        Player player2 = new Player(inputHandler.getPlayerName());
        Player player1 = new Player("P1");
        Player player2 = new Player("auto");
        this.turnManager = new TurnManager(player1, player2);
        this.gameBoard = new GameBoard(x, y);

        // aj na rozmery by sa tu malo pytat
    }

    public void start() {

        System.out.println(CliRendererUtil.gridToString(gameBoard.getGrid()));

        while (!gameBoard.isGameOver()) {
            Player currentPlayer = turnManager.getCurrentPlayer();
            System.out.println("Current player: " + currentPlayer.getName());

//            // all moves testing
//            Set<Sausage> allPossibleMoves = MoveGenerator.getAllPossibleMoves(gameBoard.getGrid());
////            System.out.println(allPossibleMoves);
////            CliRendererUtil.printAllPossibleMoves(gameBoard, new ArrayList<>(allPossibleMoves));
//            System.out.println("Number of moves: " + allPossibleMoves.size());
//            //

            Sausage turn;
            if (currentPlayer.getName().equals("auto")) {
                List<Sausage> possibleMoves = new ArrayList<>(MoveGenerator.getAllPossibleMoves(gameBoard.getGrid()));
                System.out.println("Possible moves: " + possibleMoves);
                int randomIndex = ThreadLocalRandom.current().nextInt(possibleMoves.size());
                turn = possibleMoves.get(randomIndex);
                turn.setPlayer(currentPlayer);
            } else {
                turn = inputHandler.nacitajSausage();
                turn.setPlayer(currentPlayer);
            }

            // neviem ci toto ma byt tu, asi nn
            try {
                gameBoard.addSausage(turn);
            } catch (InvalidPointForGridException e) {
                System.out.println("Invalid sausage placement. Try again.");
                continue; // retry the turn
            } catch (IntersectingSausagesException e) {
                System.out.println("Sausage intersects with another sausage. Try again.");
                continue; // retry the turn
            }


            System.out.println(CliRendererUtil.gridToString(gameBoard.getGrid()));
            turnManager.nextTurn();
        }

        System.out.println("Game over!");
        System.out.println("Winner: " + gameBoard.getWinner().getName());
    }

//    public static void main(String[] args) {
//        Game game = new Game(5, 5); // tu by malo podsuvat najaky interface na pracu (napr. nazvat GameView)
//        game.start();
//
//    }
}
