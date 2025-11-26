package org.example.gui_utils;

import com.badlogic.gdx.audio.Sound;

/**
 * SoundManager wraps sound playback functionality.
 * It allows enabling/disabling sound globally.
 */
public class SoundManager {
    private static boolean soundEnabled = true;

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Plays the sound with specified volume, returns sound ID if played, -1 otherwise.
     */
    public static long play(Sound sound) {
        if (soundEnabled && sound != null) {
            return sound.play(0.2f);
        }
        return -1; // Invalid ID, nothing played
    }

    public static void stop(Sound sound, long soundId) {
        if (sound != null && soundId != -1) {
            sound.stop(soundId);
        }
    }

    public static void setPitch(Sound sound, long soundId, float pitch) {
        if (sound != null && soundId != -1) {
            sound.setPitch(soundId, pitch);
        }
    }

    public static void setLooping(Sound sound, long soundId, boolean looping) {
        if (sound != null && soundId != -1) {
            sound.setLooping(soundId, looping);
        }
    }

    public static void setPan(Sound sound, long soundId, float pan, float volume) {
        if (sound != null && soundId != -1) {
            sound.setPan(soundId, pan, volume);
        }
    }

    public static void dispose(Sound sound) {
        if (sound != null) {
            sound.dispose();
        }
    }
}
