package puppy.code;

public class SinusoidalMovement implements MovementStrategy {
    private float time = 0;
    private float amplitude;
    private float frequency;
    
    public SinusoidalMovement(float amplitude, float frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
    }
    
    @Override
    public void move(GameObject gameObject) {
        time += 0.1f;
        float offset = (float)Math.sin(time * frequency) * amplitude;
        
        gameObject.setX(gameObject.getX() + gameObject.getXSpeed());
        gameObject.setY(gameObject.getY() + gameObject.getYSpeed() + offset);
    }
    
    @Override
    public MovementStrategy copy() {
        return new SinusoidalMovement(amplitude, frequency);
    }
    
    @Override
    public String toString() {
        return "Movimiento Sinusoidal";
    }
}