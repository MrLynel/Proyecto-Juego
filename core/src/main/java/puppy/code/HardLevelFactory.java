package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import java.util.Random;

public class HardLevelFactory implements GameObjectFactory {
    private Random random = new Random();
    private ResourceManager resources = ResourceManager.getInstance();
    
    @Override
    public Ball2 createAsteroid(float x, float y) {
        int size = 40 + random.nextInt(40); // TAMAñO ENTRE 40-80
        int xSpeed = random.nextInt(9) - 4;
        int ySpeed = -(3 + random.nextInt(4));
        
        Texture texture = resources.getTexture("aGreyMedium4.png");
        Ball2 asteroid = new Ball2(x, y, size, xSpeed, ySpeed, texture);
        
        float movementType = random.nextFloat();
        if (movementType < 0.4f) {
            asteroid.setMovementStrategy(new LinearMovement());
        } else if (movementType < 0.7f) {
            asteroid.setMovementStrategy(new SinusoidalMovement(0.8f, 0.05f));
        } else {
            asteroid.setMovementStrategy(new ZigZagMovement(25));
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
        return "Fábrica Nivel Difícil";
    }
}