package org.example;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import org.example.gui.GameboardScene;

public class GdxGame extends Game {
    @Override public void create() {
        setScreen(new GameboardScene(this));
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
