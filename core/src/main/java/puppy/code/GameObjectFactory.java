package puppy.code;

public interface GameObjectFactory {
    Ball2 createAsteroid(float x, float y);
    GameObject createPowerUp(float x, float y);
    String getFactoryType();
}