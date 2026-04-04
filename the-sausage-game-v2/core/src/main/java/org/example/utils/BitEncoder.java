package org.example.utils;

import com.badlogic.gdx.Game;
import org.example.entities.GameState;
import org.example.entities.Point;
import org.example.entities.Sausage;
import org.example.strategy.MoveGenerator;

import java.util.List;
import java.util.Set;

public class BitEncoder {

    public static long encodeSausage(Sausage s) {
        long out = 0L;
        int offset = 0;

        for (Point p : s.getThreePoints()) {
            out |= (long) p.getX() << offset;
            out |= (long) p.getY() << (offset + 10);
            offset += 20;
        }
        return out;
    }

    public static Sausage decodeSausage(long encodedSausage) {
//        int[] points = new int[6];
        Point[] points = new Point[3];
        long mask = 0b1111111111L;

        int offset = 0;
        for (int i = 0; i < 3; i++) {
            int x = (int) ((encodedSausage >> offset) & mask);
            int y = (int) ((encodedSausage >> (offset + 10)) & mask);

            points[i] = new Point(x, y);
            offset += 20;
        }

        return new Sausage(points[0], points[1], points[2]);
    }

    // testing
    public static void main(String[] args) {
        GameState g = new GameState(100, 100);
        Set<Sausage> moves = MoveGenerator.getAllPossibleMoves(g.getGrid());
        for (Sausage s : moves) {
            if (!s.equals(decodeSausage(encodeSausage(s)))) {
                System.out.println("problem with sausage " + s);
            } else {
                System.out.println(s + " encoded as " + encodeSausage(s) + " is fine");
            }
        }

//        Sausage s = new Sausage(new Point(0,0), new Point(1,1), new Point(2,2));
//        long a = encodeSausage(s);
//        Sausage s2 = decodeSausage(a);
//
//        System.out.println("Original: " + s);
//        System.out.println("Encoded: " + a);
//        System.out.println("Decoded: " + s2);
    }
}
