package io.github.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.game.GameBoard;

import io.github.MainGame;
import io.github.entities.Player;
import io.github.utils.SoundManager;
import io.github.utils.TurnManager;

/**
 * The GameScreen class represents the main game screen where players can play the Sausage Game.
 * It handles the game logic, rendering of circles and connections, and user interactions.
 * Ideally, this class could be split into two, one for the game logic and one for rendering.
 */
public class GameScreen implements Screen {
    private MainGame game;
    private Stage stage;
    private TurnManager turnManager;
    private Batch batch;
    private GameBoard board;
    private Texture background;
    private float mouseX;
    private float mouseY;
    private boolean isTouched;
    private Sound selectSound;
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

        batch = stage.getBatch();
        board = new GameBoard(columns, rows, batch, baseCircleRadius, enlargedCircleRadius);
        board.generateCircles(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 80);

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


    /*
     * This method is called every frame to render the game.
     */
    @Override
    public void render(float delta) {
        // Clear the screen with a white color
        ScreenUtils.clear(1, 1, 1, 1);

        // Get the current mouse position and touch state
        mouseX = Gdx.input.getX();
        mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        isTouched = Gdx.input.isTouched();

        // Every drawing should be done here
        batch.begin();
        drawBackground();
        board.render(mouseX, mouseY, isTouched, turnManager, selectSound);
        if (board.isGameOver()) {
            String winnerName = board.getWinner().getName();
            game.setScreen(new GameOverScreen(game, winnerName));
            GameScreen.this.dispose();
            batch.end();
            return;
        }
        batch.end();

        stage.act(delta);
        stage.draw();
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
