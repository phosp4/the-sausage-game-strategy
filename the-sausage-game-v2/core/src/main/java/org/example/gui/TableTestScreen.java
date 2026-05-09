package org.example.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TableTestScreen implements Screen {

    private Stage stage;
    private int[][] gridData;
    private CellActor currentlySelectedCell = null;

    private final Color COLOR_GREEN = new Color(0.7f, 0.9f, 0.6f, 1f);
    private final Color COLOR_RED = new Color(0.9f, 0.6f, 0.5f, 1f);
    private final Color COLOR_GREY = new Color(0.9f, 0.9f, 0.9f, 1f);
    private final Color COLOR_HEADER = new Color(0.75f, 0.75f, 0.75f, 1f);

    // Konštanty pre veľkosť
    private final int GRID_SIZE = 20; // Zmeň na 32 a tabuľka sa sama prispôsobí
    private final float CELL_SIZE = 40f;

    @Override
    public void show() {
        // 1. Nastavenie FitViewportu
        // Vypočítame presnú veľkosť virtuálneho sveta: (počet buniek + 1 hlavička) * veľkosť bunky
        float worldSize = (GRID_SIZE + 1) * CELL_SIZE;
        // FitViewport udrží pomer strán a vycentruje obsah bez naťahovania
        stage = new Stage(new FitViewport(worldSize, worldSize));
        Gdx.input.setInputProcessor(stage);

        gridData = generateDummyData(GRID_SIZE);

        Texture whiteBox = createColorTexture(Color.WHITE);
        Texture blackBorder = createBorderTexture(Color.BLACK);

        // 2. Freetype Font konfigurácia
        Label.LabelStyle labelStyle = new Label.LabelStyle(generateCustomFont(), Color.BLACK);

        // 3. Vytvorenie tabuľky bez ScrollPane
        Table mainTable = new Table();
        mainTable.setFillParent(true); // Tabuľka vyplní celý náš FitViewport a vycentruje sa

        // Hlavičky stĺpcov
        mainTable.add(new Image(whiteBox)).size(CELL_SIZE);
        for (int c = 0; c < GRID_SIZE; c++) {
            CellActor header = new CellActor(c, -1, String.valueOf(c + 1), COLOR_HEADER, whiteBox, blackBorder, labelStyle, false);
            mainTable.add(header).size(CELL_SIZE);
        }
        mainTable.row();

        // Vyplnenie riadkov
        for (int r = 0; r < GRID_SIZE; r++) {
            CellActor rowHeader = new CellActor(-1, r, String.valueOf(r + 1), COLOR_HEADER, whiteBox, blackBorder, labelStyle, false);
            mainTable.add(rowHeader).size(CELL_SIZE);

            for (int c = 0; c < GRID_SIZE; c++) {
                int value = gridData[r][c];
                Color bgColor = (value == 1) ? COLOR_GREEN : (value == -1 ? COLOR_RED : COLOR_GREY);
                boolean isClickable = (value == 1 || value == -1);

                CellActor cell = new CellActor(c, r, String.valueOf(value), bgColor, whiteBox, blackBorder, labelStyle, isClickable);

                if (isClickable) {
                    cell.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            selectCell(cell);
                        }
                    });
                }

                // Používame padding 1px, aby bola mriežka viditeľná, ale FitViewport sa postará o celkové škálovanie
                mainTable.add(cell).size(CELL_SIZE - 2).pad(1);
            }
            mainTable.row();
        }

        stage.addActor(mainTable);
    }

    // --- FREETYPE FONT GENERATOR ---
    private BitmapFont generateCustomFont() {
        // UISTI SA, ŽE MÁŠ SÚBOR V ZLOŽKE assets! Napr. assets/fonts/arial.ttf
        // Pre účely testovania, ak nemáš TTF súbor, zakomentuj túto metódu a vráť new BitmapFont()
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // Trik: Vygenerujeme väčší font (napr. 40px) aby bol ostrý,
        // ale Label ho v CellActor vykreslí do boxu, čiže ho vizuálne zmenší.
        parameter.size = 24;
        parameter.color = Color.BLACK;

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose(); // Nezabudni uvoľniť generátor z pamäte

        // lineárne filtrovanie zabezpečí vyhladenie pri zmene veľkosti okna
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        return font;
    }

    // Prispôsobenie pri zmene veľkosti okna !DÔLEŽITÉ!
    @Override
    public void resize(int width, int height) {
        // Tretí parameter 'true' vycentruje kameru na stred
        stage.getViewport().update(width, height, true);
    }

    private void selectCell(CellActor newCell) {
        if (currentlySelectedCell != null) currentlySelectedCell.setSelected(false);
        newCell.setSelected(true);
        currentlySelectedCell = newCell;
        System.out.println("Vybraná bunka: [" + newCell.gridX + ", " + newCell.gridY + "]");
    }

    @Override
    public void render(float delta) {
        // Čierne pozadie (vyplní pruhy okolo tabuľky ak má okno iný pomer strán)
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    class CellActor extends Group {
        public int gridX, gridY;
        private Image background;
        private Image border;

        public CellActor(int x, int y, String text, Color bgColor, Texture bgTexture, Texture borderTexture, Label.LabelStyle style, boolean isClickable) {
            this.gridX = x;
            this.gridY = y;

            background = new Image(bgTexture);
            background.setColor(bgColor);
            background.setFillParent(true); // Vždy vyplní Group
            this.addActor(background);

            Label label = new Label(text, style);
            label.setAlignment(Align.center);
            label.setFillParent(true);
            this.addActor(label);

            border = new Image(borderTexture);
            border.setFillParent(true);
            border.setVisible(false);
            this.addActor(border);
        }

        public void setSelected(boolean selected) {
            border.setVisible(selected);
        }
    }

    private int[][] generateDummyData(int size) {
        int[][] data = new int[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (r > 13 || c > 13) data[r][c] = -3;
                else if (r == c) data[r][c] = -1;
                else data[r][c] = 1;
            }
        }
        return data;
    }

    private Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    private Texture createBorderTexture(Color color) {
        // Border sa vytvorí relatívne k hrúbke 40x40.
        Pixmap pixmap = new Pixmap((int)CELL_SIZE, (int)CELL_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        // Kreslíme čiary okolo celého obvodu, hrúbka 3 pixely
        for(int i = 0; i < 3; i++) {
            pixmap.drawRectangle(i, i, (int)CELL_SIZE - (i*2), (int)CELL_SIZE - (i*2));
        }
        Texture tex = new Texture(pixmap);
        // Lineárne filtrovanie aby bol okraj hladký aj po zväčšení
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return tex;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
