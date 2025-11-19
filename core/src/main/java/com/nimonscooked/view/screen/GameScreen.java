package com.nimonscooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.nimonscooked.manager.TextureManager;
import com.nimonscooked.map.GameMap;
import com.nimonscooked.model.chef.Chef;
import com.nimonscooked.model.chef.Direction;
import com.nimonscooked.model.chef.Position;
import com.nimonscooked.controller.MapParser;
import com.nimonscooked.model.recipe.Recipe;
import com.nimonscooked.model.order.OrderManager;
import com.nimonscooked.renderer.TileRenderer;
import com.nimonscooked.renderer.ChefRenderer;
import com.nimonscooked.renderer.ItemRenderer;
import com.nimonscooked.core.InteractionSystem;

import java.util.List;

public class GameScreen extends ScreenAdapter {

    private SpriteBatch batch;
    private OrthographicCamera camera;

    private TileRenderer tileRenderer;
    private ChefRenderer chefRenderer;
    private ItemRenderer itemRenderer;

    private GameMap map;
    private Chef chef1, chef2;
    private Chef activeChef;

    private final List<Recipe> menu;
    private OrderManager orderManager;

    private float moveTimer = 0f;
    private float interactTimer = 0f;
    private static final float MOVE_DELAY = 0.15f;
    private static final float INTERACT_DELAY = 0.3f;

    public GameScreen(List<Recipe> menu) {
        this.menu = menu;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        loadTextures();

        chef1 = new Chef("chef1", "Chef Kebin", new Position(2, 4));
        chef2 = new Chef("chef2", "Chef Stewart", new Position(6, 10));
        activeChef = chef1;

        map = MapParser.loadTypeCMap("maps/map_c.txt", chef1, chef2, menu);

        orderManager = new OrderManager(menu);
        orderManager.addRandomOrder();
        orderManager.addRandomOrder();

        tileRenderer = new TileRenderer();
        chefRenderer = new ChefRenderer();
        itemRenderer = new ItemRenderer();
    }

    private void loadTextures() {
        TextureManager tm = TextureManager.get();
        tm.load("tileset", "tiles/Interiors_free_16x16.png");
        tm.load("chef_base", "chef/base_walk_strip8.png");
        tm.load("bread", "item/bread.png");
        tm.load("meat_raw", "item/meat_raw.png");
        tm.load("meat_cooked", "item/meat_cooked.png");
        tm.load("tomato", "item/tomato.png");
        tm.load("tomato_chopped", "item/tomato_chopped.png");
        tm.load("lettuce", "item/lettuce.png");
        tm.load("lettuce_chopped", "item/lettuce_chopped.png");
        tm.load("plate", "item/plate.png");
        tm.load("frying_pan", "item/frying_pan.png");
        tm.load("cutting_board", "item/cutting_board.png");
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        tileRenderer.render(batch, map);
        chefRenderer.render(batch, chef1, map);
        chefRenderer.render(batch, chef2, map);

        renderItemInHand(chef1);
        renderItemInHand(chef2);

        batch.end();
    }

    private void renderItemInHand(Chef chef) {
        if (chef.getHeldItem() == null) return;

        float x = chef.getPosition().getCol() * 48 + 24;
        float y = (map.getRows() - chef.getPosition().getRow() - 1) * 48 + 32;

        itemRenderer.render(batch, chef.getHeldItem(), x, y);
    }

    private void update(float delta) {
        moveTimer += delta;
        interactTimer += delta;
        handleInput();
    }

    private void handleInput() {
        if (moveTimer >= MOVE_DELAY) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                activeChef.move(Direction.UP, map);
                moveTimer = 0;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                activeChef.move(Direction.DOWN, map);
                moveTimer = 0;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                activeChef.move(Direction.LEFT, map);
                moveTimer = 0;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                activeChef.move(Direction.RIGHT, map);
                moveTimer = 0;
            }
        }

        if (interactTimer >= INTERACT_DELAY) {
            if (Gdx.input.isKeyPressed(Input.Keys.V)) {
                InteractionSystem.interact(activeChef, map, orderManager);
                interactTimer = 0;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.C)) {
                if (!InteractionSystem.pickFromFloor(activeChef, map)) {
                    InteractionSystem.dropToFloor(activeChef, map);
                }
                interactTimer = 0;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.X)) {
                switchChef();
                interactTimer = 0;
            }
        }
    }

    private void switchChef() {
        activeChef = (activeChef == chef1) ? chef2 : chef1;
        System.out.println("Switched to: " + activeChef.getName());
    }

    @Override
    public void dispose() {
        batch.dispose();
        tileRenderer.dispose();
        chefRenderer.dispose();
        itemRenderer.dispose();
    }
}