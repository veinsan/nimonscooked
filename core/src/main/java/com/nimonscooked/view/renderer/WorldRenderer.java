package com.nimonscooked.view.renderer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nimonscooked.config.GameConfig;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.manager.ResourceManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.map.Tile;

public class WorldRenderer {

    private ResourceManager resourceManager;
    private MapManager mapManager;

    // Animasi Chef
    private Animation<TextureRegion> chefIdleAnim;
    private Animation<TextureRegion> chefWalkAnim;
    private Animation<TextureRegion> chefChopAnim;

    // Texture Region Khusus (Untuk Crate/Station yang punya animasi)
//    private TextureRegion crateClosedRegion;

    // Skala Chef (Biar kelihatan besar dan gagah)
    private static final float CHEF_SCALE = 2.2f;

    public WorldRenderer() {
        this.resourceManager = ResourceManager.getInstance();
        this.mapManager = MapManager.getInstance();

        initAnimations();
//        initStationTextures();
    }

    private void initAnimations() {
        // --- CHEF ANIMATIONS ---
        Texture walkSheet = resourceManager.getTexture("chef/chef_walk.png");
        Texture idleSheet = resourceManager.getTexture("chef/chef_idle.png");
        Texture chopSheet = resourceManager.getTexture("chef/chef_chop.png");

        int FRAME_COLS = 8; // Asumsi semua spritesheet chef punya 8 frame
        int FRAME_ROWS = 1;

        chefWalkAnim = createAnimation(walkSheet, FRAME_COLS, 0.1f);
        chefIdleAnim = createAnimation(idleSheet, FRAME_COLS, 0.15f);
        chefChopAnim = createAnimation(chopSheet, FRAME_COLS, 0.08f); // Chop lebih cepat
    }

//    private void initStationTextures() {
//        // --- CRATE (Peti) ---
//        // Kita potong frame pertama saja untuk visual default (Tertutup)
//        Texture crateSheet = resourceManager.getTexture("stations/crate.png");
//        if (crateSheet != null) {
//            // Asumsi crate.png adalah strip horizontal 8 frame (seperti yang kamu kirim)
//            int frameWidth = crateSheet.getWidth() / 8;
//            int frameHeight = crateSheet.getHeight();
//
//            // Ambil frame ke-0 (Peti tertutup)
//            crateClosedRegion = new TextureRegion(crateSheet, 0, 0, frameWidth, frameHeight);
//        }
//    }

    // Helper bikin animasi biar gak copy-paste kode
    private Animation<TextureRegion> createAnimation(Texture sheet, int cols, float speed) {
        if (sheet == null) return null;
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight());
        TextureRegion[] frames = new TextureRegion[cols];
        for (int i = 0; i < cols; i++) frames[i] = tmp[0][i];
        return new Animation<>(speed, frames);
    }

    public void render(SpriteBatch batch) {
        renderMap(batch);
        renderChefs(batch);
    }

    private void renderMap(SpriteBatch batch) {
        GridMap map = mapManager.currentMap;
        if (map == null) return;

        int size = GameConfig.TILE_SIZE;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Tile tile = map.getTile(x, y);
                if (tile == null) continue;

                float drawX = x * size;
                float drawY = y * size;

                // 1. Gambar Lantai Dulu (Background)
                // Agar stasiun transparan atau potongannya gak bolong
                Texture floorTex = resourceManager.getTexture("stations/floor.png");
                if (floorTex != null) batch.draw(floorTex, drawX, drawY, size, size);

                // 2. Gambar Station di Atasnya
                Texture tex = null;
                TextureRegion region = null;

                switch (tile.getSymbol()) {
                    case 'X': tex = resourceManager.getTexture("stations/wall.png"); break;
                    case 'C': tex = resourceManager.getTexture("stations/cutting_board.png"); break;
                    case 'R': tex = resourceManager.getTexture("stations/stove.png"); break;
                    case 'A': tex = resourceManager.getTexture("stations/counter.png"); break;

                    case 'I':
                        // Khusus Crate: Pakai Region Potongan (Frame 1)
//                        region = crateClosedRegion;
                        tex = resourceManager.getTexture("stations/crate.png");
                        break;

                    case 'P': tex = resourceManager.getTexture("stations/counter.png"); break;
                    case 'S': tex = resourceManager.getTexture("stations/delivery.png"); break;
                    case 'W': tex = resourceManager.getTexture("stations/sink.png"); break;
                    case 'T': tex = resourceManager.getTexture("stations/trash.png"); break;
                    default: break; // Lantai udah digambar di atas
                }

                if (region != null) {
                    batch.draw(region, drawX, drawY, size, size);
                } else if (tex != null) {
                    batch.draw(tex, drawX, drawY, size, size);
                }
            }
        }
    }

    private void renderChefs(SpriteBatch batch) {
        if (mapManager.chefs == null) return;

        for (Chef c : mapManager.chefs) {
            TextureRegion currentFrame = null;

            // --- LOGIKA PEMILIHAN ANIMASI ---
            if (c.isChopping) {
                // Prioritas 1: Memotong (Tombol V ditekan)
                currentFrame = chefChopAnim.getKeyFrame(c.stateTime, true);
            } else if (c.isMoving) {
                // Prioritas 2: Berjalan
                currentFrame = chefWalkAnim.getKeyFrame(c.stateTime, true);
            } else {
                // Default: Diam
                currentFrame = chefIdleAnim.getKeyFrame(0);
            }

            // Warna Chef (Aktif/Pasif)
            if (c == mapManager.activeChef) {
                batch.setColor(1, 1, 1, 1); // Normal Cerah
            } else {
                batch.setColor(0.5f, 0.5f, 0.5f, 1); // Agak Gelap
            }

            // --- POSISI & SCALING ---
            float size = GameConfig.TILE_SIZE;
            float scaledSize = size * CHEF_SCALE;

            // Agar Chef tetap di tengah tile walaupun diperbesar:
            // Geser kiri dan bawah sebanyak setengah dari penambahan ukuran
            float offsetXY = (scaledSize - size) / 2f;
            float offsetYCorrection = size * 0.4f;

            float drawX = (c.getX() * size) - offsetXY;
            float drawY = (c.getY() * size) - offsetXY + offsetYCorrection;

            // Flip jika hadap kiri (Opsional, aktifkan kalau mau)
            if (currentFrame != null) {
                batch.draw(currentFrame, drawX, drawY, scaledSize, scaledSize);
            }

            batch.setColor(1, 1, 1, 1);
        }
    }
}
