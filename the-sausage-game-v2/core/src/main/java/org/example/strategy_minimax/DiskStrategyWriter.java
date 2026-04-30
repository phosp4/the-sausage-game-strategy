package org.example.strategy_minimax;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiskStrategyWriter {

    private long[] bufferKeys = new long[10_000_000];
    private long[] bufferValues = new long[10_000_000];
    private int bufferIndex = 0;
    private DataOutputStream dos;

    public DiskStrategyWriter(String filename) {
        // BufferedOutputStream zrýchľuje zápis na disk
        try {
            dos = new DataOutputStream(new java.io.BufferedOutputStream(new FileOutputStream(filename)));
        } catch (FileNotFoundException e) {
            System.err.println("Chyba pri zápise na disk: " + e.getMessage());
            throw new RuntimeException(e); // najlepsie zmenit
        }
    }

    public void put(long gameBoard, long move) {
        bufferKeys[bufferIndex] = gameBoard;
        bufferValues[bufferIndex] = move;
        bufferIndex++;

        // Ak je buffer plný, zapíš na disk (flush)
        if (bufferIndex == bufferKeys.length) {
            try {
                flush();
            } catch (IOException e) {
                System.err.println("Chyba pri zápise na disk: " + e.getMessage());
                throw new RuntimeException(e); // najlepsie zmenit
            }
        }
    }

    public void flush() throws IOException {
        for (int i = 0; i < bufferIndex; i++) {
            dos.writeLong(bufferKeys[i]);
            dos.writeLong(bufferValues[i]);
        }
        bufferIndex = 0;
    }

    public void close() {
        try {
            flush();       // Zápis zvyšku z buffra (aj keby tam bol len 1 záznam)
            dos.close();   // Bezpečné uzatvorenie súboru
        } catch (IOException e) {
            System.err.println("Chyba pri zápise na disk: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
