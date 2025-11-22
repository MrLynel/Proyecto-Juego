package puppy.code;

public class GameObjectFactoryCreator {
    public static GameObjectFactory createFactory(int ronda) {
        if (ronda <= 3) {
            return new EasyLevelFactory();
        } else if (ronda <= 6) {
            return new MediumLevelFactory();
        } else {
            return new HardLevelFactory();
        }
    }
}