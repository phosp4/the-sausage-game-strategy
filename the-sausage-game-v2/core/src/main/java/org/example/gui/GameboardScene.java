package org.example.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import org.example.GdxGame;
import org.example.engine.GameController;
import org.example.engine.TurnManager;
import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Point;
import org.example.entities.Sausage;
import org.example.gui_utils.MoveValidator;
import org.example.gui_utils.SoundManager;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;

public class GameboardScene implements Screen {

    // main stuff
    private final GdxGame game;
    private final GameController ctrl;

    // libgdx stuff
    private Stage stage;
    private Batch batch;
    private ShapeDrawer drawer;
    private ShapeRenderer shapes;

    // temporal ui data
    private GridCircle firstCircle = null;
    private GridCircle secondCircle = null;
    private GridCircle thirdCircle = null;
    private GridCircle currentlyHoveredCircle;
    private float mouseX;
    private float mouseY;
    private float baseCircleRadius;
    private float enlargedCircleRadius;
    private boolean isTouched;
//    private List<GridConnection> connections = new ArrayList<>(); // toto by tu byt nemuselo

    // libgdx assets
    private Texture background;
    private Sound selectSound;

    private int columns;
    private int rows;

    public GameboardScene(GdxGame gdxGame) {
        this.game = gdxGame;
        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        this.ctrl = new GameController(5, 5, p1, p2);
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
        generateCircles(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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

    private void generateCircles(int width, int height) {

        // Clear existing circles and connections
        circles.clear();

        float scale = Gdx.graphics.getDensity();
        int bottomPadding = (int)(80 * scale); // reserve space for bottom UI on different densities
        height = height - bottomPadding;

        // Calculate spacing based on the number of columns and rows
        float spacingX = (width) / (columns + 1f);
        float spacingY = height / (rows + 1f);

        // Generate circles in a hexagonal-grid-like pattern
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns - (row % 2); col++) {
                float offsetX = (row % 2) * (spacingX / 2);
                float x = spacingX + col * spacingX + offsetX;
                float y = bottomPadding + height - spacingY * (row + 1);
                circles.add(new GridCircle(x, y, row, col, baseCircleRadius, enlargedCircleRadius));
            }
        }
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
                !MoveValidator.intersectsExistingConnection(firstCircle.getX(), firstCircle.getY(), secondCircle.getX(), secondCircle.getY(), connections) &&
                    !MoveValidator.intersectsExistingConnection(secondCircle.getX(), secondCircle.getY(), thirdCircle.getX(), thirdCircle.getY(), connections);

            if (noIntersections) {
                Player currentPlayer = ctrl.getTurnManager().getCurrentPlayer();

                connections.add(new GridConnection(firstCircle, secondCircle, currentPlayer));
                connections.add(new GridConnection(secondCircle, thirdCircle, currentPlayer));
                ctrl.getGameBoard().addSausage();

                long id = SoundManager.play(selectSound);
                SoundManager.setPitch(selectSound, id, 0.75f);

                firstCircle.setIsConnected(true);
                secondCircle.setIsConnected(true);
                thirdCircle.setIsConnected(true);

                // Swap turn
                ctrl.getTurnManager().nextTurn();

                if (!MoveValidator.playerHasValidMove(circles, connections)) {
//                    gameOver = true;
                    String winnerName = ctrl.getTurnManager().getNotCurrentPlayer().getName();
//                    GameOverDialog dialog = new GameOverDialog(game, winnerName);
//                    dialog.showOn(stage);
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
        drawer.setColor(ctrl.getTurnManager().getCurrentPlayer().getColor());
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

    private void drawExistingCircles() {
        currentlyHoveredCircle = null; // Reset before checking hover
        for (GridCircle circle : circles) {
            boolean isHovered = circle.updateIfHovered(mouseX, mouseY, isTouched, ctrl.getTurnManager());
            if (isHovered) {
                currentlyHoveredCircle = circle;
            }
            drawer.setColor(circle.getColor());

            // drawing all the circles
            drawer.filledCircle(circle.getX(), circle.getY(),
                circle.isEnlarged() ? circle.getEnlargedRadius() : circle.getBaseRadius());
        }
    }

    private void drawExistingConnections() {
        // Draw all connections
        drawer.setDefaultLineWidth(enlargedCircleRadius);
        for (GridConnection conn : connections) {
            drawer.setColor(conn.getOwner().getColor());
            drawer.line(conn.getA().getX(), conn.getA().getY(), conn.getB().getX(), conn.getB().getY());
            drawer.filledCircle(conn.getA().getX(), conn.getA().getY(), baseCircleRadius);
            drawer.filledCircle(conn.getB().getX(), conn.getB().getY(), baseCircleRadius);
        }
    }

    private List<GridCircle> getValidMoves(GridCircle mainCircle) {
        List<GridCircle> validMoves = new ArrayList<>();

        for (GridCircle circle : circles) {
            if (circle != firstCircle && circle != secondCircle && !circle.getIsConnected() &&
                mainCircle.isNeighbor(circle) &&
                !MoveValidator.intersectsExistingConnection(mainCircle.getX(), mainCircle.getY(), circle.getX(), circle.getY(), connections)) {
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
        selectSound.dispose();
    }
}
