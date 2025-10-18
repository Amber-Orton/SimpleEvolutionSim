package Things;
public class Position {
    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int[] getPos() {
        return new int[]{row, col};
    }

    public void setPos(int[] pos) {
        this.row = pos[0];
        this.col = pos[1];
    }

    public void setPos(Position pos) {
        this.row = pos.row;
        this.col = pos.col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Position add(Position other) {
        return new Position(this.row + other.row, this.col + other.col);
    }


    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
