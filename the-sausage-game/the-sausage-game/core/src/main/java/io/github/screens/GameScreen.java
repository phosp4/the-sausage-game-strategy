package io.github.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.MainGame;
import io.github.sausagegame.backend.ConnectionView;
import io.github.sausagegame.backend.GameConfig;
import io.github.sausagegame.backend.MoveResult;
import io.github.sausagegame.backend.MoveStatus;
import io.github.sausagegame.backend.NodeView;
import io.github.sausagegame.backend.Player;
import io.github.sausagegame.backend.SausageGame;
import io.github.utils.SoundManager;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * Main play screen that renders the board and translates user input into backend moves.
 */
public class GameScreen implements Screen {
    private final MainGame game;
    private final int columns;
    private final int rows;

    private Stage stage;
    private Batch batch;
    private ShapeDrawer drawer;
    private Texture background;
    private Sound selectSound;

    private SausageGame sausageGame;
    private Map<Integer, CircleVisual> circles;
    private final List<Integer> currentSelection = new ArrayList<>(3);
    private CircleVisual hoveredCircle;
    private boolean gameOverDialogShown = false;

    private float baseCircleRadius;
    private float enlargedCircleRadius;
    private float bottomPadding;

    private Map<Player, Color> playerColors;

    public GameScreen(MainGame game, int columns, int rows) {
        this.game = game;
        this.columns = columns;
        this.rows = rows;
    }

    @Override
    public void show() {
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        float scale = Gdx.graphics.getDensity();
        baseCircleRadius = 12f * scale;
        enlargedCircleRadius = 24f * scale;
        bottomPadding = 80f * scale;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture(Gdx.files.internal("white-paper-texture.png"));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        Texture pixelTexture = new Texture(pixmap);
        pixelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        batch = stage.getBatch();
        drawer = new ShapeDrawer(batch, new TextureRegion(pixelTexture));

        selectSound = Gdx.audio.newSound(Gdx.files.internal("click4.ogg"));

        initialiseGame();
        buildUi(scale);
    }

    private void initialiseGame() {
        List<Player> players = List.of(
                new Player("blue", "Blue Player"),
                new Player("red", "Red Player")
        );
        sausageGame = new SausageGame(new GameConfig(columns, rows), players);
        playerColors = new LinkedHashMap<>();
        playerColors.put(players.get(0), new Color(0x2585F7FF));
        playerColors.put(players.get(1), new Color(0xF72585FF));
        circles = new HashMap<>();
        refreshCircleStates();
        updateCircleLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void buildUi(float scale) {
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
                dispose();
                game.setScreen(new GameScreen(game, columns, rows));
            }
        });
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new MenuScreen(game));
            }
        });
        soundsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.setSoundEnabled(!SoundManager.isSoundEnabled());
            }
        });

        table.add(restartButton).width(200 * scale).height(50 * scale).padBottom(20 * scale);
        table.add(quitButton).width(200 * scale).height(50 * scale).padBottom(20 * scale);
        table.add(soundsButton).width(200 * scale).height(50 * scale).padBottom(20 * scale);
        table.align(Align.bottom);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        refreshCircleStates();
        updateHoveredCircle();
        handleInput();

        batch.begin();
        drawBackground();
        drawCircleHints();
        drawExistingConnections();
        drawExistingCircles();
        batch.end();

        stage.act(delta);
        stage.draw();

        if (sausageGame.isGameOver() && !gameOverDialogShown && sausageGame.winner() != null) {
            gameOverDialogShown = true;
            GameOverDialog dialog = new GameOverDialog(game, sausageGame.winner().displayName());
            dialog.showOn(stage);
        }
    }

    private void handleInput() {
        if (sausageGame.isGameOver()) {
            clearSelection();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            clearSelection();
            return;
        }

        if (Gdx.input.justTouched() && hoveredCircle != null && !hoveredCircle.isConnected()) {
            Set<Integer> allowed = sausageGame.validContinuations(currentSelection);
            if (!currentSelection.isEmpty() && allowed.isEmpty()) {
                return;
            }
            if (!currentSelection.isEmpty() && currentSelection.contains(hoveredCircle.getId())) {
                return;
            }
            if (!allowed.isEmpty() && !allowed.contains(hoveredCircle.getId())) {
                return;
            }

            currentSelection.add(hoveredCircle.getId());
            hoveredCircle.setSelected(true);
            SoundManager.play(selectSound);

            if (currentSelection.size() == 3) {
                applyMove();
            }
        }
    }

    private void applyMove() {
        MoveResult result = sausageGame.playMove(List.copyOf(currentSelection));
        if (result.status() == MoveStatus.INVALID) {
            Gdx.app.log("GameScreen", "Invalid move: " + result.message());
        } else {
            long id = SoundManager.play(selectSound);
            SoundManager.setPitch(selectSound, id, 0.75f);
        }
        refreshCircleStates();
        clearSelection();
    }

    private void refreshCircleStates() {
        List<NodeView> nodes = sausageGame.getNodeViews();
        for (NodeView node : nodes) {
            CircleVisual circle = circles.computeIfAbsent(node.id(), id -> new CircleVisual(node.id(),
                    node.x(), node.y(), baseCircleRadius, enlargedCircleRadius));
            circle.update(node, playerColors);
        }
    }

    private void updateCircleLayout(int width, int height) {
        for (CircleVisual circle : circles.values()) {
            circle.updateLayout(width, height, bottomPadding);
        }
    }

    private void updateHoveredCircle() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        hoveredCircle = null;
        for (CircleVisual circle : circles.values()) {
            boolean hovered = circle.contains(mouseX, mouseY);
            circle.setHovered(hovered && !circle.isConnected());
            if (hovered) {
                hoveredCircle = circle;
            }
        }
    }

    private void drawExistingCircles() {
        Player currentPlayer = sausageGame.currentPlayer();
        for (CircleVisual circle : circles.values()) {
            drawer.setColor(circle.color(currentPlayer, playerColors));
            drawer.filledCircle(circle.getX(), circle.getY(), circle.currentRadius());
        }
    }

    private void drawExistingConnections() {
        drawer.setDefaultLineWidth(enlargedCircleRadius);
        for (ConnectionView connection : sausageGame.getConnections()) {
            CircleVisual from = circles.get(connection.fromNodeId());
            CircleVisual to = circles.get(connection.toNodeId());
            Color color = playerColors.getOrDefault(connection.owner(), Color.DARK_GRAY);
            drawer.setColor(color);
            drawer.line(from.getX(), from.getY(), to.getX(), to.getY());
            drawer.filledCircle(from.getX(), from.getY(), baseCircleRadius);
            drawer.filledCircle(to.getX(), to.getY(), baseCircleRadius);
        }
    }

    private void drawCircleHints() {
        if (currentSelection.isEmpty()) {
            return;
        }
        Set<Integer> allowed = sausageGame.validContinuations(currentSelection);
        if (allowed.isEmpty()) {
            return;
        }
        drawer.setDefaultLineWidth(4f);
        drawer.setColor(playerColors.getOrDefault(sausageGame.currentPlayer(), Color.BLACK));
        for (Integer id : allowed) {
            CircleVisual circle = circles.get(id);
            if (circle != null && !circle.isConnected()) {
                drawer.circle(circle.getX(), circle.getY(), circle.getBaseRadius() + 6f);
            }
        }
    }

    private void drawBackground() {
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void clearSelection() {
        for (Integer id : currentSelection) {
            CircleVisual circle = circles.get(id);
            if (circle != null) {
                circle.setSelected(false);
            }
        }
        currentSelection.clear();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
        updateCircleLayout(width, height);
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

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (background != null) {
            background.dispose();
        }
        if (selectSound != null) {
            selectSound.dispose();
        }
    }

    private static final class CircleVisual {
        private final int id;
        private final float normalizedX;
        private final float normalizedY;
        private final float baseRadius;
        private final float enlargedRadius;
        private float x;
        private float y;
        private boolean connected;
        private boolean selected;
        private boolean hovered;
        private Player occupant;

        CircleVisual(int id, float normalizedX, float normalizedY, float baseRadius, float enlargedRadius) {
            this.id = id;
            this.normalizedX = normalizedX;
            this.normalizedY = normalizedY;
            this.baseRadius = baseRadius;
            this.enlargedRadius = enlargedRadius;
        }

        int getId() {
            return id;
        }

        float getX() {
            return x;
        }

        float getY() {
            return y;
        }

        float getBaseRadius() {
            return baseRadius;
        }

        boolean isConnected() {
            return connected;
        }

        void setSelected(boolean selected) {
            this.selected = selected;
        }

        void setHovered(boolean hovered) {
            this.hovered = hovered;
        }

        void update(NodeView node, Map<Player, Color> colors) {
            this.connected = node.occupied();
            this.occupant = node.occupant();
        }

        void updateLayout(int width, int height, float bottomPadding) {
            float usableHeight = height - bottomPadding;
            x = normalizedX * width;
            y = bottomPadding + normalizedY * usableHeight;
        }

        boolean contains(float px, float py) {
            float radius = currentRadius();
            float dx = px - x;
            float dy = py - y;
            return dx * dx + dy * dy <= radius * radius;
        }

        float currentRadius() {
            if (selected || hovered) {
                return enlargedRadius;
            }
            return baseRadius;
        }

        Color color(Player currentPlayer, Map<Player, Color> palette) {
            if (connected && occupant != null) {
                return palette.getOrDefault(occupant, Color.GRAY);
            }
            if (selected || hovered) {
                return palette.getOrDefault(currentPlayer, Color.NAVY);
            }
            return Color.BLACK;
        }
    }
}
