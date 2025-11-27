package com.nimonscooked.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.manager.ResourceManager;

public class MainMenuScreen extends ScreenAdapter {

    private Stage stage;
    private Skin skin;
    private Texture background;

    // State Machine untuk Menu
    private enum MenuState { LANDING, MAIN_MENU, OPTIONS, HOW_TO_PLAY }
    private MenuState currentState = MenuState.LANDING;

    // UI Tables (Container untuk setiap halaman menu)
    private Table landingTable;
    private Table mainMenuTable;
    private Table optionsTable;
    private Table howToPlayTable;

    public MainMenuScreen() {
        // Menggunakan FitViewport agar UI tetap proporsional di resolusi apapun
        stage = new Stage(new FitViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT));
        skin = ResourceManager.getInstance().getSkin();
        background = ResourceManager.getInstance().getTexture("ui/title_bg.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Play Music
        AudioManager.getInstance().playMusic("music/bgm_menu.mp3");

        // Build UI
        rebuildUI();
    }

    private void rebuildUI() {
        stage.clear();

        // Buat semua tabel halaman
        buildLandingTable();
        buildMainMenuTable();
        buildOptionsTable();
        buildHowToPlayTable();

        // Tampilkan sesuai state awal
        changeState(MenuState.LANDING);
    }

    private void changeState(MenuState newState) {
        this.currentState = newState;

        // Sembunyikan semua dulu
        landingTable.setVisible(false);
        mainMenuTable.setVisible(false);
        optionsTable.setVisible(false);
        howToPlayTable.setVisible(false);

        // Tampilkan yang aktif
        switch (newState) {
            case LANDING: landingTable.setVisible(true); break;
            case MAIN_MENU: mainMenuTable.setVisible(true); break;
            case OPTIONS: optionsTable.setVisible(true); break;
            case HOW_TO_PLAY: howToPlayTable.setVisible(true); break;
        }
    }

    // --- 1. LANDING PAGE ---
    private void buildLandingTable() {
        landingTable = new Table();
        landingTable.setFillParent(true);

        Label pressKeyLabel = new Label("PRESS 'ENTER' TO CONTINUE", skin);
        // Efek kedip-kedip manual di render() nanti atau pakai Action

        landingTable.add(pressKeyLabel).expand().bottom().padBottom(100);
        stage.addActor(landingTable);
    }

    // --- 2. MAIN MENU ---
    private void buildMainMenuTable() {
        mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);
        mainMenuTable.center();

        TextButton btnStart = new TextButton("START GAME", skin);
        TextButton btnHowTo = new TextButton("HOW TO PLAY", skin);
        TextButton btnOption = new TextButton("OPTIONS", skin);
        TextButton btnExit = new TextButton("EXIT", skin);

        // Listeners
        btnStart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                NimonscookedGame.instance.setScreen(new GameScreen());
            }
        });

        btnHowTo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeState(MenuState.HOW_TO_PLAY);
            }
        });

        btnOption.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeState(MenuState.OPTIONS);
            }
        });

        btnExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Layout
        mainMenuTable.add(btnStart).width(300).height(60).pad(10).row();
        mainMenuTable.add(btnHowTo).width(300).height(60).pad(10).row();
        mainMenuTable.add(btnOption).width(300).height(60).pad(10).row();
        mainMenuTable.add(btnExit).width(300).height(60).pad(10).row();

        stage.addActor(mainMenuTable);
    }

    // --- 3. OPTIONS MENU ---
    private void buildOptionsTable() {
        optionsTable = new Table();
        optionsTable.setFillParent(true);
        optionsTable.center();

        Label titleLabel = new Label("OPTIONS", skin);
        titleLabel.setFontScale(1.5f);

        // --- VIDEO SECTION ---
        Label videoLabel = new Label("--- VIDEO ---", skin);
        CheckBox fullScreenCheck = new CheckBox(" Fullscreen Mode", skin);
        fullScreenCheck.setChecked(Gdx.graphics.isFullscreen());

        fullScreenCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean isFull = fullScreenCheck.isChecked();
                if (isFull) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
                }
            }
        });

        // --- AUDIO SECTION ---
        Label audioLabel = new Label("--- AUDIO ---", skin);

        // Master Volume
        Label lblMaster = new Label("Master Volume", skin);
        final Slider sldMaster = new Slider(0, 1, 0.1f, false, skin);
        sldMaster.setValue(AudioManager.getInstance().getMasterVolume());
        sldMaster.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.getInstance().setMasterVolume(sldMaster.getValue());
            }
        });

        // Music Volume
        Label lblMusic = new Label("Music Volume", skin);
        final Slider sldMusic = new Slider(0, 1, 0.1f, false, skin);
        sldMusic.setValue(AudioManager.getInstance().getMusicVolume());
        sldMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.getInstance().setMusicVolume(sldMusic.getValue());
            }
        });

        // SFX Volume
        Label lblSfx = new Label("SFX Volume", skin);
        final Slider sldSfx = new Slider(0, 1, 0.1f, false, skin);
        sldSfx.setValue(AudioManager.getInstance().getSfxVolume());
        sldSfx.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.getInstance().setSfxVolume(sldSfx.getValue());
            }
        });

        TextButton btnBack = new TextButton("BACK", skin);
        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeState(MenuState.MAIN_MENU);
            }
        });

        // Layout Options
        optionsTable.add(titleLabel).colspan(2).padBottom(30).row();

        optionsTable.add(videoLabel).colspan(2).padBottom(10).row();
        optionsTable.add(fullScreenCheck).colspan(2).padBottom(20).row();

        optionsTable.add(audioLabel).colspan(2).padBottom(10).row();

        optionsTable.add(lblMaster).left();
        optionsTable.add(sldMaster).width(200).row();

        optionsTable.add(lblMusic).left();
        optionsTable.add(sldMusic).width(200).row();

        optionsTable.add(lblSfx).left();
        optionsTable.add(sldSfx).width(200).padBottom(30).row();

        optionsTable.add(btnBack).colspan(2).width(200).height(50);

        stage.addActor(optionsTable);
    }

    // --- 4. HOW TO PLAY ---
    private void buildHowToPlayTable() {
        howToPlayTable = new Table();
        howToPlayTable.setFillParent(true);

        Label title = new Label("HOW TO PLAY", skin);
        title.setFontScale(1.5f);

        String instructions =
            "CONTROLS:\n" +
                "WASD : Move Chef\n" +
                "V    : Interact / Chop / Cook\n" +
                "C    : Pick Up / Drop\n" +
                "X    : Switch Chef\n\n" +
                "GOAL:\n" +
                "Prepare ingredients, cook them, assemble dishes,\n" +
                "and serve them to the customers before time runs out!";

        Label content = new Label(instructions, skin);
        content.setAlignment(Align.center);

        TextButton btnBack = new TextButton("BACK", skin);
        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeState(MenuState.MAIN_MENU);
            }
        });

        howToPlayTable.add(title).padBottom(30).row();
        howToPlayTable.add(content).padBottom(30).row();
        howToPlayTable.add(btnBack).width(200).height(50);

        stage.addActor(howToPlayTable);
    }

    @Override
    public void render(float delta) {
        // Logic Input Khusus Landing Page
        if (currentState == MenuState.LANDING) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                // Efek suara konfirmasi (opsional)
                AudioManager.getInstance().playSound("sfx/delivery_success.wav");
                changeState(MenuState.MAIN_MENU);
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw Background
        stage.getBatch().begin();
        if (background != null) {
            stage.getBatch().draw(background, 0, 0, stage.getWidth(), stage.getHeight());
        }
        stage.getBatch().end();

        // Draw UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
