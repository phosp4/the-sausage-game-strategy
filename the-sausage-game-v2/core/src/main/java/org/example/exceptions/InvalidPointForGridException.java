package org.example.exceptions;

import org.example.entities.Point;

public class InvalidPointForGridException extends RuntimeException {
    public InvalidPointForGridException(Point p) {
        super("Point is invalid for grid: " + p);
    }
}
