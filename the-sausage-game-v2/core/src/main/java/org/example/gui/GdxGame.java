package org.example.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class GdxGame extends Game {
    @Override public void create() {
        setScreen(new GameScene(this));
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
