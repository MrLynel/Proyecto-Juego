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
    
    // SISTEMA DE ESCUDO
    private boolean escudoActivo = false;
    private float tiempoEscudoRestante = 0;
    private Sprite sprEscudo;
    
    // SISTEMA DE DISPARO TRIPLE
    private boolean disparoTripleActivo = false;
    private float tiempoDisparoTriple = 0;
    
    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala) {
        sonidoHerido = soundChoque;
        this.soundBala = soundBala;
        this.txBala = txBala;
        spr = new Sprite(tx);
        spr.setPosition(x, y);
        spr.setBounds(x, y, 45, 45);
        
        ResourceManager resources = ResourceManager.getInstance();
        Texture txEscudo = resources.getTexture("escudo.png");
        sprEscudo = new Sprite(txEscudo);
        sprEscudo.setSize(60, 60);
        sprEscudo.setPosition(x - 7.5f, y - 7.5f);
    }
    
    public void draw(SpriteBatch batch, PantallaJuego juego) {
        if (!herido) {
            // MOVIMIENTO
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                spr.setX(spr.getX() - 5);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                spr.setX(spr.getX() + 5);
            }
            
            // LÍMITES DE PANTALLA
            if (spr.getX() < 0) {
                spr.setX(0);
            }
            if (spr.getX() + spr.getWidth() > Gdx.graphics.getWidth()) {
                spr.setX(Gdx.graphics.getWidth() - spr.getWidth());
            }
            
            // ACTUALIZAR ESCUDO
            if (escudoActivo) {
                tiempoEscudoRestante -= Gdx.graphics.getDeltaTime();
                if (tiempoEscudoRestante <= 0) {
                    escudoActivo = false;
                }
                
                sprEscudo.setPosition(spr.getX() - 7.5f, spr.getY() - 7.5f);
                
                if (tiempoEscudoRestante < 1) {
                    float alpha = (MathUtils.sin(tiempoEscudoRestante * 10) + 1) / 2;
                    sprEscudo.setAlpha(alpha);
                } else {
                    sprEscudo.setAlpha(0.7f);
                }
                
                sprEscudo.draw(batch);
            }
            
            // ACTUALIZAR DISPARO TRIPLE
            if (disparoTripleActivo) {
                tiempoDisparoTriple -= Gdx.graphics.getDeltaTime();
                if (tiempoDisparoTriple <= 0) {
                    disparoTripleActivo = false;
                }
            }
            
            spr.draw(batch);
            
        } else {
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            spr.draw(batch);
            spr.setX(spr.getX() - MathUtils.random(-2, 2));
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }
        
        // DISPARO
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (disparoTripleActivo) {
                // DISPARO TRIPLE
                Bullet balaCentro = new Bullet(spr.getX() + spr.getWidth() / 2 - 5, 
                                             spr.getY() + spr.getHeight() - 5, 0, 3, txBala);
                Bullet balaIzquierda = new Bullet(spr.getX() + spr.getWidth() / 4 - 5, 
                                                spr.getY() + spr.getHeight() - 5, -1, 3, txBala);
                Bullet balaDerecha = new Bullet(spr.getX() + 3 * spr.getWidth() / 4 - 5, 
                                              spr.getY() + spr.getHeight() - 5, 1, 3, txBala);
                
                juego.agregarBala(balaCentro);
                juego.agregarBala(balaIzquierda);
                juego.agregarBala(balaDerecha);
                soundBala.play();
            } else {
                // DISPARO NORMAL
                Bullet bala = new Bullet(spr.getX() + spr.getWidth() / 2 - 5, 
                                       spr.getY() + spr.getHeight() - 5, 0, 3, txBala);
                juego.agregarBala(bala);
                soundBala.play();
            }
        }
    }
    
    // MÉTODO PARA ACTIVAR DISPARO TRIPLE
    public void activarDisparoTriple(float duracion) {
        this.disparoTripleActivo = true;
        this.tiempoDisparoTriple = duracion;
    }
    
    public boolean tieneDisparoTriple() {
        return disparoTripleActivo;
    }
    
    public float getTiempoDisparoTripleRestante() {
        return tiempoDisparoTriple;
    }
    
    // ... (el resto de los métodos se mantienen igual)
    public boolean checkCollision(Ball2 b) {
        if (escudoActivo) {
            return true;
        }
        
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
    
    public void activarEscudo(float duracion) {
        this.escudoActivo = true;
        this.tiempoEscudoRestante = duracion;
        if (sprEscudo != null) {
            sprEscudo.setAlpha(0.7f);
        }
    }
    
    public boolean tieneEscudoActivo() {
        return escudoActivo;
    }
    
    public float getTiempoEscudoRestante() {
        return tiempoEscudoRestante;
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
    
    public Sound getSoundBala() {
        return soundBala;
    }
}
