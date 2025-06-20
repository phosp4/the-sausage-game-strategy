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
    private HoverCircle firstCircle = null;
    private HoverCircle secondCircle = null;
    private HoverCircle thirdCircle = null;

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

        // draw the highlighting animated circles in progress
        HoverCircle anchor = null;
        if (firstCircle != null && secondCircle == null) {
            anchor = firstCircle;
        } else if (secondCircle != null && thirdCircle == null) {
            anchor = secondCircle;
        }
        if (anchor != null) {
            drawer.setColor(new Color(0xF72585ff));
            drawer.setDefaultLineWidth(4f);
            for (HoverCircle circle : circles) {
                if (circle != firstCircle && circle != secondCircle && !circle.connected &&
                    anchor.isNeighbor(circle) &&
                    !intersectsExistingConnection(anchor.x, anchor.y, circle.x, circle.y)) {

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

        if (isTouched) {
            if (firstCircle == null && hovered != null && !hovered.connected) {
                firstCircle = hovered;
            } else if (firstCircle != null && secondCircle == null && hovered != null
                && hovered != firstCircle && firstCircle.isNeighbor(hovered) && !hovered.connected) {
                secondCircle = hovered;
            } else if (firstCircle != null && secondCircle != null && hovered != null
                && hovered != firstCircle && hovered != secondCircle
                && secondCircle.isNeighbor(hovered) && !hovered.connected) {
                thirdCircle = hovered;
            }

            // Draw connection-in-progress lines
            drawer.setColor(new Color(0x4cc9f0ff));
            drawer.setDefaultLineWidth(30f);
            if (firstCircle != null && secondCircle == null) {
                drawer.line(firstCircle.x, firstCircle.y, mouseX, mouseY);
                drawer.filledCircle(firstCircle.x, firstCircle.y, 15f);
                drawer.filledCircle(mouseX, mouseY, 15f);
            } else if (firstCircle != null && secondCircle != null && thirdCircle == null) {
                drawer.line(firstCircle.x, firstCircle.y, secondCircle.x, secondCircle.y);
                drawer.line(secondCircle.x, secondCircle.y, mouseX, mouseY);
                drawer.filledCircle(firstCircle.x, firstCircle.y, 15f);
                drawer.filledCircle(secondCircle.x, secondCircle.y, 15f);
                drawer.filledCircle(mouseX, mouseY, 15f);
            } else if (firstCircle != null && secondCircle != null && thirdCircle != null) {
                drawer.line(firstCircle.x, firstCircle.y, secondCircle.x, secondCircle.y);
                drawer.line(secondCircle.x, secondCircle.y, thirdCircle.x, thirdCircle.y);
                drawer.filledCircle(firstCircle.x, firstCircle.y, 15f);
                drawer.filledCircle(secondCircle.x, secondCircle.y, 15f);
                drawer.filledCircle(thirdCircle.x, thirdCircle.y, 15f);
            }

        } else {
            if (firstCircle != null && secondCircle != null && thirdCircle != null) {
                boolean noIntersections =
                    !intersectsExistingConnection(firstCircle.x, firstCircle.y, secondCircle.x, secondCircle.y) &&
                        !intersectsExistingConnection(secondCircle.x, secondCircle.y, thirdCircle.x, thirdCircle.y);

                if (noIntersections) {
                    connections.add(new Connection(firstCircle, secondCircle));
                    connections.add(new Connection(secondCircle, thirdCircle));
                    firstCircle.connected = true;
                    secondCircle.connected = true;
                    thirdCircle.connected = true;
                }
            }

            // Reset selection on mouse release
            firstCircle = null;
            secondCircle = null;
            thirdCircle = null;
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

    // utility methods - ai generated

    private boolean intersectsExistingConnection(float x1, float y1, float x2, float y2) {
        for (Connection conn : connections) {
            float x3 = conn.a.x, y3 = conn.a.y;
            float x4 = conn.b.x, y4 = conn.b.y;

            // Allow shared endpoints but not if geometrically crossing
            if (isSharedEndpoint(x1, y1, x2, y2, x3, y3, x4, y4)) {
                continue;
            }

            if (segmentsIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
                return true;
            }
        }
        return false;
    }

    // True if segments intersect excluding touching at shared endpoints
    private boolean segmentsIntersect(float x1, float y1, float x2, float y2,
                                      float x3, float y3, float x4, float y4) {
        int o1 = orientation(x1, y1, x2, y2, x3, y3);
        int o2 = orientation(x1, y1, x2, y2, x4, y4);
        int o3 = orientation(x3, y3, x4, y4, x1, y1);
        int o4 = orientation(x3, y3, x4, y4, x2, y2);

        if (o1 != o2 && o3 != o4) return true;

        return (o1 == 0 && onSegment(x1, y1, x3, y3, x2, y2)) ||
            (o2 == 0 && onSegment(x1, y1, x4, y4, x2, y2)) ||
            (o3 == 0 && onSegment(x3, y3, x1, y1, x4, y4)) ||
            (o4 == 0 && onSegment(x3, y3, x2, y2, x4, y4));
    }

    private int orientation(float x1, float y1, float x2, float y2, float x3, float y3) {
        float val = (y2 - y1) * (x3 - x2) - (x2 - x1) * (y3 - y2);
        if (Math.abs(val) < 1e-6) return 0; // colinear
        return (val > 0) ? 1 : 2; // clockwise or counterclockwise
    }

    private boolean onSegment(float x1, float y1, float x2, float y2, float x3, float y3) {
        return x2 <= Math.max(x1, x3) && x2 >= Math.min(x1, x3) &&
            y2 <= Math.max(y1, y3) && y2 >= Math.min(y1, y3);
    }


    // Allow touching only at ends, not crossing mid-segment
    private boolean isOnlyTouchingAtEndpoint(float x1, float y1, float x2, float y2,
                                             float x3, float y3, float x4, float y4) {
        return (equals(x1, y1, x3, y3) || equals(x1, y1, x4, y4) ||
            equals(x2, y2, x3, y3) || equals(x2, y2, x4, y4));
    }

    private boolean isSharedEndpoint(float x1, float y1, float x2, float y2,
                                     float x3, float y3, float x4, float y4) {
        return equals(x1, y1, x3, y3) || equals(x1, y1, x4, y4) ||
            equals(x2, y2, x3, y3) || equals(x2, y2, x4, y4);
    }

    private boolean equals(float x1, float y1, float x2, float y2) {
        float epsilon = 0.01f;
        return Math.abs(x1 - x2) < epsilon && Math.abs(y1 - y2) < epsilon;
    }


}
