package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class VidaExtra extends GameObject implements Colisionable {
    private boolean efectoActivado = false;
    private int tiempoEfecto = 0;
    
    public VidaExtra(float x, float y, Texture tx) {
        super(x, y, 30, 0, -2, tx);
    }
    
    @Override
    public void update() {
        if (!efectoActivado) {
            super.update();
        } else {
            //EFECTO ESPECIAL
            tiempoEfecto++;
            spr.setScale(1.0f + (tiempoEfecto * 0.1f)); //SE AGRANDA
            spr.setAlpha(1.0f - (tiempoEfecto * 0.05f)); // SE DESAPARECE
            
            if (tiempoEfecto > 20) {
                active = false; //ELIMINARSE DESPUES DEL EFECTO
            }
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
    

    @Override
    public boolean debeEliminarse() {
        
        return efectoActivado && tiempoEfecto > 20;
    }
    
    @Override
    public void onColision() {
        
        efectoActivado = true;
        activarEfectoVisual();
    }
    
    @Override
    public void activarEfectoVisual() {
        //SE CAMBIA A COLOR DORADO POR UN MOMENTO
        spr.setColor(1, 1, 0.3f, 1); // Color dorado
    }
}
