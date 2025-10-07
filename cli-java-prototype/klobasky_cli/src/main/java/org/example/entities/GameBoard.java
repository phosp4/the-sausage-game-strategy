package org.example.entities;

import lombok.Getter;

import java.util.*;

@Getter
public class GameBoard {

    private Sausage[][] grid;
    private Deque<Sausage> sausages;

    public GameBoard(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Grid dimensions cannot be negative");
        }

        grid = new Sausage[y][x]; // filled with zeroes
        sausages = new ArrayDeque<>();;
    }

    public void addSausage(Sausage sausage) {
        // check inputs
        if (sausage == null) {
            throw new IllegalArgumentException("Sausage cannot be null");
        }

        // check if sausage is valid
        for (Dot dot : sausage.getThreeDots()) {
            checkDotForGrid(dot);
        }

        // TODO podla papiera kde to mam napisane
        // check if no intersection with existing sausages

        // add sausage to the grid
        for (Dot dot : sausage.getThreeDots()) {
            grid[dot.getOffsetY()][dot.getOffsetX()] = sausage; // adds the reference
        }
        sausages.add(sausage);
    }

    private void checkDotForGrid(Dot dot) {
        if (dot == null) {
            throw new IllegalArgumentException("Dot cannot be null");
        }
        if (dot.getOffsetY() < 0 || dot.getOffsetY() >= grid.length || dot.getOffsetX() < 0 || dot.getOffsetX() >= grid[0].length) {
            throw new IllegalArgumentException("Dot is out of bounds");
        }
    }

    public static void main(String[] args) {
        GameBoard g = new GameBoard(7,5);
        System.out.println(g);
    }

    public boolean isFull() {
        // todo implement
        return false;
    }

    // todo porozmyslat nad efektivnostou lebo toto bude behat velakrat
    public boolean isFirstPlayerWinner() {
        return this.isFull() && sausages.getLast().getPlayer() == sausages.getFirst().getPlayer();
    }

    public void removeLastSausage() {
        if (sausages.isEmpty()) {
            throw new IllegalStateException("No sausages to remove");
        }
        Sausage lastSausage = sausages.removeLast();
        for (Dot dot : lastSausage.getThreeDots()) {
            grid[dot.getOffsetY()][dot.getOffsetX()] = null; // reset to empty
        }
    }

    // todo implement
    public Sausage[] getAllPossibleMoves() {
        return null;
    }
}
