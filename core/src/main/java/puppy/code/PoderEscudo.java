package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class PoderEscudo extends GameObject implements Colisionable {
    private boolean efectoActivado = false;
    private int tiempoEfecto = 0;
    
    public PoderEscudo(float x, float y, Texture tx) {
        super(x, y, 25, 0, -3, tx);
    }
    
    @Override
    protected void aplicarComportamientoEspecifico() {
        if (efectoActivado) {
            tiempoEfecto++;
            spr.rotate(10);
            spr.setScale(1.5f - (tiempoEfecto * 0.05f));
            
            if (tiempoEfecto > 15) {
                active = false;
            }
        }
    }
    
    @Override
    protected void comportamientoPostActualizacion() {
        if (!efectoActivado) {
            spr.setY(spr.getY() + (float)Math.sin(System.currentTimeMillis() * 0.01) * 0.5f);
        }
    }
    
    @Override
    public void aplicarEfecto(Nave4 nave) {
        // SOLO 3 SEGUNDOS DE ESCUDO 
        nave.activarEscudo(3);
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
