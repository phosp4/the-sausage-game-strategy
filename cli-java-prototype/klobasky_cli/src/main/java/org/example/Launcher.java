package org.example;

import org.example.entities.Dot;
import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Sausage;

public class Launcher {

    public void welcomeText() {

    }
    
    public static void main(String[] args) {

//        int activePlayer = 1;
//        System.out.println("Welcome to the sausage game!");
//        System.out.println(STR."Player: \{activePlayer}");
        Player p1 = new Player("asdf");
        Player p2 = new Player("jkl;");

        GameBoard g = new GameBoard(7, 9);
        System.out.println(CliRenderer.gridToString(g.getGrid()));
        System.out.println(CliRenderer.gridToStringAsArray(g.getGrid()));
        g.addSausage(new Sausage(p1, new Dot(0, 0), new Dot(0, 1), new Dot(0, 2)));
        g.addSausage(new Sausage(p2, new Dot(1, 0), new Dot(1, 1), new Dot(1, 2)));
        g.addSausage(new Sausage(p1, new Dot(2, 0), new Dot(2, 1), new Dot(2, 2)));
        System.out.println(CliRenderer.gridToString(g.getGrid()));
        System.out.println(CliRenderer.gridToStringAsArray(g.getGrid()));
        System.out.println(g.getSausages());
    }
}