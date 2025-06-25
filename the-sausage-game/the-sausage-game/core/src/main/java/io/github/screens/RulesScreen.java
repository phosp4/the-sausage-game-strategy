// Portions of this code were generated using ChatGPT (June 2025)

package io.github.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.MainGame;

public class RulesScreen implements Screen {
    private Stage stage;
    private MainGame app;
    private Texture background;

    public RulesScreen(MainGame app) {
        this.app = app;
    }

    @Override
    public void show() {
        if (!VisUI.isLoaded()) {
            VisUI.load(); // Load VisUI skin
        }

        float scale = Gdx.graphics.getDensity();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture(Gdx.files.internal("white-paper-texture.png"));

        VisTable rootTable = new VisTable();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Title
        VisLabel titleLabel = new VisLabel("Game Rules");
        titleLabel.setColor(Color.NAVY);
        titleLabel.setFontScale(2f * scale);
        rootTable.add(titleLabel).padTop(20 * scale).padBottom(30 * scale).row();

        // Rules text container with scrolling
        VisTable rulesTable = new VisTable();
        String rulesText =
            "The Sausage Game is a strategy game for two players, played on a triangular dotted grid." +
                "Players take turns marking exactly three unclaimed, connected dots to form a shape" +
                "called a \"sausage.\" The three dots can be in a straight line or form an angle, as long as" +
                "they're all directly connected.\n\nOnce a sausage is made, its dots can't be used again." +
                "Sausages can't overlap or share dots. The game ends when a player has no legal moves left –" +
                "that player loses.\n\nGood luck!";

        VisLabel rulesLabel = new VisLabel(rulesText);
        rulesLabel.setWrap(true); // Enable text wrapping
        rulesLabel.setColor(Color.BLACK);
        rulesTable.add(rulesLabel).growX().width(Gdx.graphics.getWidth() * 0.8f);

        VisScrollPane scrollPane = new VisScrollPane(rulesTable);
        scrollPane.setFadeScrollBars(false); // Always show scrollbars
        scrollPane.setSmoothScrolling(true);

        rootTable.add(scrollPane).grow().pad(20).row();

        // Back button
        VisTextButton backButton = new VisTextButton("Back to Menu");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(new MenuScreen(app));
                RulesScreen.this.dispose(); // ?? not sure
            }
        });
        rootTable.add(backButton).pad(20 * scale).width(250 * scale).height(60 * scale);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }
}
