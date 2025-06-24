package io.github.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.MainGame;
import io.github.data.GameRepositoryProvider;
import io.github.data.GameResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardScreen implements Screen {
    private Stage stage;
    private MainGame app;
    private Texture background;

    public ScoreboardScreen(MainGame app) {
        this.app = app;
    }

    @Override
    public void show() {
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        float scale = Gdx.graphics.getDensity();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture(Gdx.files.internal("white-paper-texture.png"));

        VisTable root = new VisTable();
        root.setFillParent(true);
        root.top().pad(30);
        stage.addActor(root);

        VisLabel titleLabel = new VisLabel("Scoreboard");
        titleLabel.setColor(Color.NAVY);
        titleLabel.setFontScale(2f * scale);
        root.add(titleLabel).padBottom(20).row();

        VisTable table = new VisTable(true);
        table.top();

        // Header
        table.add(addFormatedVisLabel("Player", Color.BLACK)).pad(10);
        table.add(addFormatedVisLabel("Wins", Color.BLACK)).pad(10);
        table.add(addFormatedVisLabel("Losses", Color.BLACK)).pad(10);
        table.add(addFormatedVisLabel("Points", Color.BLACK)).pad(10);
        table.row();

        // Load results from repository and aggregate stats
        List<GameResult> results = GameRepositoryProvider.getRepository().getAllGameResults();
        Map<String, PlayerStats> stats = new HashMap<>();
        for (GameResult result : results) {
            PlayerStats winner = stats.computeIfAbsent(result.getPlayerOne(), k -> new PlayerStats());
            if (result.isPlayerOneWon()) winner.wins++; else winner.losses++;

            PlayerStats loser = stats.computeIfAbsent(result.getPlayerTwo(), k -> new PlayerStats());
            if (result.isPlayerOneWon()) loser.losses++; else loser.wins++;
        }

        for (Map.Entry<String, PlayerStats> entry : stats.entrySet()) {
            PlayerStats s = entry.getValue();
            addRow(table, entry.getKey(), s.wins, s.losses, s.points());
        }

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        root.add(scrollPane).width(600 * scale).height(300 * scale).padBottom(30 * scale).row();

        VisTextButton backButton = new VisTextButton("Back to Menu");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(new MenuScreen(app));
                ScoreboardScreen.this.dispose();
            }
        });

        root.add(backButton).width(250 * scale).height(50 * scale);
    }

    private VisLabel addFormatedVisLabel(String text, Color color) {
        VisLabel label = new VisLabel(text);
        label.setColor(color);
//        label.setFontScale(1.2f);
        return label;
    }

    private void addRow(VisTable table, String player, int wins, int losses, int points) {
        table.add(addFormatedVisLabel(player, Color.BLACK)).pad(5);
        table.add(addFormatedVisLabel(String.valueOf(wins), Color.BLACK)).pad(5);
        table.add(addFormatedVisLabel(String.valueOf(losses), Color.BLACK)).pad(5);
        table.add(addFormatedVisLabel(String.valueOf(points), Color.BLACK)).pad(5);
        table.row();
    }

    private static class PlayerStats {
        int wins;
        int losses;

        int points() {
            return wins * 3 - losses;
        }
    }

    @Override
    public void render(float delta) {
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
