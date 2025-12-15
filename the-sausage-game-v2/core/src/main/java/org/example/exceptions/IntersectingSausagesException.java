package org.example.exceptions;

import org.example.entities.Point;

public class IntersectingSausagesException extends RuntimeException {

    public IntersectingSausagesException(Point a, Point b) {
        super("Sausage intersects at points: " + a + " and " + b);
    }
}
