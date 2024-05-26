package gameobjects.bricks;

import gameobjects.GameObject;

public abstract class Brick extends GameObject {
    protected int brickHealth;
    protected boolean isDestroyed = false;

    public Brick(int x, int y, int width, int height, int health) {
        super(x, y, width, height);

        brickHealth = health;
    }

    public abstract void draw(java.awt.Graphics g);

    public abstract void update();

    public abstract void reset();

    public boolean isDestroyed() {
        return isDestroyed;
    };

    public void hit(int damage) {
        brickHealth -= damage;
        if (brickHealth <= 0) {
            isDestroyed = true;
        }
    }
}