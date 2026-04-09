package org.example.utils;

import org.example.entities.GameBoard;
import org.example.entities.Point;
import org.example.entities.Sausage;
import org.example.strategy.MoveGenerator;

import java.util.Set;

public class BitEncoder {

    //grid na long
    //sausage na long
    //tryAddingSausage(gameBoard, move)
    //removeSausage(gameBoard, move);

    public static long sausageGridToLongBitboard(Sausage[][] grid) {
        long bitboard = 0L;
        int height = grid.length;
        int width = grid[0].length;

        if (width * height > 64) {
            throw new IllegalArgumentException("Hracia plocha je pre jeden long príliš veľká - >64 políčok");
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] != null) {
                    int idx = y * width + x;

                    bitboard |= (1L << idx);
                }
            }
        }

        return bitboard;
    }

    public static long sausageObjectToLongBitboard(Sausage s, Sausage[][] grid) {
        long moveBitboard = 0L;
        int height = grid.length;
        int width = grid[0].length;

        for (Point p : s.getThreePoints()) {
            int idx = p.getY() * width + p.getX();
            moveBitboard |= (1L << idx);
        }
        for (Point p : s.getTwoAdditionalPoints()) {
            int idx = p.getY() * width + p.getX();
            moveBitboard |= (1L << idx);
        }

        return moveBitboard;
    }

    public static boolean validateSausageForGrid(long board, long sausage) {
        return (board & sausage) == 0L; // teda nic sa neprekryva
    }

    public static long addSausage(long board, long sausage) {
        return board | sausage;
    }

    public static long removeSausage(long board, long sausage) {
        return board & ~sausage; // ~ je negacia
    }

    ///

    public static long encodeSausageWithOffsets(Sausage s) {
        long out = 0L;
        int offset = 0;

        for (Point p : s.getThreePoints()) {
            out |= (long) p.getX() << offset;
            out |= (long) p.getY() << (offset + 10);
            offset += 20;
        }
        return out;
    }

    public static Sausage decodeSausageWithOffsets(long encodedSausage) {
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
        GameBoard g = new GameBoard(100, 100);
        Set<Sausage> moves = MoveGenerator.getPossibleMoves(g.getGrid());
        for (Sausage s : moves) {
            if (!s.equals(decodeSausageWithOffsets(encodeSausageWithOffsets(s)))) {
                System.out.println("problem with sausage " + s);
            } else {
                System.out.println(s + " encoded as " + encodeSausageWithOffsets(s) + " is fine");
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
