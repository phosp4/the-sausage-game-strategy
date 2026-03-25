// Generated with the help of ChatGPT (June 2025)

package org.example.gui;

import com.badlogic.gdx.graphics.Color;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.engine.TurnManager;
import org.example.entities.Player;

@Data
public class GridCircle {
    private float x, y;
    private int row, col;
    private float baseRadius;
    private float enlargedRadius;
    private boolean isEnlarged = false;
    private Color color = Color.BLACK;
    private boolean isConnected = false;

    public GridCircle(float x, float y, int row, int col, float baseRadius, float enlargedRadius) {
        this.x = x;
        this.y = y;
        this.row = row;
        this.col = col;
        this.baseRadius = baseRadius;
        this.enlargedRadius = enlargedRadius;
    }

    /**
     * Updates the circle's hover state and radius based on mouse position and touch state.
     */
    public boolean updateIfHovered(float mouseX, float mouseY, boolean isTouched, TurnManager turnManager) {
        float currentRadius = isEnlarged ? enlargedRadius : baseRadius;
        boolean isHovered = Math.hypot(mouseX - x, mouseY - y) <= currentRadius;
        if (isHovered) {
            color = isConnected ? color : turnManager.getCurrentPlayer().getColor();
            isEnlarged = isTouched;
            return true;
        } else {
            color = isConnected ? color : Color.BLACK;
            isEnlarged = false;
            return false;
        }
    }

    // treba explicitne
    public boolean getIsConnected() {
        return isConnected;
    }
    public void setIsConnected(boolean isConnected, Player p) {
        this.isConnected = isConnected;
        color = p.getColor().cpy().mul(0.7f);
    }
}
