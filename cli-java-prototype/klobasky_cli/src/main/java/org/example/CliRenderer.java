package org.example;

import org.example.entities.Sausage;

public class CliRenderer {

    public static final String EMPTY_FIELD = "-";

    public static String gridToString(Sausage[][] grid) {

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < grid.length; i++) {
            int delimiterLength = 7; // should be an odd number
            String delimiter = " ";
            String line;

            if (i % 2 == 0) {
                StringBuilder lineBuilder = new StringBuilder();
                for (int j = 0; j < grid[i].length; j++) {
                    lineBuilder.append(grid[i][j] != null? grid[i][j].getPlayer().getOneLetterNickname():EMPTY_FIELD);
                    if (j < grid[i].length - 1) {
                        lineBuilder.append(delimiter.repeat(delimiterLength));
                    }
                }
                line = lineBuilder.toString();
            } else {
                StringBuilder lineBuilder = new StringBuilder();
                lineBuilder.append(delimiter.repeat((delimiterLength / 2) + 1));
                for (int j = 0; j < grid[i].length - 1; j++) {
                    lineBuilder.append(grid[i][j] != null? grid[i][j].getPlayer().getOneLetterNickname():EMPTY_FIELD);
                    if (j < grid[i].length - 2) {
                        lineBuilder.append(delimiter.repeat(delimiterLength));
                    }
                }
                line = lineBuilder.toString();
            }

            out.append(line).append("\n");
        }

        return out.toString();
    }

    public static String gridToStringAsArray(Sausage[][] grid) {

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                out.append(grid[i][j] != null? grid[i][j].getPlayer().getOneLetterNickname() : EMPTY_FIELD);
                if (j < grid[i].length - 1) {
                    out.append(" ");
                }
            }
            out.append("\n");
        }

        return out.toString();
    }
}
