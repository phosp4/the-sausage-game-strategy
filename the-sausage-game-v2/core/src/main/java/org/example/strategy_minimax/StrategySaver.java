package org.example.strategy_minimax;

import java.io.IOException;

public interface StrategySaver {

    public void put(long gameBoard, short localWinner);

    public default void flush() {};

    public default void close() {};
}
