package io.github;

public class GridConnection {
    GridCircle a, b;
    Player owner;

    GridConnection(GridCircle a, GridCircle b, Player owner) {
        this.a = a;
        this.b = b;
        this.owner = owner;
    }
}
