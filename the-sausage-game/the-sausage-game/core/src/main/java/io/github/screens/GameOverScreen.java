// todo fix flickering, its something with dispose...

package io.github.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.github.MainGame;

public class GameOverScreen implements Screen {
    private MainGame game;
    private String winnerName;
    private Stage stage;

    public GameOverScreen(MainGame game, String winnerName) {
        this.game = game;
        this.winnerName = winnerName;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        VisTable table = new VisTable();
        table.setFillParent(true);

        VisLabel gameOverLabel = new VisLabel("Game Over! Winner: " + winnerName);
        VisTextButton restartButton = new VisTextButton("Restart");
        VisTextButton menuButton = new VisTextButton("Menu");

        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game, 6, 6));
            }
        });

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MenuScreen(game));
            }
        });

        table.add(gameOverLabel).padBottom(20).row();
        table.add(restartButton).width(200).padBottom(10).row();
        table.add(menuButton).width(200);
        stage.addActor(table);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
    }
}

