package io.github.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import io.github.MainGame;
import io.github.utils.MoveValidator;
import io.github.entities.Player;
import io.github.utils.TurnManager;
import io.github.entities.GridCircle;
import io.github.entities.GridConnection;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameScreen implements Screen {
    private MainGame game;
    private Stage stage;
    private TurnManager turnManager;
    private boolean gameOver = false;
    private Batch batch;
    private ShapeDrawer drawer;
    private List<GridCircle> circles;
    private GridCircle firstCircle = null;
    private GridCircle secondCircle = null;
    private GridCircle thirdCircle = null;
    GridCircle currentlyHoveredCircle;
    private Texture background;
    float mouseX;
    float mouseY;
    boolean isTouched;

    private List<GridConnection> connections = new ArrayList<>();

    private int columns;
    private int rows;

    public GameScreen(MainGame game, int columns, int rows) {
        this.game = game;
        this.columns = columns;
        this.rows = rows;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture(Gdx.files.internal("white-paper-texture.png"));

        // Create a 1x1 pixel texture to use as a pixel for drawing
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        Texture pixelTexture = new Texture(pixmap);
        pixelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        batch = stage.getBatch();
        drawer = new ShapeDrawer(batch, new TextureRegion(pixelTexture));

        circles = new ArrayList<>();
        generateCircles(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialize the turn manager
        Player player1 = new Player("Player 1", new Color(0x2585F7FF)); // 0x4cc9f0ff 0x4895EFF0 0x4361eeff
        Player player2 = new Player("Player 2", new Color(0xF72585ff)); // 0xF72585ff
        turnManager = new TurnManager(player1, player2);

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

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
                circles.add(new GridCircle(x, y, row, col));
            }
        }
    }

    /*
     * This method is called every frame to render the game.
     */
    @Override
    public void render(float delta) {
//        if (gameOver) {
//            // todo show game over screen
//            batch.begin();
//            // Optional: gray overlay or something fancy
//            drawer.setColor(Color.BLACK);
//            drawer.getBatch().end(); // switch to GDX font rendering if needed
//
//            batch.begin();
//            // Show game over message
//            // (You may use a BitmapFont for text; here’s a placeholder comment)
//            // font.draw(batch, gameOverMessage, 100, 100);
//            batch.end();
//            return;
//        }

        // Clear the screen with a white color
        ScreenUtils.clear(1, 1, 1, 1);

        // Get the current mouse position and touch state
        mouseX = Gdx.input.getX();
        mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        isTouched = Gdx.input.isTouched();

        // Every drawing should be done here
        batch.begin();

        drawBackground();
        drawCircleHints();
        drawExistingCircles();
        drawExistingConnections();

        if (isTouched) { handleTemporaryConnections(); }
        else {
            handleNewConnection();
            firstCircle = null;
            secondCircle = null;
            thirdCircle = null;
        }

        batch.end();
    }

    private void handleNewConnection() {
        if (firstCircle != null && secondCircle != null && thirdCircle != null) {
            boolean noIntersections =
                !MoveValidator.intersectsExistingConnection(firstCircle.getX(), firstCircle.getY(), secondCircle.getX(), secondCircle.getY(), connections) &&
                    !MoveValidator.intersectsExistingConnection(secondCircle.getX(), secondCircle.getY(), thirdCircle.getX(), thirdCircle.getY(), connections);

            if (noIntersections) {
                Player currentPlayer = turnManager.getCurrentPlayer();

                connections.add(new GridConnection(firstCircle, secondCircle, currentPlayer));
                connections.add(new GridConnection(secondCircle, thirdCircle, currentPlayer));

                firstCircle.setIsConnected(true);
                secondCircle.setIsConnected(true);
                thirdCircle.setIsConnected(true);

                // Swap turn
                turnManager.nextTurn();

                if (!MoveValidator.playerHasValidMove(circles, connections)) {
                    gameOver = true;
                    System.out.println("Game Over! " + turnManager.getNotCurrentPlayer().getName() + " wins!");
                }

            }
        }
    }

    private void handleTemporaryConnections() {
        if (firstCircle == null && currentlyHoveredCircle != null && !currentlyHoveredCircle.getIsConnected()) {
            firstCircle = currentlyHoveredCircle;
        } else if (firstCircle != null && secondCircle == null && currentlyHoveredCircle != null
            && currentlyHoveredCircle != firstCircle && firstCircle.isNeighbor(currentlyHoveredCircle) && !currentlyHoveredCircle.getIsConnected()) {
            secondCircle = currentlyHoveredCircle;
        } else if (firstCircle != null && secondCircle != null && currentlyHoveredCircle != null
            && currentlyHoveredCircle != firstCircle && currentlyHoveredCircle != secondCircle
            && secondCircle.isNeighbor(currentlyHoveredCircle) && !currentlyHoveredCircle.getIsConnected()) {
            thirdCircle = currentlyHoveredCircle;
        }

        // Draw connection-in-progress lines
        drawer.setColor(turnManager.getCurrentPlayer().getColor());
        drawer.setDefaultLineWidth(30f);

        if (firstCircle != null && secondCircle == null) {
            drawer.line(firstCircle.getX(), firstCircle.getY(), mouseX, mouseY);
            drawer.filledCircle(firstCircle.getX(), firstCircle.getY(), 15f);
            drawer.filledCircle(mouseX, mouseY, 15f);
        } else if (firstCircle != null && secondCircle != null && thirdCircle == null) {
            drawer.line(firstCircle.getX(), firstCircle.getY(), secondCircle.getX(), secondCircle.getY());
            drawer.line(secondCircle.getX(), secondCircle.getY(), mouseX, mouseY);
            drawer.filledCircle(firstCircle.getX(), firstCircle.getY(), 15f);
            drawer.filledCircle(secondCircle.getX(), secondCircle.getY(), 15f);
            drawer.filledCircle(mouseX, mouseY, 15f);
        } else if (firstCircle != null && secondCircle != null && thirdCircle != null) {
            drawer.line(firstCircle.getX(), firstCircle.getY(), secondCircle.getX(), secondCircle.getY());
            drawer.line(secondCircle.getX(), secondCircle.getY(), thirdCircle.getX(), thirdCircle.getY());
            drawer.filledCircle(firstCircle.getX(), firstCircle.getY(), 15f);
            drawer.filledCircle(secondCircle.getX(), secondCircle.getY(), 15f);
            drawer.filledCircle(thirdCircle.getX(), thirdCircle.getY(), 15f);
        }

    }

    private void drawExistingCircles() {
        for (GridCircle circle : circles) {
            boolean isHovered = circle.updateIfHovered(mouseX, mouseY, isTouched, turnManager);
            if (isHovered) currentlyHoveredCircle = circle;
            drawer.setColor(circle.getColor());

            // drawing all the circles
            drawer.filledCircle(circle.getX(), circle.getY(), circle.getCurrentRadius());
        }
    }

    private void drawExistingConnections() {
        // Draw all connections
        drawer.setDefaultLineWidth(30f);
        for (GridConnection conn : connections) {
            drawer.setColor(conn.getOwner().getColor());
            drawer.line(conn.getA().getX(), conn.getA().getY(), conn.getB().getX(), conn.getB().getY());
            drawer.filledCircle(conn.getA().getX(), conn.getA().getY(), 15f);
            drawer.filledCircle(conn.getB().getX(), conn.getB().getY(), 15f);
        }
    }

    private void drawCircleHints() {
        // draw the highlight around circles
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
                    !MoveValidator.intersectsExistingConnection(anchor.getX(), anchor.getY(), circle.getX(), circle.getY(), connections)) {
                    drawer.circle(circle.getX(), circle.getY(), circle.getBaseRadius() + 6f);
                }
            }
        }
    }

    private void drawBackground() {
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
