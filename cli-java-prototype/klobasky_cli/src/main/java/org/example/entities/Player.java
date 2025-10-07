package org.example.entities;

import lombok.Data;

@Data
public class Player {
    private String name;
    private Character oneLetterNickname;

    public Player(String name) {
        this.name = name;
        this.oneLetterNickname = name.charAt(0);
    }

    public Player(String name, Character oneLetterNickname) {
        this.name = name;
        this.oneLetterNickname = oneLetterNickname;
    }

    @Override
    public String toString() {
        return "Player{name='" + name + "'}";
    }
}