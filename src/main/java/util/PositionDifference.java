package util;

public class PositionDifference {
    private int position;
    private long difference;

    public PositionDifference(int position, long difference) {
        this.position = position;
        this.difference = difference;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getDifference() {
        return difference;
    }

    public void setDifference(long difference) {
        this.difference = difference;
    }
}
