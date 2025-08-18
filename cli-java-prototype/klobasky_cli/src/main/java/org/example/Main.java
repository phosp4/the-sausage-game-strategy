package org.example;

public class Main {

    public void welcomeText() {

    }

    public static void main(String[] args) {

//        int activePlayer = 1;
//        System.out.println("Welcome to the sausage game!");
//        System.out.println(STR."Player: \{activePlayer}");
        Grid g = new Grid(7, 9);
        System.out.println(g);
        System.out.println(g.normalToString());
        g.addSausage(new Sausage(1, new Dot(0, 0), new Dot(0, 1), new Dot(0, 2)));
        g.addSausage(new Sausage(2, new Dot(1, 0), new Dot(1, 1), new Dot(1, 2)));
        g.addSausage(new Sausage(1, new Dot(2, 0), new Dot(2, 1), new Dot(2, 2)));
        System.out.println(g);
        System.out.println(g.normalToString());
        System.out.println(g.getSausages());
    }
}