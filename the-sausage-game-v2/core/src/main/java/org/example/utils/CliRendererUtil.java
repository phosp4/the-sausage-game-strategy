package org.example.utils;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;

import java.util.List;

public class CliRendererUtil {

    public static final String EMPTY_FIELD = "-";
    private static final String DELIMITER = " ";
    private static final int DELIMITER_LENGTH = 3;

    public static String gridToString(Sausage[][] grid) {

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < grid.length; i++) {
            StringBuilder lineBuilder = new StringBuilder();

            for (int j = 0; j < grid[i].length; j++) {
                if ((i + j) % 2 == 0) {
                    lineBuilder.append(grid[i][j] != null ? grid[i][j].getPlayer().getOneLetterNickname() : EMPTY_FIELD);
                } else {
                    lineBuilder.append(DELIMITER);
                }
                if (j < grid[i].length - 1) {
                    lineBuilder.append(DELIMITER.repeat(DELIMITER_LENGTH));
                }
            }
            out.append(lineBuilder).append("\n");
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

    public static void printAllPossibleMoves(GameBoard gameBoard, List<Sausage> possibleMoves) {
        for (Sausage s : possibleMoves) {
            gameBoard.addSausage(s);
            System.out.println(CliRendererUtil.gridToString(gameBoard.getGrid()));
            System.out.println("–––––––––––––");
            gameBoard.removeLastSausage();
        }
    }
}
