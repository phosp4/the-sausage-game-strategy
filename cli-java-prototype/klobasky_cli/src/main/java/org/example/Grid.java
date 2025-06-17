package org.example;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class Grid {

    private int[][] grid;
    private List<Sausage> sausages; // alt. using stack

    Grid(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Grid dimensions cannot be negative");
        }

        grid = new int[x][y]; // filled with zeroes
        for (int i = 0; i < grid.length; i++) {
            if (i % 2 == 1) {
                grid[i][grid[i].length - 1] = GridConstants.INVALID;
            }
        }
        sausages = new ArrayList<>();
    }

    public void addSausage(Sausage sausage) {
        // check inputs
        if (sausage == null) {
            throw new IllegalArgumentException("Sausage cannot be null");
        }
        if (sausage.getPlayer() != GridConstants.PLAYER_ONE && sausage.getPlayer() != GridConstants.PLAYER_TWO) {
            throw new IllegalArgumentException("Invalid player");
        }

        // check if sausage is valid
        for (Dot dot : sausage.getThreeDots()) {
            checkDotForGrid(dot);
        }

        // add sausage to the grid
        for (Dot dot : sausage.getThreeDots()) {
            grid[dot.getX()][dot.getY()] = sausage.getPlayer();
        }
        sausages.add(sausage);
    }

    private void checkDotForGrid(Dot dot) {
        if (dot == null) {
            throw new IllegalArgumentException("Dot cannot be null");
        }
        if (grid[dot.getX()][dot.getY()] == GridConstants.PLAYER_ONE ||
                grid[dot.getX()][dot.getY()] == GridConstants.PLAYER_TWO) {
            throw new IllegalArgumentException("Dot is already occupied");
        }
        if (dot.getX() < 0 || dot.getX() >= grid.length || dot.getY() < 0 || dot.getY() >= grid[0].length ||
                grid[dot.getX()][dot.getY()] == GridConstants.INVALID) {
            throw new IllegalArgumentException("Dot is out of bounds");
        }
    }
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < grid.length; i++) {
            int delimiterLength = 7; // should be an odd number
            String delimiter = " ";
            String line;

            if (i % 2 == 0) {
                line = Arrays.stream(grid[i])
                        .mapToObj(this::convertValueToSymbol)
                        .collect(Collectors.joining(delimiter.repeat(delimiterLength)));
            } else {
                int finalI = i;
                line = delimiter.repeat((delimiterLength / 2) + 1) + IntStream.range(0, grid[i].length - 1)
                        .mapToObj(j -> convertValueToSymbol(grid[finalI][j]))
                        .collect(Collectors.joining(delimiter.repeat(delimiterLength)));
            }

            out.append(line).append("\n");
        }

        return out.toString();
    }

    private String convertValueToSymbol(int value) {
        return switch (value) {
            case 0 -> "–";
            case 1 -> "X"; // Player One
            case 2 -> "O"; // Player Two
            case -1 -> "~"; // Invalid
            default -> "?"; // Unknown
        };
    }

    public static void main(String[] args) {
        Grid g = new Grid(7,5);
        System.out.println(g);
    }
}
