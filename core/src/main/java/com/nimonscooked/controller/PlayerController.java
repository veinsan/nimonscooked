package com.nimonscooked.controller;

import com.badlogic.gdx.Gdx;
import com.nimonscooked.manager.MapManager;
import com.nimonscooked.model.entity.Chef; // Import Chef yang benar
import com.nimonscooked.model.map.GridMap;
import com.nimonscooked.model.station.Station;
import com.nimonscooked.controller.command.Command; // Import Command interface

import java.util.List;

public class PlayerController {
    private InputHandler inputHandler; // Ubah nama variabel biar jelas
    private MapManager mapManager;
    private GridMap map;

    private float inputCooldown = 0f;
    private static final float ACTION_DELAY = 0.2f;
    private static final float VISUAL_LERP_SPEED = 15f;

    public PlayerController(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
        this.mapManager = MapManager.getInstance();
        this.map = mapManager.currentMap;
    }

    public void update(float delta) {
        // Update referensi map jika berubah (misal loading level baru)
        if(mapManager.currentMap != map) this.map = mapManager.currentMap;

        // --- UPDATE VISUAL SEMUA CHEF ---
        if (mapManager.chefs != null) {
            for (Chef c : mapManager.chefs) {
                updateChefVisuals(c, delta);
            }
        }

        if (mapManager.activeChef == null) return;

        // --- COOLDOWN ---
        if (inputCooldown > 0) {
            inputCooldown -= delta;
        } else {
            // --- PROSES COMMAND DARI QUEUE ---
            // Ambil command dari InputHandler
            while (!inputHandler.commandQueue.isEmpty()) {
                Command cmd = inputHandler.commandQueue.poll();
                if (cmd != null) {
                    cmd.execute(mapManager.activeChef);
                    inputCooldown = 0.15f; // Set cooldown kecil antar command
                    break; // Eksekusi satu command per frame agar smooth
                }
            }
        }

        // Switch Chef (X)
        if (inputHandler.switchChefRequested) {
            switchChef();
            inputHandler.switchChefRequested = false; // Reset flag
            inputCooldown = ACTION_DELAY;
        }
    }

    private void switchChef() {
        List<Chef> chefs = mapManager.chefs;
        if (chefs.size() < 2) return;

        int currentIndex = chefs.indexOf(mapManager.activeChef);
        int nextIndex = (currentIndex + 1) % chefs.size();
        mapManager.activeChef = chefs.get(nextIndex);

        Gdx.app.log("PlayerController", "Switched to Chef #" + (nextIndex + 1));
    }

    private void updateChefVisuals(Chef c, float delta) {
        // 1. Update timer animasi
        if (c.isMoving || c.isChopping) {
            c.stateTime += delta;
        } else {
            c.stateTime = 0;
        }

        // 2. Interpolasi Posisi (Visual mengejar Grid)
        c.visualPos.x += (c.position.col - c.visualPos.x) * VISUAL_LERP_SPEED * delta;
        c.visualPos.y += (c.position.row - c.visualPos.y) * VISUAL_LERP_SPEED * delta;

        // 3. Cek apakah sudah sampai?
        float dist = Math.abs(c.visualPos.x - c.position.col) +
                     Math.abs(c.visualPos.y - c.position.row);

        if (dist < 0.05f) {
            c.visualPos.x = c.position.col;
            c.visualPos.y = c.position.row;
            c.isMoving = false;
        } else {
            c.isMoving = true;
        }
    }
}
