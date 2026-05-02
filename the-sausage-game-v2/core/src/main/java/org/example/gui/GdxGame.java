package org.example.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import org.example.engine.GameSession;

public class GdxGame extends Game {

    // Uloženie inštancie pre prístup z launcheru
    public static GdxGame instance;

    @Override
    public void create() {
        instance = this;

        // Môžeš tu načítať základnú hru, alebo len nechať čiernu obrazovku/menu,
        // kým užívateľ nestlačí tlačidlo v HTML.
        // Pre ukážku spustíme defaultnú hru:
        startNewGame(9, 7, "pvp");
    }

    // Metóda na dynamické spustenie/reštartovanie hry
    public void startNewGame(int width, int height, String mode) {
        if (this.getScreen() != null) {
            this.getScreen().dispose();
        }

        // Tu zistíš, či máš spustiť hru s AI (pve) alebo pre dvoch (pvp)
        boolean isPvE = mode.equals("pve");

        // Predpokladám, že musíš upraviť GameSession, aby prijímal informáciu o AI
        GameSession engine = new GameSession(width, height, isPvE);

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
