package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CoordUtils {

    /*
    * offset coordinates are used for actual storage in memery
    * doubled coordinates are useful for shape calculations
    * see this for more details: https://www.redblobgames.com/grids/hexagons/#coordinates
     */

    // covert offset coordinates to doubled coordinates
    public static int offsetToDoubledX(Dot d) {
        return 2 * d.getX() + d.getY() % 2;
    }
    public static int offsetToDoubledY(Dot d) {
        return d.getY(); // no change needed for y
    }

    // convert doubled coordinates to offset coordinates
    public static int doubledToOffsetX(Dot d) {
        return d.getX() / 2; // purposefully integer division
    }
    public static int doubledToOffsetY(Dot d) {
        return d.getY(); // no change needed for y
    }

    public static boolean isValidShape(Dot d1, Dot d2, Dot d3) {

        // Create an array to store doubled coordinates
        int[][] doubledCoords = new int[3][2];
        Dot[] dots = {d1, d2, d3};

        // Convert to doubled coordinates and store in the array
        for (int i = 0; i < 3; i++) {
            doubledCoords[i][0] = offsetToDoubledX(dots[i]);
            doubledCoords[i][1] = offsetToDoubledY(dots[i]);
        }

        // Sort the doubled coordinates by x, then y
        Arrays.sort(doubledCoords, (a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));

        // Extract sorted coordinates for readability
        int d1x = doubledCoords[0][0];
        int d1y = doubledCoords[0][1];
        int d2x = doubledCoords[1][0];
        int d2y = doubledCoords[1][1];
        int d3x = doubledCoords[2][0];
        int d3y = doubledCoords[2][1];

        // manually check all the possible sausage shapes
        // numbering is only according to my reference

        // sausage type 1 ("–--")
        if (d1x == d2x - 2 && d1x == d3x - 4 && d2y == d1y && d3y == d1y) {
            return true;
        }
        // sausage type 2 ("|")
        if (d1x == d2x && d1x == d3x && d1y == d2y - 2 && d1y == d3y - 4) {
            return true;
        }

        // sausage type 3 ("J")
        //     case a
        if (d1x == d2x - 1 && d1x == d3x - 1 && d1y == d2y - 1 && d1y == d3y - 3) {
            return true;
        }
        //     case c
        if (d1x == d2x - 1 && d1x == d3x - 1 && d1y == d2y + 3 && d1y == d3y + 1) {
            return true;
        }

        //     case b
        if (d1x == d2x && d1x == d3x - 1 && d1y == d2y - 2 && d1y == d3y - 3) {
            return true;
        }

        //     case d
        if (d1x == d2x && d1x == d3x - 1 && d1y == d2y + 1 && d1y == d3y - 2) {
            return true;
        }

        //     case e, g
        if (d1x == d2x - 2 && d1x == d3x - 3) {
            return (d1y == d2y && d1y == d3y + 1) || (d1y == d2y && d1y == d3y - 1);
        }
        //     case f, h
        if (d1x == d2x - 1 && d1x == d3x - 3) {
            return (d1y == d2y - 1 && d1y == d3y - 1) || (d1y == d2y + 1 && d1y == d3y + 1);
        }

        // sausage type 4 ("/")
        //     case a
        if (d1x == d2x - 1 && d1x == d3x - 2 && d1y == d2y - 1 && d1y == d3y - 2) {
            return true;
        }
        //     case b
        if (d1x == d2x - 1 && d1x == d3x - 2 && d1y == d2y + 1 && d1y == d3y + 2) {
            return true;
        }

        //
        // sausage type 5 ("O")
        //     case a
        if (d1x == d2x && d1x == d3x - 1 && d1y == d2y - 2 && d1y == d3y - 1) {
            return true;
        }
        //     case b
        //     case c
        //     case d

        // sausage type 6 ("L")
        //     case a
        //     case b
        //     case c
        //     case d

        // note that this is not a complete implementation of all possible sausage shapes

        return false;
    }

    public static void main(String[] args) {
        Dot d1 = new Dot(0, 0);
        Dot d2 = new Dot(1, 0);
        Dot d3 = new Dot(2, 0);
        System.out.println(CoordUtils.isValidShape(d1, d2, d3));
        System.out.println(d1 + " " + d2 + " " + d3);
    }
}
