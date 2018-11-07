package com.android.home.pixeldungeon.noosa;

public class Gizmo {

    public boolean exists;
    public boolean alive;
    public boolean active;
    public boolean visible;

    public Group parent;

    public Camera camera;

    public Gizmo() {
        exists	= true;
        alive	= true;
        active	= true;
        visible	= true;
    }

    public void destroy() {
        parent = null;
    }

    public void update() {
    }

    public void draw() {
    }

    public void kill() {
        alive = false;
        exists = false;
    }

    // Not exactly opposite to "kill" method
    public void revive() {
        alive = true;
        exists = true;
    }


}
