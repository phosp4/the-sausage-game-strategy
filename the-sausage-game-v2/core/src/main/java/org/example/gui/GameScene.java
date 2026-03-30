package org.example.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import org.example.engine.GameController;
import org.example.engine.TurnManager;
import org.example.entities.Player;
import org.example.entities.Point;
import org.example.entities.Sausage;
import org.example.exceptions.IntersectingSausagesException;
import org.example.exceptions.InvalidPointForGridException;
import org.example.strategy.GridBitMask;
import org.example.strategy.StrategyMinimax;
import org.example.utils.CliRendererUtil;
import org.example.strategy.MoveGenerator;
import org.example.utils.ValidatorUtil;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GameScene implements Screen {

    // main stuff
    private final GdxGame game;
    private final GameController ctrl;

    // libgdx stuff
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

    // libgdx assets
    private Sound selectSound;

    // constants
    private final int columns;
    private final int rows;
    private float baseCircleRadius;
    private float enlargedCircleRadius;

    // pre vypocet rovnomernej mriezky
    private float cellSize;
    private float gridOffsetX;
    private float gridOffsetY;

//    // generate moves animation
    private List<Sausage> moves;
    private int ticker = 0;
    private int idx = 0;

    // todo temporary
    Map<Long, Sausage> strategy;

    public GameScene(GdxGame gdxGame) {
        this.game = gdxGame;
        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        this.columns = 9; // temporary
        this.rows = 7; // temporary
        this.ctrl = new GameController(columns, rows, p1, p2, p2);
        System.out.println(CliRendererUtil.gridToString(ctrl.getGameBoard().getGrid()));

        moves = new ArrayList<>(MoveGenerator.getAllPossibleMoves(ctrl.getGameBoard().getGrid()));

//        // todo temporary
//        strategy = StrategyMinimax.getFirstPlayerStrategy(columns, rows);
//        System.out.println("strategy is: " + strategy);
    }

    @Override
    public void show() {
        if (!VisUI.isLoaded()) {
            VisUI.load(); // Load VisUI skin
        }

        float scale = Gdx.graphics.getDensity();
        baseCircleRadius = 12f * scale;
        enlargedCircleRadius = 24f * scale;

        stage = new Stage(new ScreenViewport());
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

        // rest of ui
        VisTable table = new VisTable();
        table.setFillParent(true);
        VisTextButton restartButton = new VisTextButton("Restart");
        VisTextButton quitButton = new VisTextButton("Quit");
        VisTextButton soundsButton = new VisTextButton("No sounds", "toggle");
        if (!SoundManager.isSoundEnabled()) {
            soundsButton.toggle();
        }

        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScene(game));
                GameScene.this.dispose();
            }
        });
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                game.setScreen(new MenuScreen(game));
//                GameScreen.this.dispose(); // todo finish later
            }
        });
        soundsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // toggle sounds
                SoundManager.setSoundEnabled(!SoundManager.isSoundEnabled());
            }
        });

        table.add(restartButton).width(200 * scale).height(50 * scale).padBottom(20 * scale);
        table.add(quitButton).width(200 * scale).height(50 * scale).padBottom(20 * scale);
        table.add(soundsButton).width(200 * scale).height(50 * scale).padBottom(20 * scale);

        table.align(Align.bottom); // Align the table to the bottom of the screen
//        table.padBottom(0); // Add padding from the bottom edge
        stage.addActor(table);

        // docasne
//        playStrategy();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
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
        return gridOffsetY + (rows + 1f) * cellSize - cellSize * (row + 1);
    }

    /*
     * This method is called every frame to render the game.
     */
    @Override
    public void render(float delta) {

//        // generate moves animation
//        ticker++;
//        if (ticker % 2 == 0) {
//            if (!ctrl.getGameBoard().getSausages().isEmpty()) ctrl.getGameBoard().removeLastSausage();
//            if (idx >= moves.size()) {
//                idx = 0;
//            }
//            ctrl.getGameBoard().addSausage(moves.get(idx));
//            idx++;
//        }

        // this used to cause flickering, idk why
        ScreenUtils.clear(1, 1, 1, 1);
        stage.getViewport().apply(); // Zabezpečí, že viewport aplikuje svoje rozmery na aktuálny frame (nutné pri resize)
        updateGridMetrics();

        // Bezpečné získanie súradníc myši vo svete (rieši HDPI a rôzne pomery strán)
        Vector2 mousePos = stage.getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

        mouseX = mousePos.x; // Gdx.input.getX();
        mouseY = mousePos.y; // Gdx.graphics.getHeight() - Gdx.input.getY();

        // todo docasne, na skusku - nesynchronizovane s circles...
//        if (ctrl.getTurnManager().getCurrentPlayer().equals(ctrl.getAutonomousPlayer())) {
//            System.out.println(ctrl.getAutonomousPlayer());
//            Sausage s = ctrl.getAuto().getAMove(ctrl.getGameBoard().getGrid());
//            if (s == null) {
//                // game over action
//            } else {
//                s.setPlayer(ctrl.getTurnManager().getCurrentPlayer());
//                ctrl.getGameBoard().addSausage(s);
//                ctrl.getTurnManager().nextTurn();
//            }
//        }

        // KĽÚČOVÁ ZMENA: Zladenie Batch matice s kamerou tvojho Stage!
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);

        batch.begin();
        drawCircleHints();
        drawSausages();
        if (Gdx.input.isTouched()) { handleTemporaryConnections(); }
        else {
            handleNewSausage();
        }
//        drawExistingCircles();
        drawCircles();
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void handleNewSausage() {
        if (firstPoint != null && secondPoint != null && thirdPoint != null) {

            Sausage s = new Sausage(ctrl.getTurnManager().getCurrentPlayer(),
                new Point(firstPoint.getX(), firstPoint.getY()),
                new Point(secondPoint.getX(), secondPoint.getY()),
                new Point(thirdPoint.getX(), thirdPoint.getY()));

            if (ctrl.tryApplyMove(s)) {
                System.out.println(ctrl.getTurnManager().getCurrentPlayer());
                System.out.println(CliRendererUtil.gridToString(ctrl.getGameBoard().getGrid()));

                if (ctrl.getGameBoard().isGameOver()) {
                    String winnerName = ctrl.getTurnManager().getNotCurrentPlayer().getName();
                    System.out.println("Game over! Winner: " + winnerName);
//                    GameOverDialog dialog = new GameOverDialog(game, winnerName);
//                    dialog.showOn(stage);
                }
            } else {
                System.out.println("Problem with handling new sausage: " + ctrl.getLastError());
            }
//            playStrategy();
        }
        // toto je potrebne aby zabudlo, aj ked sa nevytvorila klobaska
        firstPoint = null;
        secondPoint = null;
        thirdPoint = null;
    }

    private void playStrategy() {
        // strategy player demo
        if (ctrl.getTurnManager().isPlayer1Turn()) {
            Long bitboard = GridBitMask.encode(ctrl.getGameBoard().getGrid());
            Sausage dokonalyTah = strategy.get(bitboard);
            if (dokonalyTah != null) {
                dokonalyTah.setPlayer(ctrl.getTurnManager().getCurrentPlayer());
                ctrl.getGameBoard().addSausage(dokonalyTah);
                // todo update circles
                ctrl.getTurnManager().nextTurn();
                System.out.println("Strategy played for player 1: " + dokonalyTah);
            } else {
                System.out.println(bitboard);
                System.out.println("problem");
            }
        }
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
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
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
            drawer.setDefaultLineWidth(4f);
            for (Point p : ctrl.getGameBoard().getNeighbours(anchor)) {
                if (!p.equals(firstPoint) && !p.equals(secondPoint) && !ctrl.getGameBoard().isOccupied(p.getX(), p.getY())) {
                    drawer.circle(colToX(p.getX()), rowToY(p.getY()), baseCircleRadius + 6f);
                }
            }
        }
    }

    private void updateGridMetrics() {
        // pred tym tu boli tie zakomentovane, ale bol to problem na inych displejoch
        float screenWidth = stage.getViewport().getWorldWidth(); // Gdx.graphics.getWidth();
        float screenHeight = stage.getViewport().getWorldHeight(); // Gdx.graphics.getHeight();
        float scale = Gdx.graphics.getDensity();

        // Vaša pôvodná logika pre padding zdola
        float bottomPadding = 80 * scale;
        float availableHeight = screenHeight - bottomPadding;

        // Vypočítame, aká by bola medzera, keby sme išli podľa šírky alebo podľa výšky
        float spacingX = screenWidth / (columns + 1f);
        float spacingY = availableHeight / (rows + 1f);

        // KĽÚČOVÝ KROK: Vyberieme menšiu medzeru.
        // Tým zaručíme, že sa mriežka zmestí a body budú rovnako ďaleko v X aj Y smeroch.
        cellSize = Math.min(spacingX, spacingY);

        // Aká veľká bude celá mriežka v pixeloch?
        float totalGridWidth = cellSize * (columns + 1f);
        float totalGridHeight = cellSize * (rows + 1f);

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
