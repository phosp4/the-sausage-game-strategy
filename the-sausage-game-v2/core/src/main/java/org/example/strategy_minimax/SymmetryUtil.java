package org.example.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SymmetryUtil {

    // Cache: Mapuje text "šírka,výška" (napr. "9,6") na predpočítané mapy
    private static final ConcurrentHashMap<String, int[][]> CACHE = new ConcurrentHashMap<>();

    // Privatny konštruktor - nedá sa spraviť "new"
    private SymmetryUtil() {}

    private static int[][] getSymmetryMaps(int width, int height) {
        String key = width + "," + height;

        // Ak už pre túto veľkosť mapy máme, rovno ich vrátime (bleskové)
        return CACHE.computeIfAbsent(key, k -> generateMapsForSize(width, height));
    }

    private static int[][] generateMapsForSize(int width, int height) {
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
