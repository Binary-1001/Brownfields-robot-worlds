package za.co.wethinkcode.robots.persistence;

public class WorldData {
    private final int worldId;
    private final int width;
    private final int height;

    public WorldData(int worldId, int width, int height) {
        this.worldId = worldId;
        this.width = width;
        this.height = height;
    }

    public int getWorldId() {
        return worldId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}