/**
 * dolezite - aktualne funguje len pre neparne plochy
 * hladanie ukazkovych ploch do textovej casti:
 *      https://aistudio.google.com/u/2/prompts/16YLZYGmYvWSzYRTsoa4fcA4IK2OjNGNk
 * thread safety tu nie je nevyhnutna, ale bolo to s tym robene
 * pozor - pri zvacseni pola nad 64 bitov to tu treba upravit
 * pred tym tam s tym String bol memory leak - upravene
 */

package org.example.strategy_minimax;

import org.example.utils.ValidatorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SymmetryUtil {

    // Cache: Mapuje text "šírka,výška" (napr. "9,6") na predpočítané mapy
    private static final int[][][][] CACHE = new int[65][65][][];

    // Privatny konštruktor - nedá sa spraviť "new"
    private SymmetryUtil() {}

    private static int[][] getSymmetryMaps(int width, int height) {
        // Rýchly prístup bez synchronizácie v 99.999% prípadov
        int[][] maps = CACHE[width][height];

        if (maps == null) {
            // Lazy inicializácia bezpečná pre viac vlákien (Double-checked locking)
            synchronized (SymmetryUtil.class) {
                maps = CACHE[width][height];
                if (maps == null) {
                    maps = generateMapsForSize(width, height);
                    CACHE[width][height] = maps;
                }
            }
        }
        return maps;
    }

    /**
     * toto funguje len pre NxN
     */
    private static int[][] generateMapsForSize(int width, int height) {
        if (width % 2 == 0 || height % 2 == 0) {
            throw new IllegalArgumentException("Cannot generate maps for even dimensions!");
        }

        List<int[]> maps = new ArrayList<>();
        maps.add(generateMap(width, height, false, false, false)); // Identita
        maps.add(generateMap(width, height, true, false, false));  // Flip X
        maps.add(generateMap(width, height, false, true, false));  // Flip Y
        maps.add(generateMap(width, height, true, true, false));   // Flip X + Y

        if (width == height) {
            maps.add(generateMap(width, height, false, false, true)); // Transpose
            maps.add(generateMap(width, height, true, false, true));  // Rot 90
            maps.add(generateMap(width, height, false, true, true));  // Rot 270
            maps.add(generateMap(width, height, true, true, true));   // Anti-diag
        }
        return maps.toArray(new int[0][]);
    }

    /**
     * malo by to fungovat, ale netestoval som to, nechavam teda tak
     */
    private static int[][] generateMapsForSizeAll(int width, int height) {
        List<int[]> maps = new ArrayList<>();

        boolean wOdd = (width % 2 != 0);  // N (Nepárna šírka)
        boolean hOdd = (height % 2 != 0); // N (Nepárna výška)

        // 1. Identita - funguje vždy
        maps.add(generateMap(width, height, false, false, false));

        // 2. Flip X - funguje iba ak je šírka nepárna (NxN, NxP)
        // podla ai je to zrkadlenie podla osi Y
        if (wOdd) {
            maps.add(generateMap(width, height, true, false, false));
        }

        // 3. Flip Y - funguje iba ak je výška nepárna (NxN, PxN)
        // podla ai je to zrkadlenie podla osi X
        if (hOdd) {
            maps.add(generateMap(width, height, false, true, false));
        }

        // 4. Rotácia 180 (Flip X + Flip Y) - funguje VŽDY!
        // Pre NxN (zloženie dvoch platných flipov) aj pre PxP (dva neplatné flipy sa vyrušia)
        maps.add(generateMap(width, height, true, true, false));

        // 5. Štvorcové plochy (ak w == h)
        if (width == height) {
            // Transpozícia mení x a y. Bude fungovať VŽDY,
            // lebo (x + y) má rovnakú paritu ako (y + x)
            maps.add(generateMap(width, height, false, false, true)); // Transpozícia
            maps.add(generateMap(width, height, true, true, true));   // Anti-diagonála (Trans + 180 rot)

            // 90 a 270 stupňové rotácie fungujú LEN pre NxN (Nepárny štvorec)
            if (wOdd) {
                maps.add(generateMap(width, height, true, false, true));  // Rotácia 90
                maps.add(generateMap(width, height, false, true, true));  // Rotácia 270
            }
        }

        return maps.toArray(new int[0][]);
    }

    private static int[] generateMap(int w, int h, boolean flipX, boolean flipY, boolean transpose) {
        int[] map = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int oldIdx = y * w + x;
                int newX = flipX ? (w - 1 - x) : x;
                int newY = flipY ? (h - 1 - y) : y;

                map[oldIdx] = transpose ? (newX * h + newY) : (newY * w + newX);
            }
        }
        return map;
    }

    // STATICKÁ METÓDA PRE MINIMAX
    public static long canonize(long board, int width, int height) {
        if (board == 0L) return 0L;

        // Vytiahne z cache (alebo prvýkrát vygeneruje)
        int[][] symmetryMaps = getSymmetryMaps(width, height);

        long minBoard = board;

        for (int[] map : symmetryMaps) {
            long transformedBoard = 0L;
            long tempBoard = board;

            while (tempBoard != 0L) {
                int bitIndex = Long.numberOfTrailingZeros(tempBoard);
                transformedBoard |= (1L << map[bitIndex]);
                tempBoard &= (tempBoard - 1);
            }
            if (transformedBoard < minBoard) {
                minBoard = transformedBoard;
            }
        }
        return minBoard;
    }
}
