package za.co.wethinkcode.robots.persistence;

public class MineData {
    private final int id;
    private final int worldId;
    private final int x;
    private final int y;

    public MineData(int id, int worldId, int x, int y) {
        this.id = id;
        this.worldId = worldId;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }
    public int getWorldId() { return worldId; }
    public int getX() { return x; }
    public int getY() { return y; }
}
