package org.example.teavm;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import org.example.gui.GdxGame;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSExport;

/**
 * Launches the TeaVM/HTML application.
 */
public class TeaVMLauncher {

    public static void main(String[] args) {
        exposeToWindow();

        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        //// If width and height are each greater than 0, then the app will use a fixed size.
        //config.width = 640;
        //config.height = 480;
        //// If width and height are both 0, then the app will use all available space.
        config.width = 0;
        config.height = 0;
//        config.useGL30 = true; // vo videu mu nesli textures, tak toto pridal
        new TeaApplication(new GdxGame(), config);
    }

    @JSBody(script = "window.startLibgdxGame = startLibgdxGame;")
    public static native void exposeToWindow();

    // Táto anotácia spôsobí, že sa v JavaScripte vytvorí funkcia window.startLibgdxGame()
    @JSExport()
    public static void startLibgdxGame(int width, int height, boolean isAi) {
        // Uistíme sa, že hra je už inicializovaná
        if (GdxGame.instance != null) {
            // Použijeme postRunnable pre bezpečnú synchronizáciu s vykresľovacím vláknom LibGDX
            Gdx.app.postRunnable(() -> {
                GdxGame.instance.startNewGame(width, height, isAi);
            });
        } else {
            System.err.println("GdxGame ešte nie je inicializovaná! Skúste neskôr...");
        }
    }
}
