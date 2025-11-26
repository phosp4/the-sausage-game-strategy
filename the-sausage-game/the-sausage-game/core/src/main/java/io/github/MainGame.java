package io.github;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import io.github.screens.GameScreen;
import io.github.screens.MenuScreen;
import io.github.screens.RulesScreen;

public class MainGame extends Game {
    @Override
    public void create() {
        // Initialize your game here, e.g., set the initial screen
        // rows are counted as all dots, columns only "big columns"
        setScreen(new MenuScreen(this));
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

