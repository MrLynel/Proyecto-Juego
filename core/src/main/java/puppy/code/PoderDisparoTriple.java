package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class PoderDisparoTriple extends GameObject implements Colisionable {
    private boolean efectoActivado = false;
    private int tiempoEfecto = 0;
    
    public PoderDisparoTriple(float x, float y, Texture tx) {
        super(x, y, 25, 0, -3, tx);
    }
    
    @Override
    protected void aplicarComportamientoEspecifico() {
        if (efectoActivado) {
            tiempoEfecto++;
            spr.rotate(-10); 
            spr.setScale(1.5f - (tiempoEfecto * 0.05f));
            
            if (tiempoEfecto > 15) {
                active = false;
            }
        }
    }
    
    @Override
    protected void comportamientoPostActualizacion() {
        if (!efectoActivado) {
            // MOVIMIENTO ESPIRAL
            float time = System.currentTimeMillis() * 0.01f;
            spr.setX(spr.getX() + (float)Math.sin(time) * 0.8f);
            spr.setY(spr.getY() + (float)Math.cos(time) * 0.8f - 3);
        }
    }
    
    @Override
    public void aplicarEfecto(Nave4 nave) {
        nave.activarDisparoTriple(10); // SOLO 10 SEGUNDOS DE DISPARO TRIPLE
    }
    
    @Override
    public String getTipo() {
        return "DisparoTriple";
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
        spr.setColor(1, 0.5f, 0.3f, 1); // COLOR NARANJA
       
    }
}