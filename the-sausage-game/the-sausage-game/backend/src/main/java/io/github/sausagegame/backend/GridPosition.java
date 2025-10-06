package io.github.sausagegame.backend;

import java.util.Objects;

final class GridPosition {
    private final int row;
    private final int column;

    GridPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    int row() {
        return row;
    }

    int column() {
        return column;
    }

    GridPosition translate(int dRow, int dCol) {
        return new GridPosition(row + dRow, column + dCol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridPosition that)) return false;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
