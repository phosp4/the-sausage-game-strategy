package org.example.utils;

import org.example.entities.Player;
import org.example.entities.Point;
import org.example.entities.Sausage;

import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveGenerator {

    private static final Logger log = LoggerFactory.getLogger(MoveGenerator.class);

    public static Set<Sausage> getAllPossibleMoves(Sausage[][] grid) {
        int[][] vectors = {
                {0,2}, {0,-2},
                {2,0}, {-2,0},
                {1,1}, {-1,-1},
                {1,-1}, {-1,1}
        };
        Set<Sausage> validMoves = new TreeSet<>();
        Player player = new Player("tester");

        // iteruj gridom, pre kazdu bodku skusaj
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Point p1 = new Point(j, i);
                log.debug("Validating point no 1: {}", p1);
                if (!ValidatorUtil.isPointValidForGrid(p1, grid)) continue;

                for (int[] v1 : vectors) {
                    Point p2 = new Point(j + v1[0], i + v1[1]);
                    log.debug("Validating point no 2: {}", p2);
                    if (!ValidatorUtil.isPointValidForGrid(p2, grid)) continue;
                    if (!ValidatorUtil.hasNoIntersectionInGrid(p1, p2, grid)) continue;

                    for (int[] v2 : vectors) {
                        Point p3 = new Point(j + v2[0], i + v2[1]);
                        log.debug("Validating point no 3: {}", p3);
                        if (!ValidatorUtil.isPointValidForGrid(p3, grid)) continue;
                        if (!ValidatorUtil.hasNoIntersectionInGrid(p2, p3, grid)) continue;

                        Sausage sausage = new Sausage(player, p1,p2,p3);
                        log.debug("Validating sausage: {}", sausage);
                        if (ValidatorUtil.isSausageValid(sausage)) {
                            log.debug("This sausage is valid: {}", sausage);
                            validMoves.add(sausage);
                        }
                    }
                }
            }
        }
        return validMoves;
    }
}
