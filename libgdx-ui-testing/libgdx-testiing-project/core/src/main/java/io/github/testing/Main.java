package io.github.testing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeDrawer drawer;
    private List<HoverCircle> circles;
    private HoverCircle heldCircle = null;
    private List<Connection> connections = new ArrayList<>();

    private int columns = 5;
    private int rows = 7;

    @Override
    public void create() {

        // Create a 1x1 pixel texture to use as a pixel for drawing
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        Texture pixelTexture = new Texture(pixmap);
        pixelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        batch = new SpriteBatch();
        drawer = new ShapeDrawer(batch, new TextureRegion(pixelTexture));

        circles = new ArrayList<>();
        generateCircles(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
    }

    private void generateCircles(int width, int height) {

        // Clear existing circles and connections
        circles.clear();

        // Calculate spacing based on the number of columns and rows
        float spacingX = width / (columns + 1f);
        float spacingY = height / (rows + 1f);

        // Generate circles in a hexagonal-grid-like pattern
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns - (row % 2); col++) {
                float offsetX = (row % 2) * (spacingX / 2);
                float x = spacingX + col * spacingX + offsetX;
                float y = height - spacingY * (row + 1);
                circles.add(new HoverCircle(x, y, row, col));
            }
        }
    }

    /*
        * This method is called every frame to render the game.
     */
    @Override
    public void render() {
        // Clear the screen with a white color
        ScreenUtils.clear(1, 1, 1, 1);

        // Get the current mouse position and touch state
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean isTouched = Gdx.input.isTouched();

        // Every drawing should be done here
        batch.begin();

        // get the current time and convert it to seconds
        float time = (float) (System.currentTimeMillis() % 10000L) / 1000f;
        float angleOffset = -time * 2f; // rotation speed and direction

        // draw the highlighting animated circles
        if (heldCircle != null) {
            drawer.setColor(new Color(0xF72585ff));
            drawer.setDefaultLineWidth(4f);
            for (HoverCircle circle : circles) {
                if (circle != heldCircle && heldCircle.isNeighbor(circle) && !circle.connected) {
                    int dashCount = 16;
                    float radius = circle.baseRadius + 6f;
                    for (int i = 0; i < dashCount; i += 2) {
                        float angle1 = (float)(2 * Math.PI * i / dashCount) + angleOffset;
                        float angle2 = (float)(2 * Math.PI * (i + 1) / dashCount) + angleOffset;
                        float x1 = circle.x + (float)Math.cos(angle1) * radius;
                        float y1 = circle.y + (float)Math.sin(angle1) * radius;
                        float x2 = circle.x + (float)Math.cos(angle2) * radius;
                        float y2 = circle.y + (float)Math.sin(angle2) * radius;
                        drawer.line(x1, y1, x2, y2);
                    }
                }
            }
        }

        HoverCircle hovered = null;
        for (HoverCircle circle : circles) {
            boolean isHovered = circle.update(mouseX, mouseY, isTouched);
            if (isHovered) hovered = circle;
            drawer.setColor(circle.color);

            // drawing all the circles
            drawer.filledCircle(circle.x, circle.y, circle.currentRadius);
        }

        drawer.setColor(new Color(0xabababff));
        drawer.setDefaultLineWidth(30f);
        for (Connection conn : connections) {
            drawer.line(conn.a.x, conn.a.y, conn.b.x, conn.b.y);
            drawer.filledCircle(conn.a.x, conn.a.y, 15f);
            drawer.filledCircle(conn.b.x, conn.b.y, 15f);
        }

        if (isTouched && heldCircle == null && hovered != null && !hovered.connected) {
            heldCircle = hovered;
        }

        if (isTouched && heldCircle != null) {
            drawer.setColor(new Color(0x4cc9f0ff));
            drawer.setDefaultLineWidth(30f);
            float endX = (hovered != null && heldCircle != hovered) ? hovered.x : mouseX;
            float endY = (hovered != null && heldCircle != hovered) ? hovered.y : mouseY;
            drawer.line(heldCircle.x, heldCircle.y, endX, endY);
            drawer.filledCircle(heldCircle.x, heldCircle.y, 15f);
            drawer.filledCircle(endX, endY, 15f);
        }

        if (!isTouched && heldCircle != null) {
            if (hovered != null && hovered != heldCircle && heldCircle.isNeighbor(hovered)
                && !heldCircle.connected && !hovered.connected) {
                connections.add(new Connection(heldCircle, hovered));
                heldCircle.connected = true;
                hovered.connected = true;
            }
            heldCircle = null;
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private class HoverCircle {
        float x, y;
        int row, col;
        float baseRadius = 15f;
        float enlargedRadius = 30f;
        float currentRadius = 15f;
        Color color = Color.BLACK;
        boolean connected = false;

        HoverCircle(float x, float y, int row, int col) {
            this.x = x;
            this.y = y;
            this.row = row;
            this.col = col;
        }

        /**
         * Updates the circle's hover state and radius based on mouse position and touch state.
         */
        boolean update(float mouseX, float mouseY, boolean isTouched) {
            boolean isHovered = Math.hypot(mouseX - x, mouseY - y) <= currentRadius;
            if (isHovered) {
                color = connected ? Color.GRAY : new Color(0x4cc9f0ff);
                currentRadius = isTouched ? enlargedRadius : baseRadius;
                return true;
            } else {
                color = connected ? Color.GRAY : Color.BLACK;
                currentRadius = baseRadius;
                return false;
            }
        }

        boolean isNeighbor(HoverCircle other) {

            // coords of the 8 neighbors - in hex grid they vary based on row parity
            int[][] offsetsEven = {
                {-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 0},
                {-2, 0}, {2, 0}
            };
            int[][] offsetsOdd = {
                {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 0}, {1, 1},
                {-2, 0}, {2, 0}
            };

            // Determine which set of offsets to use based on the row parity
            int[][] offsets = (row % 2 == 0) ? offsetsEven : offsetsOdd;

            // Check if the other circle is a neighbor
            for (int[] offset : offsets) {
                int nr = row + offset[0];
                int nc = col + offset[1];
                if (other.row == nr && other.col == nc) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class Connection {
        HoverCircle a, b;

        Connection(HoverCircle a, HoverCircle b) {
            this.a = a;
            this.b = b;
        }
    }
}
