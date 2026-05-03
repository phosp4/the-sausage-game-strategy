package org.example.strategy_minimax;

import java.util.ArrayList;
import java.util.List;

public class SymmetryUtil {
    private final int[][] symmetryMaps;
    private final int bitsCount;

    public SymmetryUtil(int width, int height) {
        this.bitsCount = width * height;
        List<int[]> maps = new ArrayList<>();

        // 1. Identita (pôvodná plocha) - teoreticky nie je nutná v mape, ale uľahčí cyklus
        maps.add(generateMap(width, height, false, false, false));
        // 2. Horizontálne zrkadlenie (Flip X)
        maps.add(generateMap(width, height, true, false, false));
        // 3. Vertikálne zrkadlenie (Flip Y)
        maps.add(generateMap(width, height, false, true, false));
        // 4. Rotácia o 180 stupňov (Flip X + Flip Y)
        maps.add(generateMap(width, height, true, true, false));

        // Ak je to štvorec, môžeme pridať aj diagonálne symetrie (Rotácia o 90, 270, transpozícia...)
        if (width == height) {
            maps.add(generateMap(width, height, false, false, true)); // Transpozícia
            maps.add(generateMap(width, height, true, false, true));  // Rotácia 90
            maps.add(generateMap(width, height, false, true, true));  // Rotácia 270
            maps.add(generateMap(width, height, true, true, true));   // Anti-diagonála
        }

        symmetryMaps = maps.toArray(new int[0][]);
    }

    private int[] generateMap(int w, int h, boolean flipX, boolean flipY, boolean transpose) {
        int[] map = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int oldIdx = y * w + x;

                int newX = flipX ? (w - 1 - x) : x;
                int newY = flipY ? (h - 1 - y) : y;

                int newIdx;
                if (transpose) {
                    newIdx = newX * h + newY; // x a y si vymenia miesta
                } else {
                    newIdx = newY * w + newX;
                }
                map[oldIdx] = newIdx;
            }
        }
        return map;
    }

    /**
     * Zoberie bitboard a vráti jeho "najmenšiu" symetrickú podobu = kanonický tvar
     */
    public long canonize(long board) {
        if (board == 0L) return 0L;

        long minBoard = board;

        // Prejdeme všetky predvytvorené "ťaháky" symetrií
        for (int[] map : symmetryMaps) {
            long transformedBoard = 0L;
            long tempBoard = board; // Kópia plochy pre iteráciu

            // Tento cyklus preskakuje nuly a zastaví sa len na bitoch, kde je 1
            while (tempBoard != 0L) {
                // Zistí pozíciu najpravejšej jednotky (napr. bit č. 14)
                int bitIndex = Long.numberOfTrailingZeros(tempBoard);

                // Pozrie do ťaháku, kam sa má tento bit presunúť, a zapíše ho
                transformedBoard |= (1L << map[bitIndex]);

                // Vymaže túto jednotku a ide na ďalšiu
                tempBoard &= (tempBoard - 1);
            }

            // Ak je táto otočená plocha matematicky menšie číslo, zapamätáme si ju
            if (transformedBoard < minBoard) {
                minBoard = transformedBoard;
            }
        }
        return minBoard;
    }
}
