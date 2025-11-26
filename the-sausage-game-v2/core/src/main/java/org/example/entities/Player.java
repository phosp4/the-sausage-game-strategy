package org.example.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lombok.Data;

@Data
public class Player {
    private String name;
    private Character oneLetterNickname;
    private Color color;

    public Player(String name) {
        this.name = name;
        this.oneLetterNickname = name.charAt(0);
        this.color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);
    }

    public Player(String name, Color color) {
        this.name = name;
        this.oneLetterNickname = name.charAt(0);
        this.color = color;
    }

    public Player(String name, Character oneLetterNickname) {
        this.name = name;
        this.oneLetterNickname = oneLetterNickname;
        this.color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);
    }

    public Player(String name, Character oneLetterNickname, Color color) {
        this.name = name;
        this.oneLetterNickname = oneLetterNickname;
        this.color = color;
    }

    @Override
    public String toString() {
        return name;
    }
}
