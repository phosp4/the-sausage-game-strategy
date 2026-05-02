package org.example.utils;

import org.example.entities.GameBoard;
import org.example.entities.Sausage;

import java.util.List;

public class CliRendererUtil {

    public static final String EMPTY_FIELD = "-";
    private static final String DELIMITER = " ";
    private static final int DELIMITER_LENGTH = 3;
    private static final String FILLED_FIELD = "X";

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

    public static String bitboardToString(long bitboard, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive.");
        }
        if ((long) width * height > Long.SIZE) {
            throw new IllegalArgumentException("Board is too large for one long bitboard.");
        }

        StringBuilder out = new StringBuilder();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = y * width + x;
                out.append(((bitboard >>> idx) & 1L) == 1L ? FILLED_FIELD : EMPTY_FIELD);
                if (x < width - 1) {
                    out.append(DELIMITER);
                }
            }
            out.append("\n");
        }

        return out.toString();
    }

    public static void printAllPossibleMoves(GameBoard gameBoard, List<Sausage> possibleMoves) {
        for (Sausage s : possibleMoves) {
            gameBoard.addSausage(s);
            System.out.println(CliRendererUtil.gridToString(gameBoard.getGrid()) + "sausage: " + s);
            System.out.println("–––––––––––––");
            gameBoard.removeSausage(s);
        }
    }
}
