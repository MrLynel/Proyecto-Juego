package puppy.code;

import com.badlogic.gdx.Gdx; //
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    protected float x;
    protected float y;
    protected int xSpeed;
    protected int ySpeed;
    protected Sprite spr;
    protected boolean active = true;

    public GameObject(float x, float y, int size, int xSpeed, int ySpeed, Texture tx) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        
        spr = new Sprite(tx);
        spr.setSize(size, size);
        spr.setPosition(x, y);
    }

    public void update() {
        x += xSpeed;
        y += ySpeed;
        spr.setPosition(x, y);
        
        // ELIMINAR CUANDO SALGA
        if (y + spr.getHeight() < 0 || y > Gdx.graphics.getHeight() || 
            x + spr.getWidth() < 0 || x > Gdx.graphics.getWidth()) {
            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        spr.draw(batch);
    }

    public Rectangle getArea() {
        return spr.getBoundingRectangle();
    }

    public boolean isActive() {
        return active;
    }

    public abstract void aplicarEfecto(Nave4 nave);
    public abstract String getTipo();
}
