package org.example.archive;

public class CoordUtils {

    /*
    * offset coordinates are used for actual storage in memery
    * doubled coordinates are useful for shape calculations
    * see this for more details: https://www.redblobgames.com/grids/hexagons/#coordinates
     */

    // covert offset coordinates to doubled coordinates
    public static int offsetToDoubledX(int x, int y) {
        return 2 * x + y % 2;
    }
    public static int offsetToDoubledY(int x, int y) {
        return y; // no change needed for y
    }

    // convert doubled coordinates to offset coordinates
    public static int doubledToOffsetX(int x, int y) {
        return x / 2; // purposefully integer division
    }
    public static int doubledToOffsetY(int x, int y) {
        return y; // no change needed for y
    }

    public static boolean areNeighbors(Dot a, Dot b) {
        // Check if the dots are neighbors in doubled coordinates
        return Math.abs(a.getDoubledX() - b.getDoubledX()) <= 2 &&
                Math.abs(a.getDoubledY() - b.getDoubledY()) <= 2 &&
                (Math.abs(a.getDoubledX() - b.getDoubledX()) + Math.abs(a.getDoubledY() - b.getDoubledY())) <= 2;
    }

    public static void main(String[] args) {
        Dot d1 = new Dot(0, 0);
        Dot d2 = new Dot(1, 0);
        Dot d3 = new Dot(2, 0);
//        System.out.println(CoordUtils.isValidShape(d1, d2, d3));
        System.out.println(d1 + " " + d2 + " " + d3);
    }
}
