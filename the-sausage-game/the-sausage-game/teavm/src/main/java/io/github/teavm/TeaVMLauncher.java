package io.github.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;

import io.github.MainGame;
import io.github.data.DummyGameRepository;
import io.github.data.GameRepositoryProvider;

/**
 * Launches the TeaVM/HTML application.
 */
public class TeaVMLauncher {
    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        GameRepositoryProvider.setRepository(new DummyGameRepository());
        //// If width and height are each greater than 0, then the app will use a fixed size.
        //config.width = 640;
        //config.height = 480;
        //// If width and height are both 0, then the app will use all available space.
        //config.width = 0;
        //config.height = 0;
        //// If width and height are both -1, then the app will fill the canvas size.
        config.width = -1;
        config.height = -1;
        new TeaApplication(new MainGame(), config);
    }
}
