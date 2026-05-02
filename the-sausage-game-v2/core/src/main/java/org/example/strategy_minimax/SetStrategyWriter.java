package org.example.strategy_minimax;

import lombok.Getter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SetStrategyWriter implements StrategyWriter {

    private long[] bufferKeys = new long[10_000_000];
    private int bufferIndex = 0;

    @Getter private Set<Long> fullStrategy = new HashSet<>();

    @Override
    public void put(long gameBoard) {
        bufferKeys[bufferIndex] = gameBoard;
        bufferIndex++;

        if (bufferKeys.length == bufferIndex) {
            flush();
        }
    }

    @Override
    public void flush() {
        for (int i = 0; i < bufferIndex; i++) {
            fullStrategy.add(bufferKeys[i]);
        }
        bufferIndex = 0;
    }

    @Override
    public void close() {
        flush();
    }
}
