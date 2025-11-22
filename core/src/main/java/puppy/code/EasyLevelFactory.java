package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import java.util.Random;

public class EasyLevelFactory implements GameObjectFactory {
    private Random random = new Random();
    private ResourceManager resources = ResourceManager.getInstance();
    
    @Override
    public Ball2 createAsteroid(float x, float y) {
        // ASTEROIDES MAS GRANDES
        int size = 30 + random.nextInt(30); // TAMAñO ENTRE 30-60
        int xSpeed = random.nextInt(5) - 2;
        int ySpeed = -(1 + random.nextInt(2));
        
        Texture texture = resources.getTexture("aGreyMedium4.png");
        Ball2 asteroid = new Ball2(x, y, size, xSpeed, ySpeed, texture);
        asteroid.setMovementStrategy(new LinearMovement());
        return asteroid;
    }
    
    @Override
    public GameObject createPowerUp(float x, float y) {
        // SPAWNEAR VIDA EXTRA
        Texture texture = resources.getTexture("oneup.png");
        return new VidaExtra(x, y, texture);
    }
    
    @Override
    public String getFactoryType() {
        return "Fábrica Nivel Fácil";
    }
}