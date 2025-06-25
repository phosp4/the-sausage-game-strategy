package io.github.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import io.github.MainGame;
import io.github.data.GameRepositoryProvider;
import io.github.android.db.AndroidGameRepository;

/** Launches the Android application. */

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        // Disable immersive mode so Android can resize the window when the
        // soft keyboard appears. This prevents input fields in dialogs from
        // being covered by the on-screen keyboard.
        configuration.useImmersiveMode = false;
        GameRepositoryProvider.setRepository(new AndroidGameRepository(getApplicationContext()));
        initialize(new MainGame(), configuration);
    }
}
