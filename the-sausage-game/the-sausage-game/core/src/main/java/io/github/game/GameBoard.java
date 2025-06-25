package io.github.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

import io.github.entities.GridCircle;
import io.github.entities.GridConnection;
import io.github.entities.Player;
import io.github.utils.MoveValidator;
import io.github.utils.SoundManager;
import io.github.utils.TurnManager;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * Encapsulates board state and rendering logic so {@link io.github.screens.GameScreen}
 * remains smaller.
 */
public class GameBoard {
    private final int columns;
    private final int rows;
    private final float baseCircleRadius;
    private final float enlargedCircleRadius;

    private final List<GridCircle> circles = new ArrayList<>();
    private final List<GridConnection> connections = new ArrayList<>();

    private GridCircle firstCircle;
    private GridCircle secondCircle;
    private GridCircle thirdCircle;
    private GridCircle currentlyHoveredCircle;

    private final ShapeDrawer drawer;

    private boolean gameOver;
    private Player winner;

    public GameBoard(int columns, int rows, Batch batch,
                     float baseCircleRadius, float enlargedCircleRadius) {
        this.columns = columns;
        this.rows = rows;
        this.baseCircleRadius = baseCircleRadius;
        this.enlargedCircleRadius = enlargedCircleRadius;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        Texture pixelTexture = new Texture(pixmap);
        pixelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        drawer = new ShapeDrawer(batch, new TextureRegion(pixelTexture));
    }

    public void generateCircles(int width, int height, int bottomPadding) {
        circles.clear();
        height -= bottomPadding;

        float spacingX = (width) / (columns + 1f);
        float spacingY = height / (rows + 1f);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns - (row % 2); col++) {
                float offsetX = (row % 2) * (spacingX / 2);
                float x = spacingX + col * spacingX + offsetX;
                float y = bottomPadding + height - spacingY * (row + 1);
                circles.add(new GridCircle(x, y, row, col, baseCircleRadius, enlargedCircleRadius));
            }
        }
    }

    public void render(float mouseX, float mouseY, boolean isTouched,
                       TurnManager turnManager, Sound selectSound) {
        drawCircleHints(turnManager);
        drawExistingCircles(mouseX, mouseY, isTouched, turnManager);
        drawExistingConnections();
        if (isTouched) {
            handleTemporaryConnections(mouseX, mouseY, turnManager, selectSound);
        } else {
            handleNewConnection(turnManager, selectSound);
            firstCircle = null;
            secondCircle = null;
            thirdCircle = null;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }

    // ==== private helpers ==== //

    private void handleNewConnection(TurnManager turnManager, Sound selectSound) {
        if (firstCircle != null && secondCircle != null && thirdCircle != null) {
            boolean noIntersections =
                !MoveValidator.intersectsExistingConnection(firstCircle.getX(), firstCircle.getY(),
                    secondCircle.getX(), secondCircle.getY(), connections) &&
                    !MoveValidator.intersectsExistingConnection(secondCircle.getX(), secondCircle.getY(),
                        thirdCircle.getX(), thirdCircle.getY(), connections);

            if (noIntersections) {
                Player currentPlayer = turnManager.getCurrentPlayer();

                connections.add(new GridConnection(firstCircle, secondCircle, currentPlayer));
                connections.add(new GridConnection(secondCircle, thirdCircle, currentPlayer));

                long id = SoundManager.play(selectSound);
                SoundManager.setPitch(selectSound, id, 0.75f);

                firstCircle.setIsConnected(true);
                secondCircle.setIsConnected(true);
                thirdCircle.setIsConnected(true);

                turnManager.nextTurn();

                if (!MoveValidator.playerHasValidMove(circles, connections)) {
                    gameOver = true;
                    winner = turnManager.getNotCurrentPlayer();
                }
            }
        }
    }

    private void handleTemporaryConnections(float mouseX, float mouseY,
                                            TurnManager turnManager, Sound selectSound) {
        if (firstCircle == null && currentlyHoveredCircle != null && !currentlyHoveredCircle.getIsConnected()) {
            firstCircle = currentlyHoveredCircle;
            SoundManager.play(selectSound);
        } else if (firstCircle != null && secondCircle == null && currentlyHoveredCircle != null
            && currentlyHoveredCircle != firstCircle && firstCircle.isNeighbor(currentlyHoveredCircle)
            && !currentlyHoveredCircle.getIsConnected()) {
            secondCircle = currentlyHoveredCircle;
            SoundManager.play(selectSound);
        } else if (firstCircle != null && secondCircle != null && currentlyHoveredCircle != null
            && currentlyHoveredCircle != firstCircle && currentlyHoveredCircle != secondCircle
            && secondCircle.isNeighbor(currentlyHoveredCircle) && !currentlyHoveredCircle.getIsConnected()) {
            if (thirdCircle != currentlyHoveredCircle) {
                thirdCircle = currentlyHoveredCircle;
                SoundManager.play(selectSound);
            }
            thirdCircle = currentlyHoveredCircle;
        }

        drawer.setColor(turnManager.getCurrentPlayer().getColor());
        drawer.setDefaultLineWidth(enlargedCircleRadius);

        if (firstCircle != null && secondCircle == null) {
            drawer.line(firstCircle.getX(), firstCircle.getY(), mouseX, mouseY);
            drawer.filledCircle(firstCircle.getX(), firstCircle.getY(), baseCircleRadius);
            drawer.filledCircle(mouseX, mouseY, baseCircleRadius);
        } else if (firstCircle != null && secondCircle != null && thirdCircle == null) {
            drawer.line(firstCircle.getX(), firstCircle.getY(), secondCircle.getX(), secondCircle.getY());
            drawer.line(secondCircle.getX(), secondCircle.getY(), mouseX, mouseY);
            drawer.filledCircle(firstCircle.getX(), firstCircle.getY(), baseCircleRadius);
            drawer.filledCircle(secondCircle.getX(), secondCircle.getY(), baseCircleRadius);
            drawer.filledCircle(mouseX, mouseY, baseCircleRadius);
        } else if (firstCircle != null && secondCircle != null && thirdCircle != null) {
            drawer.line(firstCircle.getX(), firstCircle.getY(), secondCircle.getX(), secondCircle.getY());
            drawer.line(secondCircle.getX(), secondCircle.getY(), thirdCircle.getX(), thirdCircle.getY());
            drawer.filledCircle(firstCircle.getX(), firstCircle.getY(), baseCircleRadius);
            drawer.filledCircle(secondCircle.getX(), secondCircle.getY(), baseCircleRadius);
            drawer.filledCircle(thirdCircle.getX(), thirdCircle.getY(), baseCircleRadius);
        }
    }

    private void drawExistingCircles(float mouseX, float mouseY, boolean isTouched, TurnManager turnManager) {
        currentlyHoveredCircle = null;
        for (GridCircle circle : circles) {
            boolean isHovered = circle.updateIfHovered(mouseX, mouseY, isTouched, turnManager);
            if (isHovered) {
                currentlyHoveredCircle = circle;
            }
            drawer.setColor(circle.getColor());
            drawer.filledCircle(circle.getX(), circle.getY(),
                circle.isEnlarged() ? circle.getEnlargedRadius() : circle.getBaseRadius());
        }
    }

    private void drawExistingConnections() {
        drawer.setDefaultLineWidth(enlargedCircleRadius);
        for (GridConnection conn : connections) {
            drawer.setColor(conn.getOwner().getColor());
            drawer.line(conn.getA().getX(), conn.getA().getY(), conn.getB().getX(), conn.getB().getY());
            drawer.filledCircle(conn.getA().getX(), conn.getA().getY(), baseCircleRadius);
            drawer.filledCircle(conn.getB().getX(), conn.getB().getY(), baseCircleRadius);
        }
    }

    private void drawCircleHints(TurnManager turnManager) {
        GridCircle anchor = null;
        if (firstCircle != null && secondCircle == null) {
            anchor = firstCircle;
        } else if (secondCircle != null && thirdCircle == null) {
            anchor = secondCircle;
        }
        if (anchor != null) {
            drawer.setColor(turnManager.getCurrentPlayer().getColor());
            drawer.setDefaultLineWidth(4f);
            for (GridCircle circle : circles) {
                if (circle != firstCircle && circle != secondCircle && !circle.getIsConnected() &&
                    anchor.isNeighbor(circle) &&
                    !MoveValidator.intersectsExistingConnection(anchor.getX(), anchor.getY(),
                        circle.getX(), circle.getY(), connections)) {
                    drawer.circle(circle.getX(), circle.getY(), circle.getBaseRadius() + 6f);
                }
            }
        }
    }
}
