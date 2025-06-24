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
        configuration.useImmersiveMode = true; // Recommended, but not required.
        GameRepositoryProvider.setRepository(new AndroidGameRepository(getApplicationContext()));
        initialize(new MainGame(), configuration);
    }
}
