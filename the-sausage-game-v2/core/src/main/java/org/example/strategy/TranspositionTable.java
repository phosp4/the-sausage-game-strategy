package org.example.strategy;

import java.util.Arrays;

public class TranspositionTable {

    private final long[] keys;
    private final byte[] values;
//    private final long[] moves;

    // konstanta o ktoru posuvame hash, aby sme vyuzili most significant bits, ktore su najviac premiesane
    private final int fibonnaciShift;

    private static final long FIBONACCI_MULTIPLIER = 0x9E3779B97F4A7C15L;
    private static final byte UNKNOWN_VALUE = Byte.MAX_VALUE;

    public TranspositionTable(int sizePowerOfTwo) {
        if (sizePowerOfTwo < 1 || sizePowerOfTwo > 30) {
            throw new IllegalArgumentException("Veľkosť musí byť medzi 1 a 30");
        }

        int capacity = 1 << sizePowerOfTwo; // teda 2^sizePowerOfTwo
        keys = new long[capacity];
        values = new byte[capacity];

        fibonnaciShift = 64 - sizePowerOfTwo;

        Arrays.fill(values, UNKNOWN_VALUE);
    }

    private int getIndex(long key) {
        return (int) ((key * FIBONACCI_MULTIPLIER) >>> fibonnaciShift);
    }

    public void put(long fullKey, int value) {
        int index = getIndex(fullKey);

        // always replace pristup
        keys[index] = fullKey;
        values[index] = (byte) value;
    }

    public boolean contains(long fullKey) {
        int index = getIndex(fullKey);
        return keys[index] == fullKey && values[index] != UNKNOWN_VALUE;
    }

    public int getValue(long fullKey) {
        int index = getIndex(fullKey);
        return values[index];
    }

    public void clear() {
        Arrays.fill(values, UNKNOWN_VALUE);
    }
}
