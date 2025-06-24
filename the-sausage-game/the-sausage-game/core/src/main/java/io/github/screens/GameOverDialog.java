package io.github.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.*;

import io.github.MainGame;
import io.github.data.GameRepositoryProvider;
import io.github.data.GameResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dialog shown when the game ends. Allows the user to confirm winner/loser
 * names and stores the result.
 */
public class GameOverDialog extends VisDialog {
    private final MainGame game;
    private final String winnerName;

    private VisTextField winnerField;
    private VisTextField loserField;
    private VisLabel errorLabel;

    public GameOverDialog(MainGame game, String winnerName) {
        super("Game Over");
        this.game = game;
        this.winnerName = winnerName;
        build();
    }

    private void build() {
        float scale = Gdx.graphics.getDensity();
        VisTable root = new VisTable();
        getContentTable().add(root).pad(30 * scale);
        root.center();

        VisLabel winnerLabel = new VisLabel("Winner: " + winnerName);
        winnerLabel.setColor(Color.BLACK);
        root.add(winnerLabel).padBottom(20 * scale).row();

        // collect existing player names
        Set<String> names = new HashSet<>();
        List<GameResult> results = GameRepositoryProvider.getRepository().getAllGameResults();
        for (GameResult r : results) {
            names.add(r.getPlayerOne());
            names.add(r.getPlayerTwo());
        }
        String[] nameArray = names.toArray(new String[0]);

        winnerField = new VisTextField(winnerName);
        winnerField.setMessageText("Winner name");
        VisSelectBox<String> winnerSelect = new VisSelectBox<>();
        winnerSelect.setItems(nameArray);
        winnerSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                winnerField.setText(winnerSelect.getSelected());
            }
        });

        loserField = new VisTextField();
        loserField.setMessageText("Loser name");
        VisSelectBox<String> loserSelect = new VisSelectBox<>();
        loserSelect.setItems(nameArray);
        loserSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loserField.setText(loserSelect.getSelected());
            }
        });

        VisTable form = new VisTable(true);
        form.add(new VisLabel("Winner")).left();
        form.add(winnerField).width(200 * scale);
        form.add(winnerSelect).width(150 * scale).row();
        form.add(new VisLabel("Loser")).left();
        form.add(loserField).width(200 * scale);
        form.add(loserSelect).width(150 * scale).row();

        root.add(form).padBottom(20 * scale).row();

        errorLabel = new VisLabel("");
        errorLabel.setColor(Color.RED);
        root.add(errorLabel).padBottom(10 * scale).row();

        VisTextButton exitButton = new VisTextButton("Save and Exit");
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String winner = winnerField.getText().trim();
                String loser = loserField.getText().trim();
                if (winner.isEmpty() || loser.isEmpty()) {
                    errorLabel.setText("Please enter both names");
                    return;
                }
                GameResult result = new GameResult(System.currentTimeMillis(), winner, loser, System.currentTimeMillis(), true);
                GameRepositoryProvider.getRepository().insertGameResult(result);
                game.setScreen(new MenuScreen(game));
                GameOverDialog.this.hide();
            }
        });
        root.add(exitButton).width(250 * scale).height(60 * scale);

        setModal(true);
        setMovable(false);
        setResizable(false);
        setKeepWithinStage(true);
    }

    /**
     * Shows the dialog on the given stage.
     */
    public void showOn(Stage stage) {
        pack();
        centerWindow();
        show(stage);
    }
}
