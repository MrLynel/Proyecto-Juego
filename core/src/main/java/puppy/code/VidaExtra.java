package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class VidaExtra extends GameObject implements Colisionable {
    private boolean efectoActivado = false;
    private int tiempoEfecto = 0;
    
    public VidaExtra(float x, float y, Texture tx) {
        super(x, y, 30, 0, -2, tx);
    }
    
    // GM2.2 - IMPLEMENTACIÓN DEL MÉTODO ABSTRACTO DEL TEMPLATE METHOD
    @Override
    protected void aplicarComportamientoEspecifico() {
        if (efectoActivado) {
            // ANIMACION
            tiempoEfecto++;
            spr.setScale(1.0f + (tiempoEfecto * 0.1f)); // SE AGRANDA
            spr.setAlpha(1.0f - (tiempoEfecto * 0.05f)); // SE DESAPARECE
            
            if (tiempoEfecto > 20) {
                active = false; // ELIMINARSE DESPUES DEL EFECTO
            }
        }
    }
    
    // GM2.2 - OVERRIDE DEL HOOK METHOD OPCIONAL
    @Override
    protected void comportamientoPostActualizacion() {
        if (!efectoActivado) {
            // ANIMACION
            spr.setY(spr.getY() + (float)Math.sin(System.currentTimeMillis() * 0.005) * 0.8f);
        }
    }
    
    @Override
    public void aplicarEfecto(Nave4 nave) {
        nave.setVidas(nave.getVidas() + 1);
    }
    
    @Override
    public String getTipo() {
        return "VidaExtra";
    }
    
    // IMPLEMENTACIÓN DE COLISIONABLE
    
    @Override
    public boolean debeEliminarse() {
        // ELIMINAR
        return efectoActivado && tiempoEfecto > 20;
    }
    
    @Override
    public void onColision() {  
        efectoActivado = true;
        activarEfectoVisual();
    }
    
    @Override
    public void activarEfectoVisual() {
        // SE CAMBIA A COLOR DORADO POR UN MOMENTO
        spr.setColor(1, 1, 0.3f, 1); // Color DORADO
    }
}
