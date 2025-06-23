package io.github;

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

public class RulesScreen implements Screen {
    private Stage stage;
    private KlobaskyMain app;
    private Texture background;

    public RulesScreen(KlobaskyMain app) {
        this.app = app;
    }

    @Override
    public void show() {
        if (!VisUI.isLoaded()) {
            VisUI.load(); // Load VisUI skin
        }

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture(Gdx.files.internal("white-paper-texture.png"));

        VisTable rootTable = new VisTable();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Title
        VisLabel titleLabel = new VisLabel("Game Rules");
        titleLabel.setColor(Color.NAVY);
        titleLabel.setFontScale(2f);
        rootTable.add(titleLabel).padTop(20).padBottom(30).row();

        // Rules text container with scrolling
        VisTable rulesTable = new VisTable();
        String rulesText =
            "The Sausage Game is a two-player strategy game played on a triangular dotted grid. " +

                "Players take turns identifying and marking a group of exactly three unclaimed, adjacent dots that form a valid shape, called \"sausage\". " +
                "It may be a straight line or an angled configuration, provided all three dots are directly connected.\n\n" +
                "Once a sausage is claimed, its dots may no longer be used in future moves." +
                "Sausages must not overlap or share dots with previously claimed sausages. " +
                "The game continues until a player is unable to make a legal move on their turn. " +
                "That player loses the game.\n\n" +

                "Good luck!";

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
        rootTable.add(backButton).pad(20).width(250).height(60);
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
