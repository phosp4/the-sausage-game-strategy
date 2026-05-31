package org.example.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import org.example.engine.GameSession;
import org.example.entities.Player;
import com.badlogic.gdx.graphics.Color;

public class GameUI extends Table {

    private final GameSession ctrl;

    // UI Prvky, ktorých stav budeme neskôr meniť
    private Image p1Icon, p2Icon;
    private Table centerGameOverTable;
    private Image winnerIcon; // Ikonka víťaza vedľa korunky
    private ImageButton soundButton;

    private final TextureAtlas.AtlasRegion texHuman;
    private final TextureAtlas.AtlasRegion texAi;
    private final TextureAtlas.AtlasRegion texSoundOn;
    private final TextureAtlas.AtlasRegion texSoundOff;

    private final Color veryLightGrey = new Color(0.950f, 0.960f, 0.960f, 1);

    /**
     * @param game       Odkaz na hlavnú hru (kvôli assetom)
     * @param ctrl       Herná relácia (získavanie hráčov, zistenie výhercu)
     * @param onRestart  Funkcia, ktorá sa zavolá, keď hráč klikne na reštart
     * @param onHome     Funkcia, ktorá sa zavolá pri návrate do menu
     */
    public GameUI(GdxGame game, GameSession ctrl, Runnable onRestart, Runnable onHome, Runnable toggleSound) {
        this.ctrl = ctrl;

        // Nastavenie hlavnej tabuľky (celej obrazovky)
        this.setFillParent(true);
        this.top().pad(20);
        this.top().padTop(20).padLeft(60).padRight(60); // Väčšie odsadenie od krajov

        // KĽÚČOVÉ: Zabezpečí, že kliknúť sa dá len na tlačidlá, prázdne miesto v tabuľke prepustí kliknutie nižšie do hracej plochy
        this.setTouchable(Touchable.childrenOnly);

        // Získanie textúr z AssetManagera
        TextureAtlas atlas = game.assets.get("icons.atlas", TextureAtlas.class);
        atlas.getTextures().first().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//        TextureAtlas.AtlasRegion texHome = atlas.findRegion("home_icon");
        texSoundOn = atlas.findRegion("volume_on");
        texSoundOff = atlas.findRegion("volume_off");
        TextureAtlas.AtlasRegion texRestart = atlas.findRegion("refresh_icon");
        TextureAtlas.AtlasRegion texCrown = atlas.findRegion("crown_icon");

        texHuman = atlas.findRegion("player_icon");
        texAi = atlas.findRegion("computer_icon");

        // --- LÁVÁ ČASŤ (Tlačidlá) ---
        Table leftTable = new Table();
        leftTable.setTouchable(Touchable.childrenOnly);

//        ImageButton homeBtn = new ImageButton(new TextureRegionDrawable(texHome));
        soundButton = new ImageButton(
            new TextureRegionDrawable(SoundManager.isSoundEnabled() ? texSoundOn : texSoundOff)
        );
        ImageButton restartBtn = new ImageButton(new TextureRegionDrawable(texRestart));

        // ZAFARBENIE NA ČIERNO (Za predpokladu, že zdrojové PNG sú biele s priehľadným pozadím)
//        homeBtn.getImage().setColor(Color.BLACK);
        soundButton.getImage().setColor(Color.BLACK);
        restartBtn.getImage().setColor(Color.BLACK);

        // Listenery spúšťajú "Runnable" funkcie podané z GameScene
//        homeBtn.addListener(new ClickListener() {
//            @Override public void clicked(InputEvent event, float x, float y) { onHome.run(); }
//        });
        restartBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onRestart.run(); }
        });
        soundButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                toggleSound.run();
            }
        });

        // Tlačidlá sú teraz väčšie (napr. 56 namiesto 48) pre lepšiu výraznosť
//        leftTable.add(homeBtn).size(64).padRight(20);
        leftTable.add(soundButton).size(64).padRight(20);
        leftTable.add(restartBtn).size(64);
        // --- STREDNÁ ČASŤ (Koniec hry - Korunka + Výherca) ---
        centerGameOverTable = new Table();
        Image crownImage = new Image(texCrown);
        crownImage.setColor(Color.GOLD); // Korunku môžeme zafarbiť do zlata

        winnerIcon = new Image(texHuman); // Zástupný obrázok, zmení sa pri výhre

        centerGameOverTable.add(crownImage).size(64).padRight(15);
        centerGameOverTable.add(winnerIcon).size(64);
        centerGameOverTable.setVisible(false); // Skryté počas hry

        // --- PRAVÁ ČASŤ (Hráči) ---
        Table rightTable = new Table();
        rightTable.setTouchable(Touchable.childrenOnly);

        Player p1 = ctrl.getPlayers().get(0);
        Player p2 = ctrl.getPlayers().get(1);

        p1Icon = new Image(ctrl.getAiManager().isPlayerAi(p1) ? texAi : texHuman);
        p1Icon.setColor(p1.getColor());
        // Kľúčové pre správne fungovanie zväčšovania (Scale): bod zväčšovania musí byť v strede ikonky
//        p1Icon.setOrigin(Align.center);

        p2Icon = new Image(ctrl.getAiManager().isPlayerAi(p2) ? texAi : texHuman);
        p2Icon.setColor(p2.getColor());
        p2Icon.setOrigin(Align.center);

        rightTable.add(p1Icon).size(80).padRight(10);
        rightTable.add(p2Icon).size(80);

        // Zloženie tabuľky
        this.add(leftTable).expandX().left();
        this.add(centerGameOverTable).expandX().center();
        this.add(rightTable).expandX().right();
    }

    public void updateState() {
        soundButton.getStyle().imageUp = new TextureRegionDrawable(SoundManager.isSoundEnabled() ? texSoundOn : texSoundOff);
        if (ctrl.isGameOver()) {
            if (!centerGameOverTable.isVisible()) {
                centerGameOverTable.setVisible(true);

                // SKRYJEME IKONY HRÁČOV V PRAVO, KEĎ JE KONIEC HRY
                p1Icon.setColor(veryLightGrey);
                p2Icon.setColor(veryLightGrey);

                // LOGIKA VÝHERCU (Prispôsob si to tvojej GameSession triede!)
                Player winner = ctrl.getWinner();

                if (winner != null) {
                    boolean isAi = ctrl.getAiManager().isPlayerAi(winner);
                    winnerIcon.setDrawable(new TextureRegionDrawable(isAi ? texAi : texHuman));
                    winnerIcon.setColor(winner.getColor());
                } else {
                    winnerIcon.setVisible(false);
                }
            }
        } else {
            // VÝRAZNÉ ZOBRAZENIE HRÁČA NA ŤAHU
            Player currentPlayer = ctrl.getTurnManager().getCurrentPlayer();

            if (currentPlayer.equals(ctrl.getPlayers().get(0))) {
                p1Icon.setColor(ctrl.getPlayers().get(0).getColor());
                p2Icon.setColor(veryLightGrey);
            } else {
                    p1Icon.setColor(veryLightGrey);
                p2Icon.setColor(ctrl.getPlayers().get(1).getColor());
            }
        }
    }
}
