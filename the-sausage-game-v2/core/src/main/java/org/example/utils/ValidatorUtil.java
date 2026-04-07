package org.example.utils;

import org.example.entities.Point;
import org.example.entities.Sausage;
import org.example.exceptions.InvalidPointForGridException;

import java.util.List;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class ValidatorUtil {

//    private static final Logger log = LoggerFactory.getLogger(ValidatorUtil.class);

    public static boolean areNeigbours(Point p1, Point p2) {
        return (Math.max(p1.getX(), p2.getX()) - Math.min(p1.getX(), p2.getX()) +
                Math.max(p1.getY(), p2.getY()) - Math.min(p1.getY(), p2.getY()) <= 2);
    }

    private static boolean isPointValid(Point p) {
        if ((p.getX()+p.getY()) % 2 != 0) {
            return false;
        }
        if (p.getX() < 0 || p.getY() < 0) {
            return false;
        }
        return true;
    }

    public static boolean isPointValidForGridBounds(Point p, Sausage[][] grid) {
        if (p.getX() < 0 || p.getX() >= grid[0].length ||
            p.getY() < 0 || p.getY() >= grid.length) {
//            throw new InvalidPointForGridException(p, "Sausage point out of bounds");
            return false;
        }
        return true;
    }

    public static boolean isPointValidForGrid(Point p, Sausage[][] grid) {

        if (!isPointValid(p)) {
            return false;
            }

        if (!isPointValidForGridBounds(p, grid)) {
            return false;
        }

        if (grid[p.getY()][p.getX()] != null) {
//            throw new InvalidPointForGridException(p, "Sausage point already occupied");
            return false;
        }

        return true;
    }

    public static boolean isSausageValid(Sausage s) {
        List<Point> points = s.getThreePoints();
        return !points.get(0).equals(points.get(1)) &&
                !points.get(1).equals(points.get(2)) &&
                !points.get(0).equals(points.get(2));
    }

    public static boolean haveNoIntersectionInGrid(Point p1, Point p2, Sausage[][] grid) {

        // trivial intersection
        if (p1.equals(p2)) {
            return false;
        }

        // ak je to vodorovne spojenie
        if (Math.abs(p2.getX() - p1.getX()) == 2 && p2.getY() == p1.getY()) {

            // zober suradnice bodu nalavo
            int ii = p2.getX() - p1.getX() > 0 ? p1.getX() : p2.getX();
            int jj = p2.getX() - p1.getX() > 0 ? p1.getY() : p2.getY();

            // skontroluj ci nepresahuje grid
            if (ii + 1 < grid[0].length &&
                jj + 1 < grid.length && jj - 1 >= 0) {

                // skontroluj ci tam je instancia tej istej klobasky
                Sausage s1 = grid[jj-1][ii+1];
                Sausage s2 = grid[jj+1][ii+1];
                if (s1 != null && s1 == s2) {
                    return false;
                }
            }
        }

        // ak je to zvisle spojenie
        if (Math.abs(p2.getY() - p1.getY()) == 2 && p2.getX() == p1.getX()) {

            // zober suradnice bodu hore
            int ii = p2.getY() - p1.getY() > 0 ? p1.getX() : p2.getX();
            int jj = p2.getY() - p1.getY() > 0 ? p1.getY() : p2.getY();

            // skontroluj ci nepresahuje grid
            if (jj + 1 < grid.length &&
                    ii + 1 < grid[0].length && ii - 1 >= 0) {

                // skontroluj ci tam je instancia tej istej klobasky
                Sausage s1 = grid[jj+1][ii-1];
                Sausage s2 = grid[jj+1][ii+1];
                if (s1 != null && s1 == s2) {
                    return false;
                }
            }
        }
        return true;
    }

//    public static int[] getMiddlePoint(Point p1, Point p2) {
//        // ak je to vodorovne spojenie
//        if (Math.abs(p2.getX() - p1.getX()) == 2 && p2.getY() == p1.getY()) {
//
//            // zober suradnice bodu nalavo
//            int ii = p2.getX() - p1.getX() > 0 ? p1.getX() : p2.getX();
//            int jj = p2.getX() - p1.getX() > 0 ? p1.getY() : p2.getY();
//
//            return new int[]{ii + 1, jj};
//        }
//
//        // ak je to zvisle spojenie
//        if (Math.abs(p2.getY() - p1.getY()) == 2 && p2.getX() == p1.getX()) {
//
//            // zober suradnice bodu hore
//            int ii = p2.getY() - p1.getY() > 0 ? p1.getX() : p2.getX();
//            int jj = p2.getY() - p1.getY() > 0 ? p1.getY() : p2.getY();
//
//            return new int[]{ii, jj + 1};
//        }
//
//        return null;
//    }
}
