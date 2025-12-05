package com.nimonscooked.util;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class CachePools {

    private static final int INITIAL_CAPACITY = 64;
    private static final int MAX_CAPACITY = 256;

    private static final Pool<Vector2> vector2Pool = new Pool<Vector2>(INITIAL_CAPACITY, MAX_CAPACITY) {
        @Override
        protected Vector2 newObject() {
            return new Vector2();
        }
    };
    private static final Array<Vector2> usedVector2s = new Array<>(INITIAL_CAPACITY);

    private static final Pool<Vector3> vector3Pool = new Pool<Vector3>(INITIAL_CAPACITY, MAX_CAPACITY) {
        @Override
        protected Vector3 newObject() {
            return new Vector3();
        }
    };
    private static final Array<Vector3> usedVector3s = new Array<>(INITIAL_CAPACITY);

    private static final Pool<Rectangle> rectPool = new Pool<Rectangle>(16, 128) {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    private static final Array<Rectangle> usedRects = new Array<>(16);

    public static Vector2 getVector2() {
        Vector2 v = vector2Pool.obtain();
        v.set(0, 0);
        usedVector2s.add(v);
        return v;
    }

    public static Vector2 getVector2(float x, float y) {
        Vector2 v = vector2Pool.obtain();
        v.set(x, y);
        usedVector2s.add(v);
        return v;
    }

    public static Vector2 getVector2(Vector2 copy) {
        Vector2 v = vector2Pool.obtain();
        v.set(copy);
        usedVector2s.add(v);
        return v;
    }

    public static Vector3 getVector3() {
        Vector3 v = vector3Pool.obtain();
        v.set(0, 0, 0);
        usedVector3s.add(v);
        return v;
    }

    public static Vector3 getVector3(float x, float y, float z) {
        Vector3 v = vector3Pool.obtain();
        v.set(x, y, z);
        usedVector3s.add(v);
        return v;
    }

    public static Rectangle getRectangle() {
        Rectangle r = rectPool.obtain();
        r.set(0, 0, 0, 0);
        usedRects.add(r);
        return r;
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

        vector3Pool.freeAll(usedVector3s);
        usedVector3s.clear();

        rectPool.freeAll(usedRects);
        usedRects.clear();
    }

    public static void reset() {
        freeAll();
        vector2Pool.clear();
        vector3Pool.clear();
        rectPool.clear();
    }

    public static int getVector2PoolSize() {
        return usedVector2s.size;
    }

    public static int getVector3PoolSize() {
        return usedVector3s.size;
    }

    public static int getRectPoolSize() {
        return usedRects.size;
    }
}