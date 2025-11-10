package Things.Helpers;
public enum DIRECTION {
    NORTH, EAST, SOUTH, WEST;

    private static final DIRECTION[] VALUES = values();

    public DIRECTION turnRight() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public DIRECTION turnLeft() {
        return VALUES[(ordinal() - 1 + VALUES.length) % VALUES.length];
    }
}