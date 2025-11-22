package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

public class Ball2 extends GameObject {
    private boolean esMiniAsteroide = false;
    
    public Ball2(float x, float y, int size, int xSpeed, int ySpeed, Texture tx) {
        super(x, y, size, xSpeed, ySpeed, tx);
    }
    
    // Constructor para mini asteroides
    public Ball2(float x, float y, int size, int xSpeed, int ySpeed, Texture tx, boolean esMini) {
        super(x, y, size, xSpeed, ySpeed, tx);
        this.esMiniAsteroide = esMini;
    }
    
    @Override
    protected void aplicarComportamientoEspecifico() {
        // CONFIGURAR REBOTE EN LOS LATERALES (solo para no mini-asteroides)
        if (!esMiniAsteroide && (x < 0 || x + spr.getWidth() > Gdx.graphics.getWidth())) {
            xSpeed = -xSpeed;
        }
    }
    
    @Override
    protected void verificarLimitesPantalla() {
        if (y + spr.getHeight() < 0) {
            active = false;
        }
    }
    
    // VERIFICAR SI UN ASTEROIDE ES GRANDE
    public boolean esAsteroideGrande() {
        return !esMiniAsteroide && spr.getWidth() > 45;
    }
    
    // CREAR MINI METEORITOS AL EXPLOTAR
    public List<Ball2> explotar() {
        List<Ball2> miniAsteroides = new ArrayList<>();
        ResourceManager resources = ResourceManager.getInstance();
        Texture textura = resources.getTexture("aGreyMedium4.png");
        
        // CREAR 3 MINI METEORITOS
        // 1. DIRECTO HACIA ABAJO
        Ball2 mini1 = new Ball2(x + 10, y, 25, 0, -5, textura, true);
        // 2. HACIA LA IZQUIERDA
        Ball2 mini2 = new Ball2(x, y, 25, -3, -4, textura, true);
        // 3. HACIA LA DERECHA  
        Ball2 mini3 = new Ball2(x + 20, y, 25, 3, -4, textura, true);
        
        // ESTRATEGIAS DE MOVIMIENTO PARA LOS MINIASTEROIDES
        mini1.setMovementStrategy(new LinearMovement());
        mini2.setMovementStrategy(new SinusoidalMovement(0.3f, 0.08f));
        mini3.setMovementStrategy(new SinusoidalMovement(0.3f, 0.06f));
        
        miniAsteroides.add(mini1);
        miniAsteroides.add(mini2);
        miniAsteroides.add(mini3);
        
        return miniAsteroides;
    }
    
    // METODOS EPECIFICOS DE BALL2
    public void checkCollision(Ball2 b2) {
        if (active && b2.active && spr.getBoundingRectangle().overlaps(b2.spr.getBoundingRectangle())) {
            // REBBOTAR SOLO SI NO SON MINI ASTEROIDES EN CASO DE SERLO PASAN ENTRE SI
            if (!this.esMiniAsteroide && !b2.esMiniAsteroide) {
                xSpeed = -xSpeed;
                b2.xSpeed = -b2.xSpeed;
            }
        }
    }
    
    @Override
    public void aplicarEfecto(Nave4 nave) {
        // Ball2 NO APLICA EFECTO POSITIVO
    }
    
    @Override
    public String getTipo() {
        return esMiniAsteroide ? "MiniAsteroide" : "Asteroide";
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        if (active) {
            //EFECTO PARA MINI ASTEROIDES
            if (esMiniAsteroide) {
                // PARPADEO PARA MINIASTEROIDES
                float alpha = 0.7f + 0.3f * (float)Math.sin(System.currentTimeMillis() * 0.01);
                spr.setAlpha(alpha);
            }
            spr.draw(batch);
            
            spr.setAlpha(1.0f);
        }
    }
}
