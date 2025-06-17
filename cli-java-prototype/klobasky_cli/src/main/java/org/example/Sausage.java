package org.example;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Sausage {
    private Set<Dot> threeDots = new HashSet<Dot>();
    private int player;

    public Sausage(int player, Dot dot1, Dot dot2, Dot dot3) {

        if (!isValidSausage(dot1, dot2, dot3)) {
            throw new IllegalArgumentException("The sausage shape is invalid");
        }

        if (dot1 == null || dot2 == null || dot3 == null) {
            throw new IllegalArgumentException("Dots cannot be null");
        }
        if (dot1.equals(dot2) || dot1.equals(dot3) || dot2.equals(dot3)) {
            throw new IllegalArgumentException("Dots must be unique");
        }
        if (player != GridConstants.PLAYER_ONE && player != GridConstants.PLAYER_TWO) {
            throw new IllegalArgumentException("Invalid player");
        }

        threeDots.add(dot1);
        threeDots.add(dot2);
        threeDots.add(dot3);
        this.player = player;
    }

    private boolean isValidSausage(Dot dot1, Dot dot2, Dot dot3) {
        // TODO: Implement the logic to check if the sausage shape is valid
        return true;
    }

    @Override
    public String toString() {
        return STR."{dots=\{threeDots}, player=\{player}}";
    }
}
