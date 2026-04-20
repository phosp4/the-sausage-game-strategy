package org.example.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lombok.Data;
import org.example.automation.StrategyAgent;

@Data
public class Player {
    private String name;
    private Character oneLetterNickname;
    private Color color;

    public Player(String name, Character oneLetterNickname, Color color) {
        this.name = name;
        this.oneLetterNickname = oneLetterNickname;
        this.color = color;
    }

    public Player(String name) {
        this(name, name.charAt(0), getRandomColor());
    }

    public Player(String name, Color color) {
        this(name, name.charAt(0), color);
    }

    public Player(String name, Character oneLetterNickname) {
        this(name, oneLetterNickname, getRandomColor());
    }

    // Helper method to keep the constructors clean
    private static Color getRandomColor() {
//        return new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);
        Color c = new Color(0.03F, 0.878F, 0, 1f);
        return c;
    }

    @Override
    public String toString() {
        return name;
    }
}
