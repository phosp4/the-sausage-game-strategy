package org.example.exceptions;

import org.example.entities.Point;

public class InvalidPointForGridException extends RuntimeException {
    public InvalidPointForGridException(Point p) {
        super("Point is invalid for grid: " + p);
    }
    public InvalidPointForGridException(Point p, String reason) {
        super("Point is invalid for grid: " + p + ", specifically: " + reason);
    }
}
