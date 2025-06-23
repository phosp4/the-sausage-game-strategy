package io.github.utils;

import java.util.List;

import io.github.entities.GridCircle;
import io.github.entities.GridConnection;

public class MoveValidator {

    // not the most efficient probably, works for now
    public static boolean playerHasValidMove(List<GridCircle> circles, List<GridConnection> connections) {
        for (GridCircle a : circles) {
            if (a.getIsConnected()) continue;
            for (GridCircle b : circles) {
                if (b.getIsConnected() || b == a || !a.isNeighbor(b)) continue;
                if (intersectsExistingConnection(a.getX(), a.getY(), b.getX(), b.getY(), connections)) continue;

                for (GridCircle c : circles) {
                    if (c == a || c == b || c.getIsConnected()) continue;
                    if (!b.isNeighbor(c)) continue;
                    if (intersectsExistingConnection(b.getX(), b.getY(), c.getX(), c.getY(), connections)) continue;
                    if (intersectsExistingConnection(a.getX(), a.getY(), c.getX(), c.getY(), connections)) continue; // optional

                    // Found a valid path A→B→C
                    return true;
                }
            }
        }
        return false;
    }
    // utility methods - ai generated

    public static boolean intersectsExistingConnection(float x1, float y1, float x2, float y2,
                                                List<GridConnection> connections) {
        for (GridConnection conn : connections) {
            float x3 = conn.getA().getX(), y3 = conn.getA().getY();
            float x4 = conn.getB().getX(), y4 = conn.getB().getY();

            // Allow shared endpoints but not if geometrically crossing
            if (isSharedEndpoint(x1, y1, x2, y2, x3, y3, x4, y4)) {
                continue;
            }

            if (segmentsIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
                return true;
            }
        }
        return false;
    }

    // True if segments intersect excluding touching at shared endpoints
    private static boolean segmentsIntersect(float x1, float y1, float x2, float y2,
                                      float x3, float y3, float x4, float y4) {
        int o1 = orientation(x1, y1, x2, y2, x3, y3);
        int o2 = orientation(x1, y1, x2, y2, x4, y4);
        int o3 = orientation(x3, y3, x4, y4, x1, y1);
        int o4 = orientation(x3, y3, x4, y4, x2, y2);

        if (o1 != o2 && o3 != o4) return true;

        return (o1 == 0 && onSegment(x1, y1, x3, y3, x2, y2)) ||
            (o2 == 0 && onSegment(x1, y1, x4, y4, x2, y2)) ||
            (o3 == 0 && onSegment(x3, y3, x1, y1, x4, y4)) ||
            (o4 == 0 && onSegment(x3, y3, x2, y2, x4, y4));
    }

    private static int orientation(float x1, float y1, float x2, float y2, float x3, float y3) {
        float val = (y2 - y1) * (x3 - x2) - (x2 - x1) * (y3 - y2);
        if (Math.abs(val) < 1e-6) return 0; // colinear
        return (val > 0) ? 1 : 2; // clockwise or counterclockwise
    }

    private static boolean onSegment(float x1, float y1, float x2, float y2, float x3, float y3) {
        return x2 <= Math.max(x1, x3) && x2 >= Math.min(x1, x3) &&
            y2 <= Math.max(y1, y3) && y2 >= Math.min(y1, y3);
    }


    // Allow touching only at ends, not crossing mid-segment
    private static boolean isOnlyTouchingAtEndpoint(float x1, float y1, float x2, float y2,
                                             float x3, float y3, float x4, float y4) {
        return (equals(x1, y1, x3, y3) || equals(x1, y1, x4, y4) ||
            equals(x2, y2, x3, y3) || equals(x2, y2, x4, y4));
    }

    private static boolean isSharedEndpoint(float x1, float y1, float x2, float y2,
                                     float x3, float y3, float x4, float y4) {
        return equals(x1, y1, x3, y3) || equals(x1, y1, x4, y4) ||
            equals(x2, y2, x3, y3) || equals(x2, y2, x4, y4);
    }

    private static boolean equals(float x1, float y1, float x2, float y2) {
        float epsilon = 0.01f;
        return Math.abs(x1 - x2) < epsilon && Math.abs(y1 - y2) < epsilon;
    }
}
