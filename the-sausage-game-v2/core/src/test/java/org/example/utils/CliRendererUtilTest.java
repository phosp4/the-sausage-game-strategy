package org.example.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CliRendererUtilTest {

    @Test
    void emptyBitboardIsRenderedAsEmptyGrid() {
        assertEquals("- - -\n- - -\n- - -\n", CliRendererUtil.bitboardToString(0L, 3, 3));
    }

    @Test
    void diagonalBitboardUsesRowMajorIndexing() {
        assertEquals("X - -\n- X -\n- - X\n", CliRendererUtil.bitboardToString(0b100_010_001L, 3, 3));
    }

    @Test
    void oversizedBoardIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CliRendererUtil.bitboardToString(0L, 8, 9));
    }
}
