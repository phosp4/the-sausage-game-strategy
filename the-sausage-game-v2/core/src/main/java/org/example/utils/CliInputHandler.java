package org.example.utils;

import org.example.entities.Point;
import org.example.entities.Sausage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CliInputHandler {

    Scanner scanner = new Scanner(System.in);

    public String getPlayerName() {
        System.out.println("Enter player name: ");
        return scanner.nextLine();
    }

    public Sausage nacitajSausage() {
        List<Point> points = new ArrayList<>();

        System.out.println("Enter sausage coordinates as x1,y1 x2,y2 x3,y3 (e.g., 0,0 0,1 0,2): ");
        String input = scanner.nextLine();

        String[] parts = input.split(" ");

        for (String part : parts) {
            String[] coords = part.split(",");
            if (coords.length != 2) {
                System.out.println("Invalid input format. Please try again.");
                return nacitajSausage();
            }
            try {
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                points.add(new Point(x, y));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
                return nacitajSausage();
            }
        }
        Sausage s = new Sausage(points.get(0), points.get(1), points.get(2));
        if (!ValidatorUtil.isSausageValid(s)) {
            return nacitajSausage();
        } else {
            return s;
        }
    }

    /**
     * pozadovany format je x1,y1 x2,y2 x3,y3
     */
    public static Sausage spracujRiadokVstupu(String input) {
        List<Point> points = new ArrayList<>();
        String[] parts = input.split(" ");

        for (String part : parts) {
            String[] coords = part.split(",");
            if (coords.length != 2) {
                System.out.println("Invalid input format. Please try again.");
                return null;
            }
            try {
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                points.add(new Point(x, y));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
                return null;
            }
        }
        Sausage s = new Sausage(points.get(0), points.get(1), points.get(2));
        if (!ValidatorUtil.isSausageValid(s)) {
            return null;
        } else {
            return s;
        }
    }
}
