package io.github.testing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
    private HoverCircle heldCircle = null; // Circle that starts a drag connection
    private List<Connection> connections = new ArrayList<>(); // Store finalized lines

    @Override
    public void create() {
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
        generateCircles(width, height);
    }

    private void generateCircles(int width, int height) {
        circles.clear();
        int columns = 5; // todo fsr stlpce rata len dlhe ale riadky vsetky
        int rows = 7;
        float spacingX = width / (columns + 1f);
        float spacingY = height / (rows + 1f);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns - (row % 2); col++) {
                float offsetX = (row % 2) * (spacingX / 2);
                float x = spacingX + col * spacingX + offsetX;
                float y = height - spacingY * (row + 1);
                circles.add(new HoverCircle(x, y));
            }
        }
    }

    @Override
    public void render() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean isTouched = Gdx.input.isTouched();

        ScreenUtils.clear(1, 1, 1, 1);

        batch.begin();

        HoverCircle hovered = null;
        for (HoverCircle circle : circles) {
            boolean isHovered = circle.update(mouseX, mouseY, isTouched);
            if (isHovered) hovered = circle;
            drawer.setColor(circle.color);
            drawer.filledCircle(circle.x, circle.y, circle.currentRadius);
        }

        // Draw all saved connections with rounded ends
        drawer.setColor(new Color(0xabababff));
        drawer.setDefaultLineWidth(30f);
        for (Connection conn : connections) {
            drawer.line(conn.a.x, conn.a.y, conn.b.x, conn.b.y);
            drawer.filledCircle(conn.a.x, conn.a.y, 15f);
            drawer.filledCircle(conn.b.x, conn.b.y, 15f);
        }

        // Start dragging
        if (isTouched && heldCircle == null && hovered != null) {
            heldCircle = hovered;
        }

        // Draw line during dragging to current hover or mouse position with rounded end
        if (isTouched && heldCircle != null) {
            drawer.setColor(new Color(0x4cc9f0ff));
            drawer.setDefaultLineWidth(30f);
            float endX = (hovered != null && heldCircle != hovered) ? hovered.x : mouseX;
            float endY = (hovered != null && heldCircle != hovered) ? hovered.y : mouseY;
            drawer.line(heldCircle.x, heldCircle.y, endX, endY);
            drawer.filledCircle(heldCircle.x, heldCircle.y, 15f);
            drawer.filledCircle(endX, endY, 15f);
        }

        // Finalize line on release if hover target valid
        if (!isTouched && heldCircle != null) {
            if (hovered != null && hovered != heldCircle) {
                connections.add(new Connection(heldCircle, hovered));
            }
            heldCircle = null;
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private static class HoverCircle {
        float x, y;
        float baseRadius = 15f;
        float enlargedRadius = 30f;
        float currentRadius = 15f;
        Color color = Color.BLACK;

        HoverCircle(float x, float y) {
            this.x = x;
            this.y = y;
        }

        boolean update(float mouseX, float mouseY, boolean isTouched) {
            boolean isHovered = Math.hypot(mouseX - x, mouseY - y) <= currentRadius;
            if (isHovered) {
                color = new Color(0x4cc9f0ff);
                currentRadius = isTouched ? enlargedRadius : baseRadius;
                return true;
            } else {
                color = Color.BLACK;
                currentRadius = baseRadius;
                return false;
            }
        }
    }

    // Represents a connection between two circles
    private static class Connection {
        HoverCircle a, b;

        Connection(HoverCircle a, HoverCircle b) {
            this.a = a;
            this.b = b;
        }
    }
}
