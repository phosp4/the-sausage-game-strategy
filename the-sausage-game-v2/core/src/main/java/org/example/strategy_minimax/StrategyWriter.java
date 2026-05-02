package org.example.strategy_minimax;

public interface StrategyWriter {

    public void put(long gameBoard);

    public void flush();

    public void close();
}
