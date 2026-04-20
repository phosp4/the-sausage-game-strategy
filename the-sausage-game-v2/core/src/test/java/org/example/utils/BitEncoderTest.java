package org.example.utils;

import org.example.entities.GameBoard;
import org.example.entities.Point;
import org.example.entities.Sausage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitEncoderTest {

    @Test
    void emptyGridToBitboardEquals0() {
        GameBoard g = new GameBoard(7, 7);
        assertEquals(0, BitEncoder.sausageGridToLongBitboard(g.getGrid()));
    }

    @Test
    void tooBigGridToLongBitboard() {
        GameBoard g = new GameBoard(8, 9);
        assertThrows(IllegalArgumentException.class, () -> {
            BitEncoder.sausageGridToLongBitboard(g.getGrid());
        });
    }

    @Test
    void diagonalSausageGridToBitboardAndBack() {
        GameBoard g = new GameBoard(3,3);
        Sausage s = new Sausage(new Point(0,0), new Point(1,1), new Point(2,2));
        g.addSausage(s);

        assertEquals(0b100_010_001L, BitEncoder.sausageGridToLongBitboard(g.getGrid()));
    }
}
