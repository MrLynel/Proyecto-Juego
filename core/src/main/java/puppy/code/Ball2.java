package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Ball2 {
    private float x;
    private float y;
    private int xSpeed;
    private int ySpeed;
    private Sprite spr;
    private boolean active = true;

    public Ball2(float x, float y, int size, int xSpeed, int ySpeed, Texture tx) {
        spr = new Sprite(tx);
        this.x = x;
        this.y = y;
        
        // AJUSTAR TAMA;OS SPRITES
        spr.setSize(size, size);
        spr.setPosition(x, y);
        
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }
    
    public void update() {
        // MOVIMIENTO CON CAÍDA
        x += xSpeed;
        y += ySpeed; 

        // ELIMINAR CUANDO SE SALGA
        if (y + spr.getHeight() < 0) {
            active = false;
        }
        
        // REBOTE EN LOS LADOS
        if (x < 0 || x + spr.getWidth() > Gdx.graphics.getWidth()) {
            xSpeed = -xSpeed;
        }
        
        spr.setPosition(x, y);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public Rectangle getArea() {
        return spr.getBoundingRectangle();
    }
    
    public void draw(SpriteBatch batch) {
        spr.draw(batch);
    }
    
    public void checkCollision(Ball2 b2) {
        if (active && b2.active && spr.getBoundingRectangle().overlaps(b2.spr.getBoundingRectangle())) {
            xSpeed = -xSpeed;
            b2.xSpeed = -b2.xSpeed;
        }
    }
    
    public int getXSpeed() {
        return xSpeed;
    }
    
    public void setXSpeed(int xSpeed) {
        this.xSpeed = xSpeed;
    }
    
    public int getySpeed() {
        return ySpeed;
    }
    
    public void setySpeed(int ySpeed) {
        this.ySpeed = ySpeed;
    }
}
