package com.nimonscooked.renderer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.nimonscooked.manager.TextureManager;
import com.nimonscooked.model.chef.Chef;
import com.nimonscooked.model.chef.Direction;
import com.nimonscooked.map.GameMap;

public class ChefRenderer {

    private Texture texture;
    private TextureRegion[] frames;

    private static final int FRAME_SIZE = 32;
    private static final int RENDER_SIZE = 48;

    public ChefRenderer() {
        texture = TextureManager.get().getTexture("chef_base");

        TextureRegion[][] split = TextureRegion.split(texture, FRAME_SIZE, FRAME_SIZE);

        frames = new TextureRegion[8];
        int index = 0;

        for (int r = 0; r < split.length; r++) {
            for (int c = 0; c < split[r].length; c++) {
                if (index < 8) {
                    frames[index++] = split[r][c];
                }
            }
        }
    }

    public void render(SpriteBatch batch, Chef chef, GameMap map) {
        if (chef == null || chef.getPosition() == null) return;

        int row = chef.getPosition().getRow();
        int col = chef.getPosition().getCol();

        float x = col * RENDER_SIZE;
        float y = (map.getRows() - row - 1) * RENDER_SIZE;

        TextureRegion frame = getFrame(chef.getDirection());
        batch.draw(frame, x, y, RENDER_SIZE, RENDER_SIZE);
    }

    private TextureRegion getFrame(Direction d) {
        switch (d) {
            case DOWN:  return frames[0];
            case LEFT:  return frames[2];
            case RIGHT: return frames[4];
            case UP:    return frames[6];
        }
        return frames[0];
    }

    public void dispose() {
        // TextureManager yang dispose, jadi jangan dipake:
        // texture.dispose();
    }
}
