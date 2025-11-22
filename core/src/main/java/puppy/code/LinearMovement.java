package puppy.code;

public class LinearMovement implements MovementStrategy {
    @Override
    public void move(GameObject gameObject) {
        // MOVIMIENTO LINEAL (BASICO)
        gameObject.setX(gameObject.getX() + gameObject.getXSpeed());
        gameObject.setY(gameObject.getY() + gameObject.getYSpeed());
    }
    
    @Override
    public MovementStrategy copy() {
        return new LinearMovement();
    }
    
    @Override
    public String toString() {
        return "Movimiento Lineal";
    }
}