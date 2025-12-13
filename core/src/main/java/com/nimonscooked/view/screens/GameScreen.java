package com.nimonscooked.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.controller.InputHandler;
import com.nimonscooked.controller.PlayerController;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.manager.GameManager;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.manager.ResourceManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.view.renderer.WorldRenderer;
import com.nimonscooked.view.renderer.HudRenderer;

public class GameScreen extends ScreenAdapter {
    private WorldRenderer worldRenderer;
    private HudRenderer hudRenderer;

    private InputHandler inputHandler;
    private PlayerController playerController;

    private OrthographicCamera camera;
    private Viewport viewport;

    private boolean isPaused = false;
    private Stage pauseStage;
    private Table pauseMenu;
    private BitmapFont customFont;

    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        
        viewport = new ExtendViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, camera);
        viewport.apply();
        
        camera.zoom = 0.6f;

        worldRenderer = new WorldRenderer();
        hudRenderer = new HudRenderer(NimonscookedGame.instance.batch);

        inputHandler = new InputHandler();
        playerController = new PlayerController(inputHandler);

        Gdx.input.setInputProcessor(inputHandler);

        centerCameraOnActiveChef();

        AudioManager.getInstance().playMusic("music/bgm_game.mp3");

        pauseStage = new Stage(new ScreenViewport());
        customFont = ResourceManager.getInstance().getCustomFont();
        buildPauseMenu();
    }

    private void buildPauseMenu() {
        pauseMenu = new Table();
        pauseMenu.setFillParent(true);

        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.8f);
        bgPixmap.fill();
        Texture bgTexture = new Texture(bgPixmap);
        bgPixmap.dispose();
        pauseMenu.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));

        Label.LabelStyle titleStyle = new Label.LabelStyle(customFont, Color.WHITE);
        Label titleLabel = new Label("PAUSED", titleStyle);
        titleLabel.setFontScale(3.0f);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = customFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = new Color(1f, 0.9f, 0.4f, 1f);

        TextButton btnResume = new TextButton("Resume", buttonStyle);
        TextButton btnRestart = new TextButton("Restart Level", buttonStyle);
        TextButton btnMainMenu = new TextButton("Main Menu", buttonStyle);

        btnResume.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().playSound("sfx/click.mp3");
                resumeGame();
            }
        });

        btnRestart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().playSound("sfx/click.mp3");
                restartLevel();
            }
        });

        btnMainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().playSound("sfx/click.mp3");
                backToMainMenu();
            }
        });

        pauseMenu.add(titleLabel).padBottom(50).row();
        pauseMenu.add(btnResume).width(400).height(70).pad(15).row();
        pauseMenu.add(btnRestart).width(400).height(70).pad(15).row();
        pauseMenu.add(btnMainMenu).width(400).height(70).pad(15).row();

        pauseStage.addActor(pauseMenu);
        pauseMenu.setVisible(false);
    }

    private void pauseGame() {
        isPaused = true;
        GameManager.getInstance().pauseGame();
        pauseMenu.setVisible(true);
        Gdx.input.setInputProcessor(pauseStage);
    }

    private void resumeGame() {
        isPaused = false;
        GameManager.getInstance().resumeGame();
        pauseMenu.setVisible(false);
        Gdx.input.setInputProcessor(inputHandler);
    }

    private void restartLevel() {
        GameManager.getInstance().reset();
        MapManager.getInstance().reloadCurrentMap();
        NimonscookedGame.instance.setScreen(new GameScreen());
    }

    private void backToMainMenu() {
        GameManager.getInstance().reset();
        MapManager.getInstance().dispose();
        NimonscookedGame.instance.setScreen(new MainMenuScreen());
    }

    private void centerCameraOnActiveChef() {
        Chef activeChef = MapManager.getInstance().activeChef;
        if (activeChef != null) {
            camera.position.set(
                activeChef.visualPos.x * GameConfig.TILE_SIZE, 
                activeChef.visualPos.y * GameConfig.TILE_SIZE, 
                0
            );
        } else {
            int mapWidth = MapManager.getInstance().currentMap.getWidth() * GameConfig.TILE_SIZE;
            int mapHeight = MapManager.getInstance().currentMap.getHeight() * GameConfig.TILE_SIZE;
            camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        }
        camera.update();
    }

    private void updateCamera(float delta) {
        Chef activeChef = MapManager.getInstance().activeChef;
        if (activeChef == null) return;

        float targetX = activeChef.visualPos.x * GameConfig.TILE_SIZE;
        float targetY = activeChef.visualPos.y * GameConfig.TILE_SIZE;

        float lerpSpeed = GameConfig.CAMERA_LERP_SPEED * delta;
        camera.position.x += (targetX - camera.position.x) * lerpSpeed;
        camera.position.y += (targetY - camera.position.y) * lerpSpeed;

        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudRenderer.resize(width, height);
        pauseStage.getViewport().update(width, height, true);
        centerCameraOnActiveChef();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (isPaused) {
                resumeGame();
            } else {
                pauseGame();
            }
        }

        if (!isPaused) {
            playerController.update(delta);
            GameManager.getInstance().update(delta);
            updateCamera(delta);
        }

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        NimonscookedGame.instance.batch.setProjectionMatrix(camera.combined);
        
        NimonscookedGame.instance.batch.begin();
        worldRenderer.render(NimonscookedGame.instance.batch);
        NimonscookedGame.instance.batch.end();

        hudRenderer.render();

        if (isPaused) {
            pauseStage.act(delta);
            pauseStage.draw();
        }

        com.nimonscooked.util.CachePools.freeAll();
    }

    @Override
    public void dispose() {
        if (worldRenderer != null) worldRenderer.dispose();
        if (hudRenderer != null) hudRenderer.dispose();
        if (pauseStage != null) pauseStage.dispose();
    }
}