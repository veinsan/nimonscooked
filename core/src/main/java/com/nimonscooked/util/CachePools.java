package com.nimonscooked.util;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class CachePools {

    private static final Pool<Vector2> vector2Pool = new Pool<Vector2>(64) {
        @Override
        protected Vector2 newObject() {
            return new Vector2();
        }
    };
    private static final Array<Vector2> usedVector2s = new Array<>();

    private static final Pool<Rectangle> rectPool = new Pool<Rectangle>(16) {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    private static final Array<Rectangle> usedRects = new Array<>();

    public static Vector2 getVector2(float x, float y) {
        Vector2 v = vector2Pool.obtain();
        v.set(x, y);
        usedVector2s.add(v);
        return v;
    }

    public static Rectangle getRectangle(float x, float y, float w, float h) {
        Rectangle r = rectPool.obtain();
        r.set(x, y, w, h);
        usedRects.add(r);
        return r;
    }

    public static void freeAll() {
        vector2Pool.freeAll(usedVector2s);
        usedVector2s.clear();

        rectPool.freeAll(usedRects);
        usedRects.clear();
    }
}