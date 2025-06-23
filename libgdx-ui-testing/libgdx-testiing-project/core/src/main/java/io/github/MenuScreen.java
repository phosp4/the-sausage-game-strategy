package io.github;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuScreen implements Screen {
    private Stage stage;
    private KlobaskyMain app;
    private Texture background;
    private VisSelectBox<String> gridSelectBox;
    private int[][] gridDimensions = {{4, 5}, {5, 7}, {7, 7}, {9, 9}};
    private String[] gridOptions = {"Small", "Classic", "Large", "Very Large"};

    public MenuScreen(KlobaskyMain app) {
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

        VisTable table = new VisTable();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        VisLabel titleLabel = new VisLabel("The Sausage Game!");
//        titleLabel.setColor(new Color(0x3A0CA3ff));
        titleLabel.setColor(Color.NAVY);
        titleLabel.setFontScale(2f);

        // Grid size selector
        VisTable gridTable = new VisTable();
        VisLabel gridLabel = new VisLabel("Grid Size:");
        gridLabel.setColor(Color.BLACK);
        gridLabel.setFontScale(1.2f);

        gridSelectBox = new VisSelectBox<>();
        gridSelectBox.setItems(gridOptions);
        gridSelectBox.setSelected("Classic");
        gridSelectBox.getStyle().font.getData().setScale(1.1f);

        gridTable.add(gridLabel).right().padRight(15);
        gridTable.add(gridSelectBox).width(200).left();

        // Add components to table
        table.add(titleLabel).colspan(2).padBottom(40);
        table.row();
        table.add(gridTable).colspan(2).padBottom(30);
        table.row();

        VisTextButton rulesButton = new VisTextButton("Rules");
        VisTextButton scoreboardButton = new VisTextButton("Scoreboard");
        VisTextButton playButton = new VisTextButton("Play");

        rulesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(new RulesScreen(app));
                MenuScreen.this.dispose();
            }
        });

        scoreboardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(new ScoreboardScreen(app));
                MenuScreen.this.dispose();
            }
        });

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int index = gridSelectBox.getSelectedIndex();
                System.out.println("Selected Grid: " + gridSelectBox.getSelected());
                app.setScreen(new GameScreen(app, gridDimensions[index][0], gridDimensions[index][1]));
                MenuScreen.this.dispose(); // todo je to takto spravne?
            }
        });

        table.add(playButton).colspan(2).width(250).height(50).pad(15);
        table.row();
        table.add(rulesButton).colspan(2).width(250).height(50).pad(10);
        table.row();
        table.add(scoreboardButton).colspan(2).width(250).height(50).pad(10);
        table.row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
    public void hide() {
//        MenuScreen.this.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }
}
