package com.nimonscooked.manager;

import com.nimonscooked.model.order.OrderManager;
import java.util.ArrayList;

public class GameManager {
    private static GameManager instance;
    public OrderManager orderManager;

    public static GameManager getInstance() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    private GameManager() {
        // Pass null atau list kosong dulu kalau Recipe belum siap
        this.orderManager = new OrderManager(new ArrayList<>());
    }

    public void update(float delta) {
        if (orderManager != null) orderManager.update(delta);
    }
}
