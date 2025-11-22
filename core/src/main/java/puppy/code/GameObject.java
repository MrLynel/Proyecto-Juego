package puppy.code;

import com.badlogic.gdx.Gdx;
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
    
    // GM2.3 - PATRON STRATEGY 
    protected MovementStrategy movementStrategy;

    public GameObject(float x, float y, int size, int xSpeed, int ySpeed, Texture tx) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        
        // GM2.3 - ESTRATGIA POR DEFECTO
        this.movementStrategy = new LinearMovement();
        
        spr = new Sprite(tx);
        spr.setSize(size, size);
        spr.setPosition(x, y);
    }

    // GM2.2 - METODO TEMPLATE 
    public final void update() {
        if (!active) return;
        
        // 1. PASO FIJO: MOVIMIENTO BASICO AHORA USA STRATEGY
        mover();
        
        // 2. PASO VARIABLE: COMPORTAMIENTO ESPECIFICO
        aplicarComportamientoEspecifico();
        
        // 3. PASO FIJO: VERIFICAR LIMITES
        verificarLimitesPantalla();
        
        // 4. PASO FIJO: ACTUALIZAR SPRITE
        actualizarSprite();
        
        // 5. PASO VARIABLE: COMPORTAMIENTO POST ACTUALIZACION
        comportamientoPostActualizacion();
    }
    
    // OPERACIONES PRIMITIVAS
    
    protected void mover() {
        // GM2.3 - USAR STRATEGY PARA EL MOVIMIENTO
        if (movementStrategy != null) {
            movementStrategy.move(this);
        } else {
            
            x += xSpeed;
            y += ySpeed;
        }
    }
    
    protected void verificarLimitesPantalla() {
        if (y + spr.getHeight() < 0 || y > Gdx.graphics.getHeight() || 
            x + spr.getWidth() < 0 || x > Gdx.graphics.getWidth()) {
            active = false;
        }
    }
    
    protected void actualizarSprite() {
        spr.setPosition(x, y);
    }
    
    // METODOS HOOK
    protected abstract void aplicarComportamientoEspecifico();
    
    protected void comportamientoPostActualizacion() {
        // HOOK OPCIONAL
    }

    // GM2.3 - METODOS STRATEGY
    public void setMovementStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;
    }
    
    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }
    
    public String getMovementStrategyName() {
        return movementStrategy != null ? movementStrategy.toString() : "Sin Estrategia";
    }

    public void draw(SpriteBatch batch) {
        if (active) {
            spr.draw(batch);
        }
    }

    public Rectangle getArea() {
        return spr.getBoundingRectangle();
    }

    public boolean isActive() {
        return active;
    }

    public abstract void aplicarEfecto(Nave4 nave);
    public abstract String getTipo();
    
    // Getters y Setters
    public float getX() { return x; }
    public float getY() { return y; }
    public int getXSpeed() { return xSpeed; }
    public int getYSpeed() { return ySpeed; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setXSpeed(int xSpeed) { this.xSpeed = xSpeed; }
    public void setYSpeed(int ySpeed) { this.ySpeed = ySpeed; }
}