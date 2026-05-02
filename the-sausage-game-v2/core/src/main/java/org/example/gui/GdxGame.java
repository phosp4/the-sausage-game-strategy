package org.example.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import org.example.engine.GameSession;

public class GdxGame extends Game {
    @Override public void create() {

        // odtialto by sa tiez dali dohadzovat rozmery...
        GameSession engine = new GameSession(5,5, false);
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
