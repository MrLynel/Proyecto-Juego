package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import java.util.Random;

public class MediumLevelFactory implements GameObjectFactory {
    private Random random = new Random();
    private ResourceManager resources = ResourceManager.getInstance();
    
    @Override
    public Ball2 createAsteroid(float x, float y) {
        int size = 35 + random.nextInt(35); // TAMAñO ENTRE 35-70
        int xSpeed = random.nextInt(7) - 3;
        int ySpeed = -(2 + random.nextInt(3));
        
        Texture texture = resources.getTexture("aGreyMedium4.png");
        Ball2 asteroid = new Ball2(x, y, size, xSpeed, ySpeed, texture);
        
        float movementType = random.nextFloat();
        if (movementType < 0.6f) {
            asteroid.setMovementStrategy(new LinearMovement());
        } else if (movementType < 0.8f) {
            asteroid.setMovementStrategy(new SinusoidalMovement(0.5f, 0.03f));
        } else {
            asteroid.setMovementStrategy(new ZigZagMovement(40));
        }
        
        return asteroid;
    }
    
    @Override
    public GameObject createPowerUp(float x, float y) {
        float powerUpType = random.nextFloat();
        if (powerUpType < 0.4f) {
            Texture texture = resources.getTexture("oneup.png");
            return new VidaExtra(x, y, texture);
        } else if (powerUpType < 0.7f) {
            Texture texture = resources.getTexture("escudo.png");
            return new PoderEscudo(x, y, texture);
        } else {
            Texture texture = resources.getTexture("triple.png");
            return new PoderDisparoTriple(x, y, texture);
        }
    }
    
    @Override
    public String getFactoryType() {
        return "Fábrica Nivel Medio";
    }
}