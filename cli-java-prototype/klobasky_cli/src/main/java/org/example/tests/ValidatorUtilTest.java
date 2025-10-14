package org.example.tests;

import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Point;
import org.example.entities.Sausage;
import org.example.utils.MoveGenerator;
import org.example.utils.ValidatorUtil;
import org.junit.Test;
import org.junit.Assert;

import java.util.Set;

public class ValidatorUtilTest {

    @Test
    public void invalidPointsInGrid() {
        Sausage[][] grid = new Sausage[5][5];

        Point p1 = new Point(-1, 0);
        Point p2 = new Point(0, -1);
        Point p3 = new Point(0, 1);
        Point p4 = new Point(1, 0);
        Point p5 = new Point(5, 0);
        Point p6 = new Point(0, 5);

        Assert.assertFalse(ValidatorUtil.isPointValidForGrid(p1, grid));
        Assert.assertFalse(ValidatorUtil.isPointValidForGrid(p2, grid));
        Assert.assertFalse(ValidatorUtil.isPointValidForGrid(p3, grid));
        Assert.assertFalse(ValidatorUtil.isPointValidForGrid(p4, grid));
        Assert.assertFalse(ValidatorUtil.isPointValidForGrid(p5, grid));
        Assert.assertFalse(ValidatorUtil.isPointValidForGrid(p6, grid));
    }

    @Test
    public void occupiedPointsInGrid() {
        GameBoard gb = new GameBoard(5,5);
        gb.addSausage(new Sausage(new Point(0,0), new Point(0,2), new Point(0,4)));

        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 2);
        Point p3 = new Point(0, 4);
        Point p4 = new Point(1, 1);

        Assert.assertFalse(ValidatorUtil.isPointValidForGrid(p1, gb.getGrid()));
        Assert.assertFalse(ValidatorUtil.isPointValidForGrid(p2, gb.getGrid()));
        Assert.assertFalse(ValidatorUtil.isPointValidForGrid(p3, gb.getGrid()));
        Assert.assertTrue(ValidatorUtil.isPointValidForGrid(p4, gb.getGrid()));
    }

    @Test
    public void invalidSausage() {
        Sausage s1 = new Sausage(new Point(0,0), new Point(0,2), new Point(0,2));

        Assert.assertFalse(ValidatorUtil.isSausageValid(s1));
    }

    @Test
    public void intersectingSausages() {
        GameBoard gb = new GameBoard(5,5);
        Sausage s = new Sausage(new Point(2,0), new Point(2,2), new Point(2,4));
        gb.addSausage(s);

        Point p1 = new Point(1,1);
        Point p2 = new Point(3,1);
        Point p3 = new Point(0,2);

        Sausage s2 = new Sausage(p1, p2, p3);
        Assert.assertFalse(ValidatorUtil.haveNoIntersectionInGrid(p1, p2, gb.getGrid()));

    }
}
