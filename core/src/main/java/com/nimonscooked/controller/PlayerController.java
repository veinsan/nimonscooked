package com.nimonscooked.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.model.entity.Chef;
import com.nimonscooked.model.map.GridMap;
import java.util.List;

public class PlayerController {
    private InputHandler input;
    private MapManager mapManager;
    private GridMap map;

    private float inputCooldown = 0f;
    private static final float MOVE_DELAY = 0.15f;
    private static final float ACTION_DELAY = 0.2f;

    // Kecepatan visual mengejar grid (Semakin tinggi semakin cepat "snap")
    private static final float VISUAL_LERP_SPEED = 15f;

    public PlayerController(InputHandler input) {
        this.input = input;
        this.mapManager = MapManager.getInstance();
        this.map = mapManager.currentMap;
    }

    public void update(float delta) {
        if (mapManager.chefs != null) {
            for (Chef c : mapManager.chefs) {
                updateChefVisuals(c, delta);
            }
        }

        if (mapManager.activeChef == null) return;

        if (inputCooldown > 0) {
            inputCooldown -= delta;
            return;
        }

        // --- INPUT LOGIC ---
        if (input.switchChef || Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            switchChef();
            inputCooldown = ACTION_DELAY;
            return;
        }

        Chef active = mapManager.activeChef;

        // Reset state interaksi kalau bergerak
        if (input.up || input.down || input.left || input.right) {
            active.isChopping = false;
        }

        if (input.up)         tryMove(active, Chef.Direction.UP);
        else if (input.down)  tryMove(active, Chef.Direction.DOWN);
        else if (input.left)  tryMove(active, Chef.Direction.LEFT);
        else if (input.right) tryMove(active, Chef.Direction.RIGHT);

        if (input.interact) {
            active.isChopping = true;
            inputCooldown = ACTION_DELAY;
        }

        if (input.pickup) {
            active.isChopping = false;
            inputCooldown = ACTION_DELAY;
        }
    }

    private void tryMove(Chef chef, Chef.Direction dir) {
        chef.direction = dir;
        chef.move(dir, map);
        inputCooldown = MOVE_DELAY;
    }

    private void switchChef() {
        List<Chef> chefs = mapManager.chefs;
        if (chefs.size() < 2) return;
        int nextIndex = (chefs.indexOf(mapManager.activeChef) + 1) % chefs.size();
        mapManager.activeChef = chefs.get(nextIndex);
    }

    private void updateChefVisuals(Chef c, float delta) {
        // 1. Update State Time (Untuk Animasi)
        // Hanya tambah waktu kalau sedang bergerak atau chopping
        // Ini trik agar pas diam, framenya tidak lari-lari
        if (c.isMoving || c.isChopping) {
            c.stateTime += delta;
        } else {
            c.stateTime = 0; // Reset frame ke awal kalau diam
        }

        // 2. Interpolasi Posisi (Visual mengejar Logic)
        c.visualPos.x += (c.position.col - c.visualPos.x) * VISUAL_LERP_SPEED * delta;
        c.visualPos.y += (c.position.row - c.visualPos.y) * VISUAL_LERP_SPEED * delta;

        // 3. Cek Jarak (Snap to Grid Logic)
        float dist = Math.abs(c.visualPos.x - c.position.col) + Math.abs(c.visualPos.y - c.position.row);

        // Kalau jarak sudah sangat dekat (< 0.05 pixel), anggap sudah sampai
        if (dist < 0.05f) {
            c.visualPos.x = c.position.col; // Snap X
            c.visualPos.y = c.position.row; // Snap Y
            c.isMoving = false; // STOP bergerak
        } else {
            c.isMoving = true; // MASIH bergerak
        }
    }
}
