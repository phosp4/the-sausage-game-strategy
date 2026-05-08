package org.example.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.example.engine.GameSession;

public class GdxGame extends Game {

    // Uloženie inštancie pre prístup z launcheru
    public static GdxGame instance;
    public AssetManager assets;

    @Override
    public void create() {
        instance = this;
        assets = new AssetManager();
        assets.load("icons.atlas", TextureAtlas.class);
        assets.finishLoading(); // Zablokujeme vlákno, kým sa všetko nenačíta (pre loading screeny sa používa assets.update())

//        setScreen(new GameScene(this, engine));
//        setScreen(new GameScene(this, new GameSession(9, 7, true)));
//        setScreen(new FontTestScreen());
//        setScreen(new TableTestScreen());
        setScreen(new MenuScreen(this, 9, 7, true));
    }

    // Metóda na dynamické spustenie/reštartovanie hry
    public void startNewGame(int width, int height, boolean isAi) {
        if (this.getScreen() != null) {
            this.getScreen().dispose();
        }

        setScreen(new GameScene(this, new GameSession(width, height, isAi)));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();
        Gdx.app.exit();
    }
}
