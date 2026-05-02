package org.example.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
//import com.kotcrab.vis.ui.VisUI;
import org.example.engine.GameSession;
import org.example.entities.Player;
import org.example.entities.Point;
import org.example.entities.Sausage;
import org.example.strategy_minimax.MoveGenerator;
import org.example.utils.BitEncoder;
import org.example.utils.CliRendererUtil;
import org.example.utils.ValidatorUtil;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;

public class GameScene implements Screen {

    // data and game state
    private final GameSession ctrl;

    // libgdx stuff
    private final GdxGame game;
    private Stage stage;
    private Batch batch;
    private ShapeDrawer drawer;

    // temporal ui data
    private float mouseX;
    private float mouseY;
    private Point firstPoint = null;
    private Point secondPoint = null;
    private Point thirdPoint = null;
    private Point hoveredPoint = null;

    // assets and UX
    private Sound selectSound;
    private float aiThinkTimer = 0f;
    private final float AI_DELAY_SECONDS = 1f;

    // constants
    private float baseCircleRadius;
    private float enlargedCircleRadius;

    // pre vypocet rovnomernej mriezky a pod
    private float cellSize;
    private float gridOffsetX;
    private float gridOffsetY;

    // generate moves animation
    private boolean animateMoves = false;
    private List<Sausage> moves;
    private int ticker = 0;
    private int idx = 0;

    public GameScene(GdxGame gdxGame, GameSession ctrl) {

        this.game = gdxGame;
        this.ctrl = ctrl;
        System.out.println(CliRendererUtil.gridToString(ctrl.getGameBoard().getGrid()));
//        System.out.println(BitEncoder.sausageGridToLongBitboard(ctrl.getGameBoard().getGrid()));

        if (animateMoves) {
            ctrl.tryApplyMove(new Point(6, 0), new Point(7, 1), new Point(5, 1));
            ctrl.tryApplyMove(new Point(1, 1), new Point(2, 2), new Point(1, 3));
            ctrl.tryApplyMove(new Point(4, 0), new Point(4, 2), new Point(4, 4));
            moves = MoveGenerator.getPossibleMovesList(ctrl.getGameBoard().getGrid(), new Player("asdf"));
        }
    }

    @Override
    public void show() {
//        if (!VisUI.isLoaded()) {
//            VisUI.load(); // Load VisUI skin
//        }

        stage = new Stage(new ExtendViewport(800, 600));
        Gdx.input.setInputProcessor(stage);

        // Create a 1x1 pixel texture to use as a pixel for drawing
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        Texture pixelTexture = new Texture(pixmap);

        pixelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        batch = stage.getBatch();
        drawer = new ShapeDrawer(batch, new TextureRegion(pixelTexture));

        // sounds
        selectSound = Gdx.audio.newSound(Gdx.files.internal("click4.ogg"));

        // toto robilo problemy v prehliadaci
//        float scale = Gdx.graphics.getDensity();
//        loadVisUIElements();
    }

//    private void loadVisUIElements() {
//        // rest of ui
//        VisTable table = new VisTable();
//        table.setFillParent(true);
//        VisTextButton restartButton = new VisTextButton("Restart");
//        VisTextButton quitButton = new VisTextButton("Quit");
//        VisTextButton soundsButton = new VisTextButton("No sounds", "toggle");
//        if (!SoundManager.isSoundEnabled()) {
//            soundsButton.toggle();
//        }
//
//        restartButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                game.setScreen(new GameScene(game, new GameEngine()));
//                GameScene.this.dispose();
//            }
//        });
//        quitButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
////                game.setScreen(new MenuScreen(game));
////                GameScreen.this.dispose(); // todo finish later
//            }
//        });
//        soundsButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                // toggle sounds
//                SoundManager.setSoundEnabled(!SoundManager.isSoundEnabled());
//            }
//        });
//
//        table.add(restartButton).width(200).height(50).padBottom(20);
//        table.add(quitButton).width(200).height(50).padBottom(20);
//        table.add(soundsButton).width(200).height(50).padBottom(20);
//
//        table.align(Align.bottom); // Align the table to the bottom of the screen
////        table.padBottom(0); // Add padding from the bottom edge
//        stage.addActor(table);
//
//    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
            updateGridMetrics();
        }
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

    private float colToX(int col) {
        // Začiatok mriežky + jedna bunka (padding zľava) + pozícia stĺpca
        return gridOffsetX + cellSize + col * cellSize;
    }

    private float rowToY(int row) {
        // Začiatok mriežky + celá výška - pozícia riadku (odpočítavame, lebo Y=0 je dole)
        return gridOffsetY + (ctrl.getGameBoard().getRowsY() + 1f) * cellSize - cellSize * (row + 1);
    }

    /*
     * This method is called every frame to render the game.
     */
    @Override
    public void render(float delta) {

        // generate moves animation
        if (animateMoves) {
            showMovesAnimation();
        }

        // automatizacia
        if (!ctrl.isGameOver() && ctrl.getAiManager().isPlayerAi(ctrl.getTurnManager().getCurrentPlayer())) {
            aiThinkTimer += delta;
            if (aiThinkTimer >= AI_DELAY_SECONDS) {
                Sausage s = ctrl.getAiManager().getAiMoveForPlayer(ctrl.getTurnManager().getCurrentPlayer(), ctrl.getGameBoard());
                if (s == null) {
                    System.err.println("AI move not found!");
                } else {
                    ctrl.tryApplyMove(s);
                }
                aiThinkTimer = 0f;
            }
        }

        // this used to cause flickering, idk why
        ScreenUtils.clear(1, 1, 1, 1);
        stage.getViewport().apply(); // Zabezpečí, že viewport aplikuje svoje rozmery na aktuálny frame (nutné pri resize)
//        updateGridMetrics();

        //
        // TOTO MI DAL CHAT - ale nepomohlo to
        // Bezpečné získanie súradníc myši vo svete (rieši HDPI a rôzne pomery strán)
        Vector2 mousePos = stage.getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        mouseX = mousePos.x; // Gdx.input.getX();
        mouseY = mousePos.y; // Gdx.graphics.getHeight() - Gdx.input.getY();
        // KĽÚČOVÁ ZMENA: Zladenie Batch matice s kamerou tvojho Stage!
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        //

        batch.begin();
        drawCircleHints();
        drawSausages();
        if (!ctrl.getAiManager().isPlayerAi(ctrl.getTurnManager().getCurrentPlayer())) {
            if (Gdx.input.isTouched()) {
                handleTemporaryConnections();
            } else {
                handleNewSausage();
            }
        }
        drawCircles();
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void showMovesAnimation() {
        ticker++;
        if (ticker % 16 == 0) {
            if (idx >= moves.size()) {
                ctrl.getGameBoard().removeSausage(moves.get(idx-1));
                idx = 0;
            }
            if (!ctrl.getGameBoard().getSausages().isEmpty() && idx - 1 >= 0) {
                ctrl.getGameBoard().removeSausage(moves.get(idx - 1));
            }
            ctrl.getGameBoard().addSausage(moves.get(idx));
            idx++;
        }
    }

    private void handleNewSausage() {
        if (firstPoint != null && secondPoint != null && thirdPoint != null) {

            Point p1 = new Point(firstPoint.getX(), firstPoint.getY());
            Point p2 = new Point(secondPoint.getX(), secondPoint.getY());
            Point p3 = new Point(thirdPoint.getX(), thirdPoint.getY());

            ctrl.tryApplyMove(p1, p2, p3);
        }
        // toto je potrebne aby zabudlo, aj ked sa nevytvorila klobaska
        firstPoint = null;
        secondPoint = null;
        thirdPoint = null;
    }

    private void handleTemporaryConnections() {
        boolean isHoveredPointOccupied = true;

        if (hoveredPoint != null) {
            isHoveredPointOccupied = ctrl.getGameBoard().isOccupied(hoveredPoint.getX(), hoveredPoint.getY());
        }

        if (hoveredPoint != null && !isHoveredPointOccupied) {
            if (firstPoint == null) {
                firstPoint = hoveredPoint;
                SoundManager.play(selectSound);
            } else if (secondPoint == null && !hoveredPoint.equals(firstPoint) && ValidatorUtil.areNeigbours(firstPoint, hoveredPoint) && ValidatorUtil.haveNoIntersectionInGrid(firstPoint, hoveredPoint, ctrl.getGameBoard().getGrid())) {
                secondPoint = hoveredPoint;
                SoundManager.play(selectSound);
            } else if (secondPoint != null && !hoveredPoint.equals(firstPoint) && !hoveredPoint.equals(secondPoint) && ValidatorUtil.areNeigbours(secondPoint, hoveredPoint)) {
                if (!hoveredPoint.equals(thirdPoint)) {
                    thirdPoint = hoveredPoint;
                    SoundManager.play(selectSound);
                }
            }
        }

        // draw in progress connection lines
        drawer.setColor(ctrl.getTurnManager().getCurrentPlayer().getColor());
        drawer.setDefaultLineWidth(enlargedCircleRadius);

        // mozno sa to da aj krajsie, ale whatever
        if (firstPoint != null && secondPoint == null && thirdPoint == null) {
            drawer.line(colToX(firstPoint.getX()), rowToY(firstPoint.getY()), mouseX, mouseY);
            drawer.filledCircle(colToX(firstPoint.getX()), rowToY(firstPoint.getY()), baseCircleRadius); // zaoblene konce ciar
            drawer.filledCircle(mouseX, mouseY, baseCircleRadius); // zaoblene konce ciar
        } else if (firstPoint != null && secondPoint != null && thirdPoint == null) {
            drawer.line(colToX(firstPoint.getX()), rowToY(firstPoint.getY()), colToX(secondPoint.getX()), rowToY(secondPoint.getY()));
            drawer.line(colToX(secondPoint.getX()), rowToY(secondPoint.getY()), mouseX, mouseY);
            drawer.filledCircle(colToX(firstPoint.getX()), rowToY(firstPoint.getY()), baseCircleRadius);
            drawer.filledCircle(colToX(secondPoint.getX()), rowToY(secondPoint.getY()), baseCircleRadius);
            drawer.filledCircle(mouseX, mouseY, baseCircleRadius);
        } else if (firstPoint != null && secondPoint != null && thirdPoint != null) {
            drawer.line(colToX(firstPoint.getX()), rowToY(firstPoint.getY()), colToX(secondPoint.getX()), rowToY(secondPoint.getY()));
            drawer.line(colToX(secondPoint.getX()), rowToY(secondPoint.getY()), colToX(thirdPoint.getX()), rowToY(thirdPoint.getY()));
            drawer.filledCircle(colToX(firstPoint.getX()), rowToY(firstPoint.getY()), baseCircleRadius);
            drawer.filledCircle(colToX(secondPoint.getX()), rowToY(secondPoint.getY()), baseCircleRadius);
            drawer.filledCircle(colToX(thirdPoint.getX()), rowToY(thirdPoint.getY()), baseCircleRadius);
        }
    }

    private void drawCircles() {
        hoveredPoint = null;

        // iterate circles
        for (int row = 0; row < ctrl.getGameBoard().getRowsY(); row++) {
            for (int col = 0; col < ctrl.getGameBoard().getColumnsX(); col++) {
                if ((row + col) % 2 == 0) {
                    float newX = colToX(col);
                    float newY = rowToY(row);

                    //
                    boolean isHovered = Math.hypot(mouseX - newX, mouseY - newY) <= baseCircleRadius;
                    Color circleColor = getCircleColor(col, row, isHovered);

                    if (isHovered) {
                        hoveredPoint = new Point(col, row);
                    }

                    drawer.setColor(circleColor);
                    if (hoveredPoint != null && hoveredPoint.getX() == col && hoveredPoint.getY() == row) {
                        drawer.filledCircle(newX, newY, enlargedCircleRadius);
                    } else {
                        drawer.filledCircle(newX, newY, baseCircleRadius);
                    }
                }
             }
        }
    }

    // pomocna metoda k drawCircles
    private Color getCircleColor(int col, int row, boolean isHovered) {
        boolean isConnected = ctrl.getGameBoard().getGrid()[row][col] != null;
        Color circleColor;

        if (isHovered && isConnected) {
            circleColor = ctrl.getGameBoard().getGrid()[row][col].getPlayer().getColor();
        } else if (isHovered) {
            circleColor = ctrl.getTurnManager().getCurrentPlayer().getColor();
        } else {
            circleColor = Color.BLACK;
        }
        return circleColor;
    }

    private void drawSausages() {
        // Draw all connections
        drawer.setDefaultLineWidth(enlargedCircleRadius);

        for (Sausage s : ctrl.getGameBoard().getSausages()) {
            List<Point> points = s.getThreePoints();

            drawer.setColor(s.getPlayer().getColor());

            drawer.line(
                    colToX(points.get(0).getX()),
                    rowToY(points.get(0).getY()),
                    colToX(points.get(1).getX()),
                    rowToY(points.get(1).getY()));
            drawer.line(
                    colToX(points.get(1).getX()),
                    rowToY(points.get(1).getY()),
                    colToX(points.get(2).getX()),
                    rowToY(points.get(2).getY()));

            for (Point p : points) {
                drawer.filledCircle(
                        colToX(p.getX()),
                        rowToY(p.getY()),
                        baseCircleRadius);
            }

        }
    }

    private void drawCircleHints() {
        Point anchor = null;
        if (firstPoint != null && secondPoint == null) {
            anchor = firstPoint;
        } else if (secondPoint != null && thirdPoint == null) {
            anchor = secondPoint;
        }
        if (anchor != null) {
            drawer.setColor(ctrl.getTurnManager().getCurrentPlayer().getColor());
            drawer.setDefaultLineWidth(cellSize * 0.07f);
            for (Point p : ctrl.getGameBoard().getFreeNeighbours(anchor)) {
                if (!p.equals(firstPoint) && !p.equals(secondPoint) && !ctrl.getGameBoard().isOccupied(p.getX(), p.getY())) {
                    drawer.circle(colToX(p.getX()), rowToY(p.getY()), baseCircleRadius * 1.75f);
                }
            }
        }
    }

    private void updateGridMetrics() {
        // pred tym tu boli tie zakomentovane, ale bol to problem na inych displejoch
        float screenWidth = stage.getViewport().getWorldWidth(); // Gdx.graphics.getWidth();
        float screenHeight = stage.getViewport().getWorldHeight(); // Gdx.graphics.getHeight();

        // Vaša pôvodná logika pre padding zdola
        float bottomPadding = 80f;
        float availableHeight = screenHeight - bottomPadding;

        // Vypočítame, aká by bola medzera, keby sme išli podľa šírky alebo podľa výšky
        float spacingX = screenWidth / (ctrl.getGameBoard().getColumnsX() + 1f);
        float spacingY = availableHeight / (ctrl.getGameBoard().getRowsY() + 1f);

        // KĽÚČOVÝ KROK: Vyberieme menšiu medzeru.
        // Tým zaručíme, že sa mriežka zmestí a body budú rovnako ďaleko v X aj Y smeroch.
        cellSize = Math.min(spacingX, spacingY);

        // Veľkosť bodky bude napríklad 15% z veľkosti bunky (môžeš upraviť podľa vkusu)
        baseCircleRadius = cellSize * 0.15f;
        // Zväčšená bodka / hrúbka klobásky bude dvojnásobok
        enlargedCircleRadius = baseCircleRadius * 2f;

        // Aká veľká bude celá mriežka v pixeloch?
        float totalGridWidth = cellSize * (ctrl.getGameBoard().getColumnsX() + 1f);
        float totalGridHeight = cellSize * (ctrl.getGameBoard().getRowsY() + 1f);

        // Vypočítame offsety na vycentrovanie mriežky

        gridOffsetX = (screenWidth - totalGridWidth) / 2f;

        // Offset Y berie do úvahy aj bottomPadding a vycentrovanie vo zvyšnom priestore
        gridOffsetY = bottomPadding + (availableHeight - totalGridHeight) / 2f;
    }

//    private void drawBackground() {
//        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//    }

    @Override
    public void dispose() {
        batch.dispose();
        selectSound.dispose();
    }
}
