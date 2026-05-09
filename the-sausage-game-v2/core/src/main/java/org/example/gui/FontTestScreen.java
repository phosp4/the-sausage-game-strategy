package org.example.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FontTestScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont myFont;

    private OrthographicCamera camera;
    private Viewport viewport;

    public FontTestScreen() {
        batch = new SpriteBatch();

        // Nastavíme virtuálne rozlíšenie hry (napr. 1280x720)
        // Hra sa vždy bude tváriť, že má túto veľkosť, bez ohľadu na veľkosť okna.
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 48; // Nastavíme veľkosť v pixeloch (toto určí aký ostrý bude)
        parameter.color = Color.WHITE; // Nastavíme farbu (môžeš meniť aj neskôr priamo cez font.setColor)
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áäčďéíĺľňóôŕšťúýžÁÄČĎÉÍĹĽŇÓÔŔŠŤÚÝŽ";

        // Filtre pre hladké vyhladzovanie okrajov (Linear je najlepší pre HD fonty)
        parameter.minFilter = TextureFilter.Linear;
        parameter.magFilter = TextureFilter.Linear;

        // 3. Vygenerujeme samotný font, ktorý už LibGDX vie vykresliť
        myFont = generator.generateFont(parameter);

        // Generátor už nepotrebujeme, font je vygenerovaný v pamäti (myFont).
        generator.dispose();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Vyčistenie obrazovky na tmavo-šedú
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Povieme batchu, aby kreslil podľa našej kamery!
        // Ak toto nespravíš, batch bude kresliť podľa "natiahnutého" okna.
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        myFont.draw(batch, "Ukažkový text!", 100, 400);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // Keď hru vypíname, musíme uvoľniť pamäť
        batch.dispose();
        myFont.dispose(); // Nezabudni zničiť aj vygenerovaný font!
    }
}
