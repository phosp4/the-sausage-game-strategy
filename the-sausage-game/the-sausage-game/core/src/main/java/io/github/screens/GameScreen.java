// Portions of this code were generated using ChatGPT (June 2025)

    package io.github.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.ArrayList;
import java.util.List;

import io.github.MainGame;
import io.github.utils.MoveValidator;
import io.github.entities.Player;
import io.github.utils.SoundManager;
import io.github.utils.TurnManager;
import io.github.entities.GridCircle;
import io.github.entities.GridConnection;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * The GameScreen class represents the main game screen where players can play the Sausage Game.
 * It handles the game logic, rendering of circles and connections, and user interactions.
 * Ideally, this class could be split into two, one for the game logic and one for rendering.
 */
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
    private GridCircle currentlyHoveredCircle;
    private Texture background;
    private float mouseX;
    private float mouseY;
    private boolean isTouched;
    private Sound selectSound;
    private List<GridConnection> connections = new ArrayList<>();
    private int columns;
    private int rows;
    private float baseCircleRadius;
    private float enlargedCircleRadius;

    public GameScreen(MainGame game, int columns, int rows) {
        this.game = game;
        this.columns = columns;
        this.rows = rows;
    }

    @Override
    public void show() {
        if (!VisUI.isLoaded()) {
            VisUI.load(); // Load VisUI skin
        }

        float scale = Gdx.graphics.getDensity();
        baseCircleRadius = 12f * scale;
        enlargedCircleRadius = 24f * scale;

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
        generateCircles();

        // Initialize the turn manager
        Player player1 = new Player("Blue Player", new Color(0x2585F7FF)); // 0x4cc9f0ff 0x4895EFF0 0x4361eeff
        Player player2 = new Player("Red Player", new Color(0xF72585ff)); // 0xF72585ff
        turnManager = new TurnManager(player1, player2);

        // sounds
        selectSound = Gdx.audio.newSound(Gdx.files.internal("click4.ogg"));

        // rest of ui
        VisTable table = new VisTable();
        table.setFillParent(true);
        VisTextButton restartButton = new VisTextButton("Restart");
        VisTextButton quitButton = new VisTextButton("Quit");
        VisTextButton soundsButton = new VisTextButton("No sounds", "toggle");
        if (!SoundManager.isSoundEnabled()) {
            soundsButton.toggle();
        }

        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game, columns, rows));
                GameScreen.this.dispose();
            }
        });
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MenuScreen(game));
                GameScreen.this.dispose();
            }
        });
        soundsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // toggle sounds
                SoundManager.setSoundEnabled(!SoundManager.isSoundEnabled());
            }
        });

        table.add(restartButton).width(200 * scale).height(50 * scale).padBottom(20 * scale);
        table.add(quitButton).width(200 * scale).height(50 * scale).padBottom(20 * scale);
        table.add(soundsButton).width(200 * scale).height(50 * scale).padBottom(20 * scale);

//        table.add(restartButton).width(200).height(50).padBottom(20);
//        table.add(quitButton).width(200).height(50).padBottom(20);
//        table.add(soundsButton).width(200).height(50).padBottom(20);

        table.align(Align.bottom); // Align the table to the bottom of the screen
//        table.padBottom(0); // Add padding from the bottom edge
        stage.addActor(table);

    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
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

    private void generateCircles() {

        // Clear existing circles and connections
        circles.clear();

        // Generate circles in a hexagonal-grid-like pattern
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns - (row % 2); col++) {
                circles.add(new GridCircle(
                    colToX(col, row),
                    rowToY(col, row),
                    row, col, baseCircleRadius, enlargedCircleRadius));
            }
        }
    }

    // screen x and screen y
    private float sx(GridCircle c) {
        return colToX(c.getCol(), c.getRow());
    }
    private float sy(GridCircle c) {
        return rowToY(c.getCol(), c.getRow());
    }
    // generatePointPositionX
    // todo ak to je draha operacia, width height a scale netreba zistovat pre kazdy bod samostatne
    private float colToX(int col, int row) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float scale = Gdx.graphics.getDensity();

        int bottomPadding = (int)(80 * scale); // reserve space for bottom UI on different densities
        height = height - bottomPadding;
        float spacingX = (width) / (columns + 1f);
        float offsetX = (row % 2) * (spacingX / 2);

        return spacingX + col * spacingX + offsetX;
    }
    // generatePointPositionY
    private float rowToY(int col, int row) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float scale = Gdx.graphics.getDensity();

        int bottomPadding = (int)(80 * scale); // reserve space for bottom UI on different densities
        height = height - bottomPadding;
        float spacingY = height / (rows + 1f);

        return bottomPadding + height - spacingY * (row + 1);
    }

    /*
     * This method is called every frame to render the game.
     */
    @Override
    public void render(float delta) {
        // omitting this stops the flicker
//        ScreenUtils.clear(1, 1, 1, 1);

        if (!gameOver) {
            mouseX = Gdx.input.getX();
            mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            isTouched = Gdx.input.isTouched();
        }

        batch.begin();
        drawBackground();
        drawCircleHints();
        drawExistingCircles();
        drawExistingConnections();
        if (!gameOver) {
            if (isTouched) { handleTemporaryConnections(); }
            else {
                handleNewConnection();
                firstCircle = null;
                secondCircle = null;
                thirdCircle = null;
            }
        }
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void handleNewConnection() {
        if (firstCircle != null && secondCircle != null && thirdCircle != null) {
            boolean noIntersections =
                !MoveValidator.intersectsExistingConnection(
                    sx(firstCircle), sy(firstCircle),
                    sx(secondCircle), sy(secondCircle), connections) &&
                    !MoveValidator.intersectsExistingConnection(
                        sx(secondCircle), sy(secondCircle),
                        sx(thirdCircle), sy(thirdCircle), connections);

            if (noIntersections) {
                Player currentPlayer = turnManager.getCurrentPlayer();

                connections.add(new GridConnection(firstCircle, secondCircle, currentPlayer));
                connections.add(new GridConnection(secondCircle, thirdCircle, currentPlayer));

                long id = SoundManager.play(selectSound);
                SoundManager.setPitch(selectSound, id, 0.75f);

                firstCircle.setIsConnected(true);
                secondCircle.setIsConnected(true);
                thirdCircle.setIsConnected(true);

                // Swap turn
                turnManager.nextTurn();

//                if (!MoveValidator.playerHasValidMove(circles, connections)) { // todo kontrolovat grid
                if (false) {
                    gameOver = true;
                    String winnerName = turnManager.getNotCurrentPlayer().getName();
                    GameOverDialog dialog = new GameOverDialog(game, winnerName);
                    dialog.showOn(stage);
                    firstCircle = null;
                    secondCircle = null;
                    thirdCircle = null;
                }
            }
        }
    }

    private void handleTemporaryConnections() {
        if (firstCircle == null && currentlyHoveredCircle != null && !currentlyHoveredCircle.getIsConnected()) {
            firstCircle = currentlyHoveredCircle;
            SoundManager.play(selectSound);
        } else if (firstCircle != null && secondCircle == null && currentlyHoveredCircle != null
            && currentlyHoveredCircle != firstCircle && firstCircle.isNeighbor(currentlyHoveredCircle) && !currentlyHoveredCircle.getIsConnected()) {
            secondCircle = currentlyHoveredCircle;
            SoundManager.play(selectSound);
        } else if (firstCircle != null && secondCircle != null && currentlyHoveredCircle != null
            && currentlyHoveredCircle != firstCircle && currentlyHoveredCircle != secondCircle
            && secondCircle.isNeighbor(currentlyHoveredCircle) && !currentlyHoveredCircle.getIsConnected()) {
            if (thirdCircle != currentlyHoveredCircle) { // Play sound only when thirdCircle is newly assigned
                thirdCircle = currentlyHoveredCircle;
                SoundManager.play(selectSound);
            }
            thirdCircle = currentlyHoveredCircle;
        }

        // Draw connection-in-progress lines
        drawer.setColor(turnManager.getCurrentPlayer().getColor());
        drawer.setDefaultLineWidth(enlargedCircleRadius);

        if (firstCircle != null && secondCircle == null) {
            drawer.line(sx(firstCircle), sy(firstCircle), mouseX, mouseY);
            drawer.filledCircle(sx(firstCircle), sy(firstCircle), baseCircleRadius);
            drawer.filledCircle(mouseX, mouseY, baseCircleRadius);
        } else if (firstCircle != null && secondCircle != null && thirdCircle == null) {
            drawer.line(sx(firstCircle), sy(firstCircle), sx(secondCircle), sy(secondCircle));
            drawer.line(sx(secondCircle), sy(secondCircle), mouseX, mouseY);
            drawer.filledCircle(sx(firstCircle), sy(firstCircle), baseCircleRadius);
            drawer.filledCircle(sx(secondCircle), sy(secondCircle), baseCircleRadius);
            drawer.filledCircle(mouseX, mouseY, baseCircleRadius);
        } else if (firstCircle != null && secondCircle != null && thirdCircle != null) {
            drawer.line(sx(firstCircle), sy(firstCircle), sx(secondCircle), sy(secondCircle));
            drawer.line(sx(secondCircle), sy(secondCircle), sx(thirdCircle), sy(thirdCircle));
            drawer.filledCircle(sx(firstCircle), sy(firstCircle), baseCircleRadius);
            drawer.filledCircle(sx(secondCircle), sy(secondCircle), baseCircleRadius);
            drawer.filledCircle(sx(thirdCircle), sy(thirdCircle), baseCircleRadius);
        }

    }

    private void drawExistingCircles() {
        currentlyHoveredCircle = null; // Reset before checking hover
        for (GridCircle circle : circles) {
            boolean isHovered = circle.updateIfHovered(mouseX, mouseY, isTouched, turnManager);
            if (isHovered) {
                currentlyHoveredCircle = circle;
            }
            drawer.setColor(circle.getColor());

            // drawing all the circles
            drawer.filledCircle(
                sx(circle),
                sy(circle),
                circle.isEnlarged() ? circle.getEnlargedRadius() : circle.getBaseRadius());
        }
    }

    private void drawExistingConnections() {
        // Draw all connections
        drawer.setDefaultLineWidth(enlargedCircleRadius);
        for (GridConnection conn : connections) {
            drawer.setColor(conn.getOwner().getColor());
            drawer.line(sx(conn.getA()), sy(conn.getA()), sx(conn.getB()), sy(conn.getB()));
            drawer.filledCircle(sx(conn.getA()), sy(conn.getA()), baseCircleRadius);
            drawer.filledCircle(sx(conn.getB()), sy(conn.getB()), baseCircleRadius);
        }
    }

    private List<GridCircle> getValidMoves(GridCircle mainCircle) {
        List<GridCircle> validMoves = new ArrayList<>();

        for (GridCircle circle : circles) {
            if (circle != firstCircle && circle != secondCircle && !circle.getIsConnected() &&
                mainCircle.isNeighbor(circle) &&
                !MoveValidator.intersectsExistingConnection(
                    sx(mainCircle), sy(mainCircle),
                    sx(circle), sy(circle), connections)) {
                validMoves.add(circle);
            }
        }
        return validMoves;
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
                    !MoveValidator.intersectsExistingConnection(sx(anchor), sy(anchor), sx(circle), sy(circle), connections)) {
                    drawer.circle(sx(circle), sy(circle), circle.getBaseRadius() + 6f);
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
        selectSound.dispose();
    }
}
