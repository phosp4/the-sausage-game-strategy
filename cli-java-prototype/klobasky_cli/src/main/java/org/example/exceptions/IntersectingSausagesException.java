package org.example.exceptions;

public class IntersectingSausagesException extends RuntimeException {

    public IntersectingSausagesException() {
        super("Sausage intersects with existing sausages on the grid.");
    }
}
