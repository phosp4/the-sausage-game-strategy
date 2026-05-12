/**
 * s pomocou AI:
 * https://aistudio.google.com/prompts/1YmDyL727WIRSJelYa588LYgH0uskKLql
 * https://gemini.google.com/app/f40d39ce2f8c2eec
 * chat radil pouzitie SplittableRandom kvoli rozlozeniu bitov
 * ale teavm vebova verzia to nebrala
 */

package org.example.utils;

import org.example.entities.GameBoard;
import org.example.entities.Point;
import org.example.entities.Sausage;

import java.util.Random;
//import java.util.SplittableRandom;

public class ZobristHasher {

    private static final int MAX_DIMENSION = 100;
    private static final long[][] ZOBRIST_TABLE = new long[MAX_DIMENSION][MAX_DIMENSION];

    // zbehne pri prvom pouziti triedy
    static {
        // je to pseudonahodne kvoli seedu, podobne nextLong, teda deterministicke
        Random random = new Random(812938192381237L);

        for (int y = 0; y < MAX_DIMENSION; y++) {
            for (int x = 0; x < MAX_DIMENSION; x++) {
                // vratane mensich (additional) bodov - to je velmi dolezite!!
                ZOBRIST_TABLE[y][x] = random.nextLong();
            }
        }
    }

    /**
     * volame pri pridani, ale aj pri odobrani klobasky
     */
    public static long updateHashForSausage(long currentHash, Sausage s) {
        long newHash = currentHash;
        for (Point p : s.getThreePoints()) {
            newHash ^= ZOBRIST_TABLE[p.getY()][p.getX()];
        }
        for (Point p : s.getTwoAdditionalPoints()) {
            newHash ^= ZOBRIST_TABLE[p.getY()][p.getX()];
        }
        return newHash;
    }

    public static long calculateInitialHash(GameBoard g) {
        int height = g.getGrid().length;
        int width = g.getGrid()[0].length;

        // zakodujeme tu rozmery, aby vsetky prazdne plochy neboli 0
        // druha cast or je formalita - kvoli znamienku alebo tak
        long hash = ((long) width << 32) | (height & 0xFFFFFFFFL);

        // keby nahodou nebola plocha prazdna
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (g.isOccupied(x, y)) {
                    hash ^= ZOBRIST_TABLE[y][x];
                }
            }
        }
        return hash;
    }

    public static int toJavaHashCode(long hash) {
        return Long.hashCode(hash);
    }

//    public static void main(String[] args) {
//        GameBoard g = new GameBoard(6,6);
//        g.addSausage(CliInputHandler.spracujRiadokVstupu("1,1 2,2 3,3"));
//
//        Map<Long, String> test = new HashMap<>();
//        test.put(g.getZobristHash(), "asdf");
//
//        System.out.println(g.getZobristHash());
//        System.out.println(test);
//        System.out.println(Long.hashCode(g.getZobristHash()));
//    }
}
