package com.nimonscooked.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nimonscooked.NimonscookedGame;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.AudioManager;
import com.nimonscooked.manager.ResourceManager;

public class MainMenuScreen extends ScreenAdapter {

    private Stage stage;
    private Skin skin;
    private BitmapFont customFont;

    private enum MenuState { LANDING, MAIN_MENU, OPTIONS, HOW_TO_PLAY, TRANSITION }
    private MenuState currentState = MenuState.LANDING;

    private Table landingTable;
    private Table mainMenuTable;
    private Table optionsTable;
    private Table howToPlayTable;
    private Label pressKeyLabel;
    private Image backgroundImage;
    
    // Overlay untuk blur/dim effect
    private Image overlayDim;

    private TextureRegionDrawable grayPanelDrawable;
    private TextureRegionDrawable darkOverlayDrawable;
    private TextureRegionDrawable lineDrawable;

    private float landingBlinkTimer = 0f;
    private static final float BLINK_INTERVAL = 0.6f;
    private float fadeAlpha = 0f;
    private boolean fadingIn = true;

    // Transition variables
    private boolean isTransitioning = false;
    private float transitionAlpha = 0f;
    private float transitionTimer = 0f;
    private static final float TRANSITION_DURATION = 2.0f;
    private Label transitionLabel;

    private static final String SFX_CLICK = "sfx/click.mp3";
    private static final String SFX_HOVER = "sfx/hover.mp3";
    private static final String SFX_GAME_START = "sfx/delivery_success.wav";
    private static final String SFX_MENU_OPEN = "sfx/click.mp3";

    private long lastClickTime = 0;
    private static final long CLICK_BLOCK_DURATION = 200;

    public MainMenuScreen() {
        stage = new Stage(new ScreenViewport());
        skin = ResourceManager.getInstance().getSkin();
        customFont = ResourceManager.getInstance().getCustomFont();
        generateStyles();
    }

    private void generateStyles() {
        // Panel dengan transparansi lebih gelap untuk readability
        grayPanelDrawable = createColorDrawable(new Color(0.1f, 0.1f, 0.15f, 0.95f));
        
        // Dark overlay untuk dim background
        darkOverlayDrawable = createColorDrawable(new Color(0, 0, 0, 0.7f));
        
        // Separator line (gold)
        lineDrawable = createColorDrawable(new Color(0.9f, 0.8f, 0.5f, 0.5f));
    }

    private TextureRegionDrawable createColorDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        
        // IMPORTANT: Memastikan kursor terlihat (tidak tersembunyi)
        Gdx.input.setCursorCatched(false);
        
        AudioManager.getInstance().playMusic("music/bgm_menu.mp3");

        rebuildUI();

        fadeAlpha = 0f;
        fadingIn = true;
        isTransitioning = false;
        changeState(MenuState.LANDING);
    }

    private void rebuildUI() {
        stage.clear();

        // ============ RESPONSIVE BACKGROUND ============
        Texture bgTexture = ResourceManager.getInstance().getTexture("ui/title_bg.png");
        if (bgTexture != null) {
            backgroundImage = new Image(bgTexture);
            backgroundImage.setFillParent(true);
            backgroundImage.setScaling(Scaling.fill);
            stage.addActor(backgroundImage);
        }

        // ============ DARK OVERLAY (hidden by default) ============
        overlayDim = new Image(darkOverlayDrawable);
        overlayDim.setFillParent(true);
        overlayDim.setVisible(false);
        overlayDim.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        stage.addActor(overlayDim);

        buildLandingTable();
        buildMainMenuTable();
        buildOptionsTable();
        buildHowToPlayTable();
        buildTransitionScreen();
    }

    private void changeState(MenuState newState) {
        this.currentState = newState;

        // Hide all menus
        if (landingTable != null) landingTable.setVisible(false);
        if (mainMenuTable != null) mainMenuTable.setVisible(false);
        if (optionsTable != null) optionsTable.setVisible(false);
        if (howToPlayTable != null) howToPlayTable.setVisible(false);

        // Hide overlay by default
        overlayDim.setVisible(false);

        switch (newState) {
            case LANDING:
                landingTable.setVisible(true);
                landingBlinkTimer = 0f;
                if (pressKeyLabel != null) pressKeyLabel.setVisible(true);
                break;
            case MAIN_MENU:
                mainMenuTable.setVisible(true);
                break;
            case OPTIONS:
                showMenuPanel(optionsTable);
                break;
            case HOW_TO_PLAY:
                showMenuPanel(howToPlayTable);
                break;
            case TRANSITION:
                break;
        }
    }

    private void showMenuPanel(Table panel) {
        overlayDim.setVisible(true);
        overlayDim.getColor().a = 0f;
        overlayDim.addAction(Actions.fadeIn(0.3f));
        
        panel.setVisible(true);
        panel.setScale(0.9f);
        panel.getColor().a = 0f;
        panel.addAction(Actions.parallel(
            Actions.fadeIn(0.3f),
            Actions.scaleTo(1f, 1f, 0.3f, Interpolation.swingOut)
        ));
        
        AudioManager.getInstance().playSound(SFX_MENU_OPEN);
    }

    private void hideMenuPanel(Table panel, Runnable onComplete) {
        overlayDim.addAction(Actions.sequence(
            Actions.fadeOut(0.25f),
            Actions.run(() -> overlayDim.setVisible(false))
        ));
        
        panel.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeOut(0.25f),
                Actions.scaleTo(0.9f, 0.9f, 0.25f, Interpolation.swingIn)
            ),
            Actions.run(() -> {
                panel.setVisible(false);
                if (onComplete != null) onComplete.run();
            })
        ));
    }

    private ClickListener createButtonListener(final Runnable onAction) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().playSound(SFX_CLICK);
                lastClickTime = TimeUtils.millis();
                if (onAction != null) onAction.run();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    if (TimeUtils.millis() - lastClickTime > CLICK_BLOCK_DURATION) {
                        AudioManager.getInstance().playSound(SFX_HOVER, 0.6f);
                    }
                    if (event.getListenerActor() instanceof TextButton) {
                        TextButton btn = (TextButton) event.getListenerActor();
                        btn.getLabel().setColor(new Color(1f, 0.9f, 0.4f, 1f));
                        btn.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f, Interpolation.pow2Out));
                    }
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    if (event.getListenerActor() instanceof TextButton) {
                        TextButton btn = (TextButton) event.getListenerActor();
                        btn.getLabel().setColor(Color.WHITE);
                        btn.addAction(Actions.scaleTo(1f, 1f, 0.1f, Interpolation.pow2Out));
                    }
                }
            }
        };
    }

    private Image createSeparator() {
        Image sep = new Image(lineDrawable);
        sep.setHeight(3);
        return sep;
    }

    private void startTransition() {
        AudioManager.getInstance().stopMusic();
        AudioManager.getInstance().playSound(SFX_GAME_START);
        
        changeState(MenuState.TRANSITION);
        isTransitioning = true;
        transitionAlpha = 0f;
        transitionTimer = 0f;
    }

    private void buildTransitionScreen() {
        Label.LabelStyle style = new Label.LabelStyle(customFont, Color.WHITE);
        transitionLabel = new Label("LOADING...", style);
        transitionLabel.setFontScale(2.5f);
        transitionLabel.setAlignment(Align.center);
        transitionLabel.setPosition(
            (stage.getWidth() - transitionLabel.getPrefWidth() * 2.5f) / 2f,
            stage.getHeight() / 2f
        );
        transitionLabel.setVisible(false);
        stage.addActor(transitionLabel);
    }

    private void buildLandingTable() {
        landingTable = new Table();
        landingTable.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(customFont, Color.WHITE);
        Label gameTitle = new Label("NIMONSCOOKED", titleStyle);
        gameTitle.setFontScale(3.5f);
        gameTitle.setAlignment(Align.center);

        Label.LabelStyle subtitleStyle = new Label.LabelStyle(customFont, new Color(0.9f, 0.8f, 0.5f, 1f));
        Label subtitle = new Label("Champions of the Stack", subtitleStyle);
        subtitle.setFontScale(1.3f);
        subtitle.setAlignment(Align.center);

        Label.LabelStyle promptStyle = new Label.LabelStyle(customFont, Color.WHITE);
        pressKeyLabel = new Label("PRESS ENTER TO CONTINUE", promptStyle);
        pressKeyLabel.setFontScale(1.1f);
        pressKeyLabel.setAlignment(Align.center);

        landingTable.add(gameTitle).expandY().padTop(200).row();
        landingTable.add(subtitle).padTop(10).row();
        landingTable.add().expand().row();
        landingTable.add(pressKeyLabel).bottom().padBottom(120);

        stage.addActor(landingTable);
    }

    private void buildMainMenuTable() {
        mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(customFont, Color.WHITE);
        Label gameTitle = new Label("NIMONSCOOKED", titleStyle);
        gameTitle.setFontScale(2.8f);
        gameTitle.setAlignment(Align.center);

        TextButton.TextButtonStyle buttonStyle = createCustomButtonStyle();

        TextButton btnStart = new TextButton("New Game", buttonStyle);
        TextButton btnHowTo = new TextButton("How to Play", buttonStyle);
        TextButton btnOption = new TextButton("Settings", buttonStyle);
        TextButton btnExit = new TextButton("Exit", buttonStyle);

        btnStart.addListener(createButtonListener(this::startTransition));
        btnHowTo.addListener(createButtonListener(() -> changeState(MenuState.HOW_TO_PLAY)));
        btnOption.addListener(createButtonListener(() -> changeState(MenuState.OPTIONS)));
        btnExit.addListener(createButtonListener(() -> Gdx.app.exit()));

        mainMenuTable.add(gameTitle).padTop(150).padBottom(100).row();
        mainMenuTable.add(btnStart).width(400).height(70).pad(12).row();
        mainMenuTable.add(btnHowTo).width(400).height(70).pad(12).row();
        mainMenuTable.add(btnOption).width(400).height(70).pad(12).row();
        mainMenuTable.add(btnExit).width(400).height(70).pad(12).padTop(30).row();

        stage.addActor(mainMenuTable);
    }

    private void buildOptionsTable() {
        optionsTable = new Table();
        optionsTable.setSize(1000, 750);
        optionsTable.setPosition(
            (stage.getWidth() - 1000) / 2f,
            (stage.getHeight() - 750) / 2f
        );
        optionsTable.setBackground(grayPanelDrawable);
        optionsTable.pad(60);

        Label.LabelStyle titleStyle = new Label.LabelStyle(customFont, Color.WHITE);
        Label titleLabel = new Label("SETTINGS", titleStyle);
        titleLabel.setFontScale(3.0f);
        titleLabel.setAlignment(Align.center);

        Label.LabelStyle sectionStyle = new Label.LabelStyle(customFont, Color.WHITE);
        
        Label videoLabel = new Label("VIDEO", sectionStyle);
        videoLabel.setFontScale(1.8f);
        
        CheckBox fullScreenCheck = new CheckBox("  Fullscreen Mode", skin);
        fullScreenCheck.getLabel().setFontScale(1.4f);
        fullScreenCheck.getLabel().setColor(Color.WHITE);
        
        // Cek status saat ini (Kalau mulai windowed, ini akan false)
        fullScreenCheck.setChecked(Gdx.graphics.isFullscreen());
        
        fullScreenCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.getInstance().playSound(SFX_CLICK);
                if (fullScreenCheck.isChecked()) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    // Gunakan konfigurasi default game saat kembali ke windowed
                    Gdx.graphics.setWindowedMode(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
                }
                resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        });

        Label audioLabel = new Label("AUDIO", sectionStyle);
        audioLabel.setFontScale(1.8f);

        Label lblMaster = new Label("Master Volume", skin);
        lblMaster.setFontScale(1.4f);
        lblMaster.setColor(Color.WHITE);
        Slider sldMaster = new Slider(0, 1, 0.05f, false, skin);
        sldMaster.setValue(AudioManager.getInstance().getMasterVolume());
        sldMaster.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.getInstance().setMasterVolume(sldMaster.getValue());
            }
        });

        Label lblMusic = new Label("Music Volume", skin);
        lblMusic.setFontScale(1.4f);
        lblMusic.setColor(Color.WHITE);
        Slider sldMusic = new Slider(0, 1, 0.05f, false, skin);
        sldMusic.setValue(AudioManager.getInstance().getMusicVolume());
        sldMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.getInstance().setMusicVolume(sldMusic.getValue());
            }
        });

        Label lblSfx = new Label("SFX Volume", skin);
        lblSfx.setFontScale(1.4f);
        lblSfx.setColor(Color.WHITE);
        Slider sldSfx = new Slider(0, 1, 0.05f, false, skin);
        sldSfx.setValue(AudioManager.getInstance().getSfxVolume());
        sldSfx.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.getInstance().setSfxVolume(sldSfx.getValue());
            }
        });

        TextButton btnBack = new TextButton("Back", createCustomButtonStyle());
        btnBack.addListener(createButtonListener(() -> {
            hideMenuPanel(optionsTable, () -> changeState(MenuState.MAIN_MENU));
        }));

        optionsTable.defaults().padBottom(15);
        
        optionsTable.add(titleLabel).colspan(3).padBottom(35).row();
        optionsTable.add(createSeparator()).colspan(3).growX().padBottom(40).row();

        optionsTable.add(videoLabel).colspan(3).left().padBottom(20).row();
        optionsTable.add(fullScreenCheck).colspan(3).left().padLeft(30).padBottom(30).row();

        optionsTable.add(createSeparator()).colspan(3).growX().padBottom(40).row();

        optionsTable.add(audioLabel).colspan(3).left().padBottom(20).row();

        optionsTable.add(lblMaster).left().padLeft(30).width(300).padBottom(20);
        optionsTable.add(sldMaster).fillX().height(40).colspan(2).padBottom(20).row();

        optionsTable.add(lblMusic).left().padLeft(30).width(300).padBottom(20);
        optionsTable.add(sldMusic).fillX().height(40).colspan(2).padBottom(20).row();

        optionsTable.add(lblSfx).left().padLeft(30).width(300).padBottom(20);
        optionsTable.add(sldSfx).fillX().height(40).colspan(2).padBottom(50).row();

        optionsTable.add(createSeparator()).colspan(3).growX().padBottom(40).row();

        optionsTable.add(btnBack).colspan(3).width(300).height(70);

        stage.addActor(optionsTable);
    }

    private void buildHowToPlayTable() {
        howToPlayTable = new Table();
        howToPlayTable.setSize(1100, 800);
        howToPlayTable.setPosition(
            (stage.getWidth() - 1100) / 2f,
            (stage.getHeight() - 800) / 2f
        );
        howToPlayTable.setBackground(grayPanelDrawable);
        howToPlayTable.pad(60);

        Label.LabelStyle titleStyle = new Label.LabelStyle(customFont, Color.WHITE);
        Label title = new Label("HOW TO PLAY", titleStyle);
        title.setFontScale(3.0f);

        String instructions =
            "CONTROLS\n" +
            "WASD / Arrow Keys  -  Move Chef\n" +
            "V / E  -  Interact / Chop / Cook / Wash\n" +
            "F / K  -  Throw Item\n" +
            "SHIFT + Direction  -  Dash\n" +
            "X / TAB  -  Switch Chef\n\n" +
            "OBJECTIVE\n" +
            "Prepare ingredients, cook them perfectly,\n" +
            "assemble delicious dishes, and serve them!";

        Label.LabelStyle contentStyle = new Label.LabelStyle(customFont, Color.WHITE);
        Label content = new Label(instructions, contentStyle);
        content.setAlignment(Align.center);
        content.setFontScale(1.3f);

        TextButton btnBack = new TextButton("Back", createCustomButtonStyle());
        btnBack.addListener(createButtonListener(() -> {
            hideMenuPanel(howToPlayTable, () -> changeState(MenuState.MAIN_MENU));
        }));

        howToPlayTable.add(title).padBottom(30).row();
        howToPlayTable.add(createSeparator()).growX().padBottom(40).row();
        howToPlayTable.add(content).expandY().padBottom(50).row();
        howToPlayTable.add(createSeparator()).growX().padBottom(40).row();
        howToPlayTable.add(btnBack).width(300).height(70);

        stage.addActor(howToPlayTable);
    }

    private TextButton.TextButtonStyle createCustomButtonStyle() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = customFont;
        style.fontColor = Color.WHITE;
        style.overFontColor = new Color(1f, 0.9f, 0.4f, 1f);
        style.downFontColor = new Color(0.8f, 0.7f, 0.3f, 1f);
        
        if (skin.has("default", TextButton.TextButtonStyle.class)) {
            TextButton.TextButtonStyle skinStyle = skin.get(TextButton.TextButtonStyle.class);
            style.up = skinStyle.up;
            style.down = skinStyle.down;
            style.over = skinStyle.over;
        }
        return style;
    }

    @Override
    public void render(float delta) {
        if (fadingIn && fadeAlpha < 1f) {
            fadeAlpha += delta * 1.5f;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                fadingIn = false;
            }
            stage.getRoot().setColor(1, 1, 1, fadeAlpha);
        }

        if (currentState == MenuState.LANDING) {
            landingBlinkTimer += delta;
            if (landingBlinkTimer >= BLINK_INTERVAL) {
                landingBlinkTimer = 0f;
                if (pressKeyLabel != null) {
                    pressKeyLabel.setVisible(!pressKeyLabel.isVisible());
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                AudioManager.getInstance().playSound(SFX_GAME_START);
                changeState(MenuState.MAIN_MENU);
            }
        }

        if (isTransitioning) {
            transitionTimer += delta;
            
            if (transitionTimer < TRANSITION_DURATION / 2f) {
                transitionAlpha = transitionTimer / (TRANSITION_DURATION / 2f);
                transitionLabel.setVisible(true);
                transitionLabel.getColor().a = transitionAlpha;
            } else if (transitionTimer < TRANSITION_DURATION) {
                transitionAlpha = 1f - ((transitionTimer - TRANSITION_DURATION / 2f) / (TRANSITION_DURATION / 2f));
                transitionLabel.getColor().a = transitionAlpha;
            } else {
                isTransitioning = false;
                transitionLabel.setVisible(false);
                NimonscookedGame.instance.setScreen(new GameScreen());
                return;
            }
            
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            
            stage.getBatch().begin();
            transitionLabel.draw(stage.getBatch(), transitionAlpha);
            stage.getBatch().end();
            
            return;
        }

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        
        // Reposition panels after resize
        if (optionsTable != null) {
            optionsTable.setPosition(
                (stage.getWidth() - 1000) / 2f,
                (stage.getHeight() - 750) / 2f
            );
        }
        
        if (howToPlayTable != null) {
            howToPlayTable.setPosition(
                (stage.getWidth() - 1100) / 2f,
                (stage.getHeight() - 800) / 2f
            );
        }
        
        if (transitionLabel != null) {
            transitionLabel.setPosition(
                (stage.getWidth() - transitionLabel.getPrefWidth() * 2.5f) / 2f,
                stage.getHeight() / 2f
            );
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}