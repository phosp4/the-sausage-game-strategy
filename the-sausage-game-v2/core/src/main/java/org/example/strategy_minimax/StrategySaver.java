package org.example.strategy_minimax;

import java.io.IOException;

public interface StrategySaver {

    public void put(long gameBoard, long move);

    public default void flush() {};

    public default void close() {};
}
