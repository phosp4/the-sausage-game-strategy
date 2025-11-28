// Generated with the help of ChatGPT (June 2025)

package org.example.gui;

import com.badlogic.gdx.graphics.Color;
import org.example.engine.TurnManager;

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

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public float getBaseRadius() {
        return baseRadius;
    }
    public float getEnlargedRadius() {
        return enlargedRadius;
    }
    public boolean isEnlarged() {
        return isEnlarged;
    }
    public Color getColor() {
        return color;
    }
    public boolean getIsConnected() {
        return isConnected;
    }
    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * Updates the circle's hover state and radius based on mouse position and touch state.
     */
    public boolean updateIfHovered(float mouseX, float mouseY, boolean isTouched, TurnManager turnManager) {
        float currentRadius = isEnlarged ? enlargedRadius : baseRadius;
        boolean isHovered = Math.hypot(mouseX - x, mouseY - y) <= currentRadius;
        if (isHovered) {
            color = isConnected ? Color.GRAY : turnManager.getCurrentPlayer().getColor();
            isEnlarged = isTouched;
            return true;
        } else {
            color = isConnected ? Color.GRAY : Color.BLACK;
            isEnlarged = false;
            return false;
        }
    }
}
