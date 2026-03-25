// Generated with the help of ChatGPT (June 2025)

package org.example.gui;

import com.badlogic.gdx.graphics.Color;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.engine.TurnManager;
import org.example.entities.Player;

@Getter
@Setter
public class GridCircle {
    private float x, y;
    private int row, col;
    private boolean isEnlarged = false;
    private Color color = Color.BLACK;
    private boolean isConnected = false;

    public GridCircle(float x, float y, int row, int col) {
        this.x = x;
        this.y = y;
        this.row = row;
        this.col = col;
    }

    public void setIsConnected(boolean isConnected, Player p) {
        this.isConnected = isConnected;
        color = p.getColor().cpy().mul(0.7f);
    }
}
