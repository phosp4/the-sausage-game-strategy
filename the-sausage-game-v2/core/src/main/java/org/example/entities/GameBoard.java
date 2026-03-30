package org.example.entities;

import lombok.Getter;
import org.example.exceptions.IntersectingSausagesException;
import org.example.exceptions.InvalidPointForGridException;
import org.example.strategy.MoveGenerator;
import org.example.utils.ValidatorUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Getter
//@EqualsAndHashCode // nepouziavat - chceme custom
public class GameBoard {

    private Sausage[][] grid;
    private Deque<Sausage> sausages;

    public GameBoard(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Grid dimensions cannot be negative");
        }

        grid = new Sausage[y][x]; // using doubled coordinates
        sausages = new ArrayDeque<>();
    }

    // metoda getXthPoint pridat

    public List<Point> getNeighbours(Point anchor) {
        List<Point> neighbours = new ArrayList<>();

        int[][] vectors = {
            {0,-2}, {1,-1}, {2,0}, {1,1}, {0,2}, {-1,1}, {-2,0}, {-1,-1}
        };
        for (int[] v1 : vectors) {
            Point p = new Point(anchor.getX() + v1[0], anchor.getY() + v1[1]);

            if (ValidatorUtil.isPointValidForGrid(p, grid) &&
                ValidatorUtil.haveNoIntersectionInGrid(anchor, p, grid)) {
                neighbours.add(p);
            };
        }

        return neighbours;
    }

    public void addSausage(Sausage sausage) throws InvalidPointForGridException, IntersectingSausagesException {

        if (sausage == null) {
            throw new IllegalArgumentException("Sausage cannot be null.");
        }

        Point p1, p2, p3;
        p1 = sausage.getThreePoints().get(0);
        p2 = sausage.getThreePoints().get(1);
        p3 = sausage.getThreePoints().get(2);

        if (!ValidatorUtil.isPointValidForGrid(p1, this.grid)) {
            throw new InvalidPointForGridException(p1);
        }
        if (!ValidatorUtil.isPointValidForGrid(p2, this.grid)) {
            throw new InvalidPointForGridException(p2);
        }
        if (!ValidatorUtil.isPointValidForGrid(p3, this.grid)) {
            throw new InvalidPointForGridException(p3);
        }

        if (!ValidatorUtil.haveNoIntersectionInGrid(p1, p2, grid)) {
            throw new IntersectingSausagesException(p1, p2);
        }
        if (!ValidatorUtil.haveNoIntersectionInGrid(p2, p3, grid)) {
            throw new IntersectingSausagesException(p2, p3);
        }

        // mozno tu este raz validovatPointForGrid?

        // add sausage to the grid
        for (Point point : sausage.getThreePoints()) {
            grid[point.getY()][point.getX()] = sausage; // adds the reference
        }
        sausages.add(sausage);
    }

    // <=> is full
    public boolean isGameOver() {
        return MoveGenerator.getAllPossibleMoves(grid).isEmpty();
    }

    public boolean isOccupied(int x, int y) {
        return !(grid[y][x] == null);
    }

    // todo porozmyslat nad efektivnostou lebo toto bude behat velakrat
    public boolean isFirstPlayerWinner() {
        if (!sausages.isEmpty()) {
            return this.isGameOver() && sausages.getLast().getPlayer() == sausages.getFirst().getPlayer();
        } else {
            return false;
        }
    }

    public Player getWinner() {
        if (!this.isGameOver()) {
            return null;
        }
        return sausages.getLast().getPlayer();
    }

    public void removeLastSausage() {
        if (sausages.isEmpty()) {
            throw new IllegalStateException("No sausages to remove");
        }
        Sausage lastSausage = sausages.removeLast();
        for (Point p : lastSausage.getThreePoints()) {
            grid[p.getY()][p.getX()] = null; // reset to empty
        }
    }

    // equals and hash code by chatgpt - este skontrolovat //

    private static boolean[][] sausageGridToBooleanGrid(Sausage[][] grid) {

        boolean[][] out = new boolean[grid.length][grid[0].length];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                out[i][j] = (grid[i][j] != null);
            }
        }
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameBoard)) return false;
        GameBoard other = (GameBoard) o;

        Sausage[][] g1 = this.getGrid();
        Sausage[][] g2 = other.getGrid();

        if (g1.length != g2.length || g1[0].length != g2[0].length) {
            return false;
        }

        boolean aNull;
        boolean bNull;

        for (int i = 0; i < this.getGrid().length; i++) {
            for (int j = 0; j < this.getGrid()[0].length; j++) {
                aNull = (g1[i][j] == null);
                bNull = (g2[i][j] == null);
                if (aNull != bNull) {
                    return false;
                }
        }
        }
        return true;
    }

    // je toto naozaj dobre?
    @Override
    public int hashCode() {
        Sausage[][] grid = this.getGrid();
        int result = 31 * grid.length + grid[0].length;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                result = 31 * result + (grid[i][j] == null ? 0 : 1);
            }
        }
        return result;
    }

    // ###########################

    /**
     * Converts a 2D Object array to a 2D boolean array.
     * true = field is occupied (not null)
     * false = field is null
     */
    public boolean[][] toBooleanArray() {
        if (grid == null || grid.length == 0) return new boolean[0][0];

        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][] boolArray = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // If the field is not null, it's occupied (true)
                boolArray[i][j] = (grid[i][j] != null);
            }
        }
        return boolArray;
    }
}
