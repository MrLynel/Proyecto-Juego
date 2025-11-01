package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class PoderEscudo extends GameObject implements Colisionable {
    private boolean efectoActivado = false;
    private int tiempoEfecto = 0;
    
    public PoderEscudo(float x, float y, Texture tx) {
        super(x, y, 25, 0, -3, tx);
    }
    
    @Override
    public void update() {
        if (!efectoActivado) {
            super.update();
            
            
            spr.setY(spr.getY() + (float)Math.sin(System.currentTimeMillis() * 0.01) * 0.5f);
        } else {
            
            tiempoEfecto++;
            spr.rotate(10); 
            spr.setScale(1.5f - (tiempoEfecto * 0.05f)); 
            
            if (tiempoEfecto > 15) {
                active = false;
            }
        }
    }
    
    @Override
    public void aplicarEfecto(Nave4 nave) {
        nave.activarEscudo(10);
    }
    
    @Override
    public String getTipo() {
        return "Escudo";
    }
    
    
    @Override
    public boolean debeEliminarse() {
        
        return efectoActivado && tiempoEfecto > 15;
    }
    
    @Override
    public void onColision() {
        efectoActivado = true;
        activarEfectoVisual();
    }
    
    @Override
    public void activarEfectoVisual() {
        
        spr.setColor(0.3f, 0.5f, 1, 1);
    }
}
