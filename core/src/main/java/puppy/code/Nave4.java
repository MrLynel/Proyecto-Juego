package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Nave4 {
    
    private boolean destruida = false;
    private int vidas = 3;
    private Sprite spr;
    private Sound sonidoHerido;
    private Sound soundBala;
    private Texture txBala;
    private boolean herido = false;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido;
    
    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala) {
        sonidoHerido = soundChoque;
        this.soundBala = soundBala;
        this.txBala = txBala;
        spr = new Sprite(tx);
        spr.setPosition(x, y);
        spr.setBounds(x, y, 45, 45);
    }
    
    public void draw(SpriteBatch batch, PantallaJuego juego) {
        if (!herido) {
            // RECONSTRUCCION DE LA MOVILIDAD (CONSTANTE SIN ACELEACION) ADEMAS DE LIMITAR A IZQUIERDA A DERECHA
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                spr.setX(spr.getX() - 5); // Velocidad constante izquierda
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                spr.setX(spr.getX() + 5); // Velocidad constante derecha
            }
            
            // LIMITES DE PANTALLA
            if (spr.getX() < 0) {
                spr.setX(0);
            }
            if (spr.getX() + spr.getWidth() > Gdx.graphics.getWidth()) {
                spr.setX(Gdx.graphics.getWidth() - spr.getWidth());
            }
            
            spr.draw(batch);
        } else {
            // Efecto de herido
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            spr.draw(batch);
            spr.setX(spr.getX() - MathUtils.random(-2, 2)); // Volver a posición
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }
        
        // DISPARO
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Bullet bala = new Bullet(spr.getX() + spr.getWidth() / 2 - 5, 
                                   spr.getY() + spr.getHeight() - 5, 0, 3, txBala);
            juego.agregarBala(bala);
            soundBala.play();
        }
    }
    
    public boolean checkCollision(Ball2 b) {
        if (!herido && b.getArea().overlaps(spr.getBoundingRectangle())) {
            vidas--;
            herido = true;
            tiempoHerido = tiempoHeridoMax;
            sonidoHerido.play();
            if (vidas <= 0) 
                destruida = true;
            return true;
        }
        return false;
    }
    
    public boolean estaDestruido() {
        return !herido && destruida;
    }
    
    public boolean estaHerido() {
        return herido;
    }
    
    public int getVidas() {
        return vidas;
    }
    
    public int getX() {
        return (int) spr.getX();
    }
    
    public int getY() {
        return (int) spr.getY();
    }
    
    public void setVidas(int vidas2) {
        vidas = vidas2;
    }
    
    public Rectangle getArea() {
        return spr.getBoundingRectangle();
    }
}
