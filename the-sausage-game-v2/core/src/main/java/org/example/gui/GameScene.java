package org.example.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private GridCircle firstCircle = null;
    private GridCircle secondCircle = null;
    private GridCircle thirdCircle = null;
    private GridCircle currentlyHoveredCircle;
    private float mouseX;
    private float mouseY;
    private float baseCircleRadius;
    private float enlargedCircleRadius;
    private boolean isTouched;

    // libgdx assets
//    private Texture background;
    private Sound selectSound;

    private final int columns;
    private final int rows;

    // pre vypocet rovnomernej mriezky
    private float cellSize;
    private float gridOffsetX;
    private float gridOffsetY;

    private List<GridCircle> circles;

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
//        background = new Texture(Gdx.files.internal("white-paper-texture.png"));

        // Create a 1x1 pixel texture to use as a pixel for drawing
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        Texture pixelTexture = new Texture(pixmap);
        pixelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        batch = stage.getBatch();
        drawer = new ShapeDrawer(batch, new TextureRegion(pixelTexture));

        circles = new ArrayList<>();
        generateCircles();

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

//        // testing
//        System.out.println(moves.size());
//        System.out.println(new HashSet<>(moves).size());

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

    private void generateCircles() {

        // Clear existing circles and connections
        circles.clear();

        // Generate circles based on the Checkerboard parity logic
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if ((row + col) % 2 == 0) {
                    circles.add(new GridCircle(
                            colToX(col, row),
                            rowToY(col, row),
                            row, col, baseCircleRadius, enlargedCircleRadius));
                }
            }
        }
    }

    private float colToX(int col, int row) {
        // Začiatok mriežky + jedna bunka (padding zľava) + pozícia stĺpca
        return gridOffsetX + cellSize + col * cellSize;
    }

    private float rowToY(int col, int row) {
        // Začiatok mriežky + celá výška - pozícia riadku (odpočítavame, lebo Y=0 je dole)
        return gridOffsetY + (rows + 1f) * cellSize - cellSize * (row + 1);
    }

    // screen x and screen y
    private float sx(GridCircle c) {
        return colToX(c.getCol(), c.getRow());
    }

    private float sy(GridCircle c) {
        return rowToY(c.getCol(), c.getRow());
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
        updateGridMetrics();

        mouseX = Gdx.input.getX();
        mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        isTouched = Gdx.input.isTouched();

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

        batch.begin();
        drawCircleHints();
        drawSausages();
        if (isTouched) { handleTemporaryConnections(); }
        else {
            handleNewSausage();
            firstCircle = null;
            secondCircle = null;
            thirdCircle = null;
        }
        drawExistingCircles();
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void handleNewSausage() {
        if (firstCircle != null && secondCircle != null && thirdCircle != null) {
            try {
                Sausage s = new Sausage(ctrl.getCurrentPlayer(),
                        new Point(firstCircle.getCol(), firstCircle.getRow()),
                        new Point(secondCircle.getCol(), secondCircle.getRow()),
                        new Point(thirdCircle.getCol(), thirdCircle.getRow()));
                ctrl.getGameBoard().addSausage(s);
            } catch (InvalidPointForGridException e) {
                System.err.println(e);
                return;
            } catch (IntersectingSausagesException e) {
                System.err.println(e);
                return;
            }

            firstCircle.setIsConnected(true, ctrl.getTurnManager().getCurrentPlayer());
            secondCircle.setIsConnected(true, ctrl.getTurnManager().getCurrentPlayer());
            thirdCircle.setIsConnected(true, ctrl.getTurnManager().getCurrentPlayer());

            System.out.println(ctrl.getTurnManager().getCurrentPlayer());
            ctrl.getTurnManager().nextTurn();
            System.out.println(CliRendererUtil.gridToString(ctrl.getGameBoard().getGrid()));

            if (ctrl.getGameBoard().isGameOver()) {
                String winnerName = ctrl.getTurnManager().getNotCurrentPlayer().getName();
//                GameOverDialog dialog = new GameOverDialog(game, winnerName);
//                dialog.showOn(stage);
                firstCircle = null;
                secondCircle = null;
                thirdCircle = null;
            }

//            playStrategy();
        }
    }

    private void playStrategy() {
        // strategy player demo
        if (ctrl.getTurnManager().isPlayer1Turn()) {
            Long bitboard = GridBitMask.encode(ctrl.getGameBoard().getGrid());
            Sausage dokonalyTah = strategy.get(bitboard);
            if (dokonalyTah != null) {
                dokonalyTah.setPlayer(ctrl.getCurrentPlayer());
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

    // toto nechavam - su to len docasne spojenia ktore sa tahaju
    private void handleTemporaryConnections() {
        if (firstCircle == null && currentlyHoveredCircle != null && !currentlyHoveredCircle.getIsConnected()) {
            firstCircle = currentlyHoveredCircle;
            SoundManager.play(selectSound);
        } else if (firstCircle != null && secondCircle == null && currentlyHoveredCircle != null
                && currentlyHoveredCircle != firstCircle && ValidatorUtil.areNeigbours(new Point(firstCircle.getCol(), firstCircle.getRow()), new Point(currentlyHoveredCircle.getCol(), currentlyHoveredCircle.getRow())) && !currentlyHoveredCircle.getIsConnected()) {
            secondCircle = currentlyHoveredCircle;
            SoundManager.play(selectSound);
        } else if (firstCircle != null && secondCircle != null && currentlyHoveredCircle != null
                && currentlyHoveredCircle != firstCircle && currentlyHoveredCircle != secondCircle
                && ValidatorUtil.areNeigbours(new Point(secondCircle.getCol(), secondCircle.getRow()), new Point(currentlyHoveredCircle.getCol(), currentlyHoveredCircle.getRow())) && !currentlyHoveredCircle.getIsConnected()) {
            if (thirdCircle != currentlyHoveredCircle) { // Play sound only when thirdCircle is newly assigned
                thirdCircle = currentlyHoveredCircle;
                SoundManager.play(selectSound);
            }
            thirdCircle = currentlyHoveredCircle;
        }

        // Draw connection-in-progress lines
        drawer.setColor(ctrl.getTurnManager().getCurrentPlayer().getColor());
        drawer.setDefaultLineWidth(enlargedCircleRadius);

        if (firstCircle != null && secondCircle == null) {
            drawer.line(sx(firstCircle), sy(firstCircle), mouseX, mouseY);
            drawer.filledCircle(sx(firstCircle), sy(firstCircle), baseCircleRadius);
            drawer.filledCircle(mouseX, mouseY, baseCircleRadius);
        } else if (firstCircle != null && secondCircle != null && thirdCircle == null) {
            drawer.line(sx(firstCircle), sy(firstCircle), sx(secondCircle), sy(secondCircle));
            drawer.line(sx(secondCircle), sy(secondCircle), mouseX, mouseY);
            drawer.filledCircle(sx(firstCircle), sy(firstCircle), baseCircleRadius);
            drawer.filledCircle(sx(secondCircle), sy(secondCircle), baseCircleRadius);
            drawer.filledCircle(mouseX, mouseY, baseCircleRadius);
        } else if (firstCircle != null && secondCircle != null && thirdCircle != null) {
            drawer.line(sx(firstCircle), sy(firstCircle), sx(secondCircle), sy(secondCircle));
            drawer.line(sx(secondCircle), sy(secondCircle), sx(thirdCircle), sy(thirdCircle));
            drawer.filledCircle(sx(firstCircle), sy(firstCircle), baseCircleRadius);
            drawer.filledCircle(sx(secondCircle), sy(secondCircle), baseCircleRadius);
            drawer.filledCircle(sx(thirdCircle), sy(thirdCircle), baseCircleRadius);
        }

    }

    // toto nechavam - myslim ze ma zmysel ukladat ui circle samostatne
    private void drawExistingCircles() {
        currentlyHoveredCircle = null;
        for (GridCircle circle : circles) {

            // FIX: Musíme aktualizovať uloženú pozíciu kruhu, aby sedela s novým výpočtom mriežky
            float newX = sx(circle);
            float newY = sy(circle);

            // Ak máte prístup k x/y priamo alebo cez setter:
            circle.setX(newX);
            circle.setY(newY);

            // Teraz skontrolujeme hover s aktualizovanou pozíciou
            boolean isHovered = circle.updateIfHovered(mouseX, mouseY, isTouched, ctrl.getTurnManager());

            if (isHovered) {
                currentlyHoveredCircle = circle;
            }

            drawer.setColor(circle.getColor());

            drawer.filledCircle(
                newX,
                newY,
                circle.isEnlarged() ? circle.getEnlargedRadius() : circle.getBaseRadius());
        }
    }


    private void drawSausages() {
        // Draw all connections
        drawer.setDefaultLineWidth(enlargedCircleRadius);

        for (Sausage s : ctrl.getGameBoard().getSausages()) {
            List<Point> points = s.getThreePoints();

            drawer.setColor(s.getPlayer().getColor());

            drawer.line(
                    colToX(points.get(0).getX(), points.get(0).getY()),
                    rowToY(points.get(0).getX(), points.get(0).getY()),
                    colToX(points.get(1).getX(), points.get(1).getY()),
                    rowToY(points.get(1).getX(), points.get(1).getY()));
            drawer.line(
                    colToX(points.get(1).getX(), points.get(1).getY()),
                    rowToY(points.get(1).getX(), points.get(1).getY()),
                    colToX(points.get(2).getX(), points.get(2).getY()),
                    rowToY(points.get(2).getX(), points.get(2).getY()));

            for (Point p : points) {
                drawer.filledCircle(
                        colToX(p.getX(), p.getY()),
                        rowToY(p.getX(), p.getY()),
                        baseCircleRadius);
            }

        }
    }

    private void drawCircleHints() {
        // draw the highlight around circles
        GridCircle anchor = null;
        if (firstCircle != null && secondCircle == null) {
            anchor = firstCircle;
        } else if (secondCircle != null && thirdCircle == null) {
            anchor = secondCircle;
        }
        if (anchor != null) {
            drawer.setColor(ctrl.getTurnManager().getCurrentPlayer().getColor());
            drawer.setDefaultLineWidth(4f);
            for (GridCircle circle : circles) {
                if (circle != firstCircle && circle != secondCircle && !circle.getIsConnected() &&
                        ValidatorUtil.areNeigbours(new Point(anchor.getCol(), anchor.getRow()), new Point(circle.getCol(), circle.getRow())) &&
                        ValidatorUtil.haveNoIntersectionInGrid(
                                new Point(anchor.getCol(), anchor.getRow()),
                                new Point(circle.getCol(), circle.getRow()), ctrl.getGameBoard().getGrid())) {
                    drawer.circle(sx(circle), sy(circle), circle.getBaseRadius() + 6f);
                }
            }
        }
    }

    private void updateGridMetrics() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
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
