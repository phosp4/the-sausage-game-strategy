package org.example.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class SausageWithSwitch extends Sausage {

    @Getter @Setter
    private boolean active = false;

    public SausageWithSwitch(Player player, Point p1, Point p2, Point p3) {
        super(player, p1, p2, p3);
    }

    public SausageWithSwitch(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }
}
