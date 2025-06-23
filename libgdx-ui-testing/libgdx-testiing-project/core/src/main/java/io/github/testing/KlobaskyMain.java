package io.github.testing;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.VisUI;

public class KlobaskyMain extends Game {
    @Override
    public void create() {
        // Initialize your game here, e.g., set the initial screen
        // rows are counted as all dots, columns only "big columns"
        setScreen(new ScoreboardScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.app.exit();
    }
}
