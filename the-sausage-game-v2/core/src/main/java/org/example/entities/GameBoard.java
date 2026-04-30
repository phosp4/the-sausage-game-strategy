/**
 * problem - last sausage a metoda removeLastSausage nevedia spolu fungovat
 * aktualne tu je ale kazda na nieco ine, takze by to malo byt ok
 */

package org.example.entities;

import lombok.Getter;
import org.example.exceptions.IntersectingSausagesException;
import org.example.exceptions.InvalidPointForGridException;
import org.example.strategy_minimax.MoveGenerator;
import org.example.utils.ValidatorUtil;
import org.example.utils.ZobristHasher;

import java.io.Serializable;
import java.util.*;

@Getter
//@EqualsAndHashCode // nepouziavat - chceme custom
// predtym sa volala GameBoard, ale GameState je vystiznejsie
public class GameBoard implements Serializable {

    private Sausage[][] grid;
    private long zobristHash;
//    transient private Deque<Sausage> sausages;
//    private Sausage lastSausage = null; // nepotrebujeme - to riesi turnmanager

    public GameBoard(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Grid dimensions cannot be negative");
        }

        grid = new Sausage[y][x]; // using doubled coordinates
        zobristHash = ZobristHasher.calculateInitialHash(this);
    }

    public int getColumns() {
        return grid[0].length;
    }
    public int getRows() {
        return grid.length;
    }

    // metoda getXthPoint pridat

    public List<Point> getFreeNeighbours(Point anchor) {
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

    /**
     * pozor - O(n) metoda, v minimaxe toto nepouzivat
     */
    public List<Sausage> getSausages() {
        Set<Sausage> sausages = new HashSet<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != null) {
                    sausages.add(grid[i][j]);
                }
            }
        }
        return new ArrayList<>(sausages);
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

        // toto tu dlho chybalo !!!
        if (!ValidatorUtil.haveNoIntersectionInGrid(p3, p1, grid)) {
            throw new IntersectingSausagesException(p2, p3);
        }

        // mozno tu este raz validovatPointForGrid?

        // add sausage to the grid
        for (Point point : sausage.getThreePoints()) {
            grid[point.getY()][point.getX()] = sausage; // adds the reference
        }
        for (Point point : sausage.getTwoAdditionalPoints()) {
            grid[point.getY()][point.getX()] = sausage;
        }

        zobristHash = ZobristHasher.updateHashForSausage(zobristHash, sausage);
    }

    public boolean tryAddingSausageMinimax(Sausage sausage) {

        if (sausage == null) {
            return false;
        }

        if (!ValidatorUtil.isPointValidForGrid(sausage.getThreePoints().get(0), this.grid)) {
            return false;
        }
        if (!ValidatorUtil.isPointValidForGrid(sausage.getThreePoints().get(1), this.grid)) {
            return false;
        }
        if (!ValidatorUtil.isPointValidForGrid(sausage.getThreePoints().get(2), this.grid)) {
            return false;
        }

        if (!ValidatorUtil.haveNoIntersectionInGrid(
            sausage.getThreePoints().get(0),
            sausage.getThreePoints().get(1), grid)) {
            return false;
        }
        if (!ValidatorUtil.haveNoIntersectionInGrid(
            sausage.getThreePoints().get(1),
            sausage.getThreePoints().get(2), grid)) {
            return false;
        }
        if (!ValidatorUtil.haveNoIntersectionInGrid(
            sausage.getThreePoints().get(2),
            sausage.getThreePoints().get(0), grid)) {
            return false;
        }

        // add sausage to the grid
        for (Point point : sausage.getThreePoints()) {
            grid[point.getY()][point.getX()] = sausage; // adds the reference
        }
        for (Point point : sausage.getTwoAdditionalPoints()) {
            grid[point.getY()][point.getX()] = sausage;
        }

        zobristHash = ZobristHasher.updateHashForSausage(zobristHash, sausage);

        return true;
    }


    //    // <=> is full
    public boolean isBoardFull() {
        // zbytocne - staci skoncit, ked sa najde aspon jedna klobaska
        return MoveGenerator.getPossibleMoves(grid).isEmpty();
    }

    public boolean isOccupied(int x, int y) {
        return !(grid[y][x] == null);
    }

    public void removeSausage(Sausage s) {
        List<Point> threePoints = s.getThreePoints();

        // kontrola - ci existuju take body a ci tam klobaska takeho tvaru
        for (Point p : threePoints) {
            if (!grid[p.getY()][p.getX()].equals(s)) {
                throw new IllegalArgumentException("cannot remove this sausage: " + s);
            } else if (!ValidatorUtil.isPointValidForGridBounds(p, grid)) {
                throw new InvalidPointForGridException(p);
            }
        }
        // samotne vymazanie klobasky
        for (Point p : threePoints) {
            grid[p.getY()][p.getX()] = null;
        }
        for (Point p : s.getTwoAdditionalPoints()) {
            grid[p.getY()][p.getX()] = null;
        }

        zobristHash = ZobristHasher.updateHashForSausage(zobristHash, s);
    }

    /**
     * v tomto je mozno chyba
     * lebo ked som v minimaxe ukladal GameBoard to mapy (nie zakodovany long)
     * zavolalo to memo aj ked to zavolat nemalo
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameBoard)) return false;
        GameBoard other = (GameBoard) o;

        // staci porovnat hashe
        if (zobristHash != other.getZobristHash()) return false;

        // ak by teoreticky nastala kolizia
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

    @Override
    public int hashCode() {
        return ZobristHasher.toJavaHashCode(zobristHash);
    }

//    @Override
//    public int hashCode() {
//        Sausage[][] grid = this.getGrid();
//        int result = 31 * grid.length + grid[0].length;
//
//        for (int i = 0; i < grid.length; i++) {
//            for (int j = 0; j < grid[0].length; j++) {
//                if (((i + j) % 2 == 0)) {
//                    result = 31 * result + (grid[i][j] == null ? 0 : 1);
//                }
//            }
//        }
//        return result;
//    }
}
