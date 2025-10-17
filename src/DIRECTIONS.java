public enum DIRECTIONS {
    NORTH, EAST, SOUTH, WEST;

    private static final DIRECTIONS[] VALUES = values();

    public DIRECTIONS turnRight() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public DIRECTIONS turnLeft() {
        return VALUES[(ordinal() - 1 + VALUES.length) % VALUES.length];
    }
}