package org.example.utils;

import org.example.entities.Point;
import org.example.entities.Sausage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatorUtil {

    private static final Logger log = LoggerFactory.getLogger(ValidatorUtil.class);

    private static boolean isPointValid(Point p) {
        if ((p.getX()+p.getY()) % 2 != 0) {
            return false;
        }
        if (p.getX() < 0 || p.getY() < 0) {
            return false;
        }
        return true;
    }

    public static boolean isPointValidForGrid(Point p, Sausage[][] grid) {

        if (!isPointValid(p)) {
            return false;
            }

        if (p.getX() < 0 || p.getX() >= grid[0].length ||
                p.getY() < 0 || p.getY() >= grid.length) {
            log.debug("Sausage point out of bounds: {}", p);
            return false;
        }
        if (grid[p.getY()][p.getX()] != null) {
            log.debug("Sausage point already occupied: {}", p);
            return false;
        }

        return true;
    }

    public static boolean isSausageValid(Sausage s) {
        if (s.getThreePoints().size() != 3) {
            return false;
        }
        return true;
    }

    public static boolean hasNoIntersectionInGrid(Point p1, Point p2, Sausage[][] grid) {

        // ak je to vodorovne spojenie
        if (Math.abs(p2.getX() - p1.getX()) == 2) {

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
        if (Math.abs(p2.getY() - p1.getY()) == 2) {

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
}
