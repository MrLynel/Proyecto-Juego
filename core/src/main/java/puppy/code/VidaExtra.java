package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class VidaExtra extends GameObject implements Colisionable {
    
    public VidaExtra(float x, float y, Texture tx) {
        super(x, y, 30, 0, -2, tx); // BAJA MAS LENTO
    
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
        return true;
    }
    
    @Override
    public void onColision() {
        active = false;
    }
}
