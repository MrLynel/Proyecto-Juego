package puppy.code;

public class ZigZagMovement implements MovementStrategy {
    private int frameCount = 0;
    private int changeFrequency;
    
    public ZigZagMovement(int changeFrequency) {
        this.changeFrequency = changeFrequency;
    }
    
    @Override
    public void move(GameObject gameObject) {
        frameCount++;
        if (frameCount % changeFrequency == 0) {
            gameObject.setXSpeed(-gameObject.getXSpeed());
        }
        
        gameObject.setX(gameObject.getX() + gameObject.getXSpeed());
        gameObject.setY(gameObject.getY() + gameObject.getYSpeed());
    }
    
    @Override
    public MovementStrategy copy() {
        return new ZigZagMovement(changeFrequency);
    }
    
    @Override
    public String toString() {
        return "Movimiento ZigZag";
    }
}