package io.github.entities;

public class GridConnection {
    private GridCircle a, b;
    private Player owner;

    public GridConnection(GridCircle a, GridCircle b, Player owner) {
        this.a = a;
        this.b = b;
        this.owner = owner;
    }

    public GridCircle getA() {
        return a;
    }
    public GridCircle getB() {
        return b;
    }
    public Player getOwner() {
        return owner;
    }
    public void setA(GridCircle a) {
        this.a = a;
    }
    public void setB(GridCircle b) {
        this.b = b;
    }
    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
