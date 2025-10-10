package org.example.archive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Connection {
    private Dot a;
    private Dot b;

    public Connection(Dot a, Dot b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Dots cannot be null");
        }
        if (a.equals(b)) {
            throw new IllegalArgumentException("Dots must be unique");
        }
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}