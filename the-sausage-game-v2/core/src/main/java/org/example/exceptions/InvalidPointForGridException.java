package org.example.exceptions;

public class InvalidPointForGridException extends RuntimeException {
    public InvalidPointForGridException() {
        super("Point is outside the grid boundaries.");
    }
}
