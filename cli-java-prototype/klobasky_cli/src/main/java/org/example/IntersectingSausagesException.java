package org.example;

public class IntersectingSausagesException extends RuntimeException {

    public IntersectingSausagesException() {
        super("Sausage intersects with existing sausages on the grid.");
    }
}
