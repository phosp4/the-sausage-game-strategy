package org.example.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import org.example.entities.Player;

public class MenuScreen implements Screen {

    private final GdxGame game;
    private Stage stage;

    // Fonty (static pre znovupoužitie)
    private static BitmapFont titleFont;
    private static BitmapFont normalFont;

    // Hodnoty pre hru
    private final int MIN_SIZE = 1;
    private final int MAX_SIZE = 55;
    // TU SI NASTAV MAXIMÁLNU VEĽKOSŤ PRE AI (počet políčok X * Y)
    // Napríklad 8x8 = 64. Ak máš iné limity (napr. max obvod), uprav metódu validateSettings()
    private final int MAX_AI_AREA = 55;
    private boolean darkMode = true;
    private Color assetsColor;
    private Color secondaryAssetsColor = Color.DARK_GRAY; // pouzite na male rychle sipky

    private int gridX;
    private int gridY;
    private boolean isAiEnabled;

    // UI prvky, ktoré sa menia
    private Label xValueLabel;
    private Label yValueLabel;
    private ImageButton aiCheckboxBtn;
    private Label errorLabel;

    // Textúry pre checkbox
    private TextureRegionDrawable texCheckOn;
    private TextureRegionDrawable texCheckOff;

    public MenuScreen(GdxGame game, int x, int y, boolean isAi) {
        this.game = game;
        gridX = x;
        gridY = y;
        isAiEnabled = isAi;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(800, 600));
        Gdx.input.setInputProcessor(stage);

        generateFonts();
        loadTextures();

        if (darkMode) {
            assetsColor = Color.WHITE;
        } else {
            assetsColor = Color.BLACK;
        }
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, assetsColor);
        Label.LabelStyle normalStyle = new Label.LabelStyle(normalFont, assetsColor);
        Label.LabelStyle errorStyle = new Label.LabelStyle(normalFont, Color.RED);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
//        rootTable.setRound(true); // ZABEZPEČÍ, ŽE VŠETKO BUDE NA CELÝCH PIXELOCH (X: 150.0)

        // 1. NÁPIS KLOBÁSKY
        Label titleLabel = new Label("KLOBÁSKY", titleStyle);
        rootTable.add(titleLabel).padBottom(50).colspan(2).row();

        // 2. VÝBER ROZMEROV
        Table dimensionsTable = new Table();
        Table xTable = createDimensionSelector("Šírka (X):", normalStyle, gridX, true);
        Table yTable = createDimensionSelector("Výška (Y):", normalStyle, gridY, false);

        dimensionsTable.add(xTable).padRight(40);
        dimensionsTable.add(yTable);
        rootTable.add(dimensionsTable).padBottom(40).row();

        // 3. TLAČIDLO HRAŤ PROTI AI
        Table aiTable = new Table();

        if (!isAiEnabled) {
            aiCheckboxBtn = new ImageButton(texCheckOff);
        } else {
            aiCheckboxBtn = new ImageButton(texCheckOn);
        }
        // Zafarbenie ikonky na čierno (ak je PNG biele/priehľadné)
        aiCheckboxBtn.getImage().setColor(assetsColor);

        aiCheckboxBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleAi();
            }
        });

        Label aiLabel = new Label(" Hrať proti stratégii", normalStyle);
        aiLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleAi();
            }
        });

        aiTable.add(aiCheckboxBtn).size(48);
        aiTable.add(aiLabel);
        rootTable.add(aiTable).padBottom(30).row();

        // 4. CHYBOVÁ HLÁŠKA
        errorLabel = new Label("", errorStyle);
        errorLabel.setAlignment(Align.center);
        rootTable.add(errorLabel).padBottom(20).height(40).row();

        // 5. TLAČIDLO HRAŤ!
        Label playBtn = new Label("HRAŤ!", titleStyle);
//        playBtn.setColor(Color.GREEN); // ZMENA: namiesto BLACK
        // Hracie tlačidlo môže ostať čierne (alebo ho daj Color.DARK_GRAY, ak chceš)
        playBtn.setColor(assetsColor);
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame();
            }
        });
        rootTable.add(playBtn).padTop(20);

        stage.addActor(rootTable);

        // Hneď po vytvorení prebehneme validáciu, aby bolo všetko zosynchronizované
        validateSettings();
    }

    private void toggleAi() {
        isAiEnabled = !isAiEnabled;
        aiCheckboxBtn.getStyle().imageUp = isAiEnabled ? texCheckOn : texCheckOff;
        // Po prekliknutí AI skontrolujeme, či aktuálne rozmery nie sú pre AI priveľké
        validateSettings();
    }

    private Table createDimensionSelector(String labelText, Label.LabelStyle style, int initialValue, boolean isX) {
        Table table = new Table();

        Label title = new Label(labelText, style);
        table.add(title).colspan(5).padBottom(10).row();

        TextureAtlas atlas = game.assets.get("icons.atlas", TextureAtlas.class);
        ImageButton leftBtn = new ImageButton(new TextureRegionDrawable(atlas.findRegion("arrow_left")));
        ImageButton rightBtn = new ImageButton(new TextureRegionDrawable(atlas.findRegion("arrow_right")));

        ImageButton fastLeftBtn = new ImageButton(new TextureRegionDrawable(atlas.findRegion("arrow_left")));
        ImageButton fastRightBtn = new ImageButton(new TextureRegionDrawable(atlas.findRegion("arrow_right")));

        // Zafarbenie šípok na čierno
        leftBtn.getImage().setColor(assetsColor);
        rightBtn.getImage().setColor(assetsColor);
        fastLeftBtn.getImage().setColor(secondaryAssetsColor);
        fastRightBtn.getImage().setColor(secondaryAssetsColor);

        Label valueLabel = new Label(String.valueOf(initialValue), style);
        valueLabel.setAlignment(Align.center);

        if (isX) xValueLabel = valueLabel;
        else yValueLabel = valueLabel;

        fastLeftBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeDimension(isX, -10);
            }
        });

        fastRightBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeDimension(isX, 10);
            }
        });

        leftBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeDimension(isX, -1);
            }
        });

        rightBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeDimension(isX, 1);
            }
        });

        table.add(fastLeftBtn).size(28).padRight(-15);
        table.add(leftBtn).size(48);
        table.add(valueLabel).width(60);
        table.add(rightBtn).size(48);
        table.add(fastRightBtn).size(28).padLeft(-15);

        return table;
    }

    private void changeDimension(boolean isX, int delta) {
        if (isX) {
            gridX += delta;
            if (gridX < MIN_SIZE) gridX = MIN_SIZE;
            if (gridX > MAX_SIZE) gridX = MAX_SIZE;
            xValueLabel.setText(String.valueOf(gridX));
        } else {
            gridY += delta;
            if (gridY < MIN_SIZE) gridY = MIN_SIZE;
            if (gridY > MAX_SIZE) gridY = MAX_SIZE;
            yValueLabel.setText(String.valueOf(gridY));
        }

        // Zakaždým keď zmeníme číslo, overíme platnosť
        validateSettings();
    }

    // TÁTO METÓDA KONTROLUJE, ČI SÚ NASTAVENIA V PORIADKU
    private void validateSettings() {
        errorLabel.setText(""); // Zmažeme staré chyby

        // 1. Kontrola príliš malej mriežky
        if (gridX * gridY < 5) {
            errorLabel.setText("Plocha je príliš malá.");
            return;
        }

        // 2. Kontrola pre AI
        if (isAiEnabled) {
            int currentArea = gridX * gridY;
            if (currentArea > MAX_AI_AREA && (gridX != 9 || gridY != 7)) {
                // Výpis chyby pri AI
                errorLabel.setText("Pre hru proti AI je mriežka priveľká.");
            }
        }
    }

    private void startGame() {
        // Zistíme, či je text chyby prázdny (či je všetko OK)
        if (!errorLabel.getText().toString().isEmpty()) {
            // Ak tam je chyba, nespustíme hru. (Hráč musí opraviť nastavenia)
            // Môžeme sem pridať jemnú animáciu trasenia, ale zatial stačí blokovať.
            return;
        }

        game.startNewGame(gridX, gridY, isAiEnabled);
    }

    private void loadTextures() {
        TextureAtlas atlas = game.assets.get("icons.atlas", TextureAtlas.class);

//        // ZABEZPEČÍ HLADKÉ ŠKÁLOVANIE VŠETKÝCH IKONIEK V ATLASE
//        for (com.badlogic.gdx.graphics.Texture texture : atlas.getTextures()) {
//            texture.setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Linear,
//                com.badlogic.gdx.graphics.Texture.TextureFilter.Linear);
//        }

        texCheckOn = new TextureRegionDrawable(atlas.findRegion("checkbox_on"));
        texCheckOff = new TextureRegionDrawable(atlas.findRegion("checkbox_off"));
    }

    private void generateFonts() {
        if (titleFont != null && normalFont != null) {
            return; // vygenerujte fonty len raz, inak sa plytvá výkonom a vznikajú lagy
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áäčďéíĺľňóôŕšťúýžÁÄČĎÉÍĹĽŇÓÔŔŠŤÚÝŽ";
        parameter.minFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
        parameter.magFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;

//        // TOTO JE KĽÚČOVÉ PRE KRÁSNY TEXT PRI ŠKÁLOVANÍ:
//        parameter.genMipMaps = true;
//        parameter.minFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.MipMapLinearLinear;
//        parameter.magFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;

//        // Pridaj miernu hrúbku (gamma korekcia), aby čierna na bielej toľko nezanikala
//        parameter.gamma = 0.8f; // Čím menšie číslo (napr. 0.8), tým hrubšie/výraznejšie písmo

        parameter.size = 72;
        titleFont = generator.generateFont(parameter);

        parameter.size = 36;
        normalFont = generator.generateFont(parameter);

        generator.dispose();
    }

    @Override
    public void render(float delta) {
        if (darkMode) {
            Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1f); // dark mode
        } else {
            Gdx.gl.glClearColor(1f, 1f, 1f, 1f); // biele pozadie
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        // Zrušené diposovanie statických fontov, keďže ich chceme recyklovať
    }
}
