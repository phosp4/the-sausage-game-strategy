package org.example.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import org.example.engine.GameEngine;

public class GdxGame extends Game {
    @Override public void create() {

        // odtialto by sa tiez dali dohadzovat rozmery...
        GameEngine engine = new GameEngine();
        setScreen(new GameScene(this, engine));
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
