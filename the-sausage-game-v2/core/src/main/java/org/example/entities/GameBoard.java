package org.example.entities;

import lombok.Getter;
import org.example.exceptions.IntersectingSausagesException;
import org.example.exceptions.InvalidPointForGridException;
import org.example.utils.MoveGenerator;
import org.example.utils.ValidatorUtil;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

@Getter
public class GameBoard {

    private Sausage[][] grid;
    private Deque<Sausage> sausages;

    public GameBoard(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Grid dimensions cannot be negative");
        }

        grid = new Sausage[y][x]; // using doubled coordinates
        sausages = new ArrayDeque<>();;
    }

    public void addSausage(Sausage sausage) throws InvalidPointForGridException, IntersectingSausagesException {

        if (sausage == null) {
            throw new IllegalArgumentException("Sausage cannot be null.");
        }

        Point p1, p2, p3;
        p1 = sausage.getThreePoints().get(0);
        p2 = sausage.getThreePoints().get(1);
        p3 = sausage.getThreePoints().get(2);

        if (!ValidatorUtil.isPointValidForGrid(p1, this.grid) ||
                !ValidatorUtil.isPointValidForGrid(p2, this.grid) ||
                        !ValidatorUtil.isPointValidForGrid(p3, this.grid)) {
            throw new InvalidPointForGridException();
        }

        if (!ValidatorUtil.haveNoIntersectionInGrid(p1, p2, grid) ||
                !ValidatorUtil.haveNoIntersectionInGrid(p2, p3, grid) ||
                !ValidatorUtil.haveNoIntersectionInGrid(p1, p3, grid)) {
            throw new IntersectingSausagesException();
        }

        // mozno tu este raz validovatPointForGrid?

        // add sausage to the grid
        for (Point point : sausage.getThreePoints()) {
            grid[point.getY()][point.getX()] = sausage; // adds the reference
        }
        sausages.add(sausage);
    }

    public boolean isFull() {
        return MoveGenerator.getAllPossibleMoves(grid).isEmpty();
    }

    // todo porozmyslat nad efektivnostou lebo toto bude behat velakrat
    public boolean isFirstPlayerWinner() {
        return this.isFull() && sausages.getLast().getPlayer() == sausages.getFirst().getPlayer();
    }

    public Player getWinner() {
        if (!this.isFull()) {
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
}
