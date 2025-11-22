package puppy.code;

public interface MovementStrategy {
    void move(GameObject gameObject);
    MovementStrategy copy();
}