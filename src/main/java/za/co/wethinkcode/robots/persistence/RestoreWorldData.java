package za.co.wethinkcode.robots.persistence;

import za.co.wethinkcode.robots.obstacle.BottomLessPit;
import za.co.wethinkcode.robots.obstacle.Mine;
import za.co.wethinkcode.robots.obstacle.Mountain;
import za.co.wethinkcode.robots.obstacle.Obstacle;
import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.world.World;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads a world (by name) and its related obstacles/pits/mines
 */
public class RestoreWorldData {

    private  DatabaseConnection db ;

    public RestoreWorldData(DatabaseConnection db){
        this.db = db;
    }
    public RestoreWorldData(){
        this.db = new DatabaseConnection();
    }

    /**
     * Load world by name. Returns null if not found.
     */
    public WorldData loadWorld(String name) {
        String sql = "SELECT id, width, height FROM world WHERE name = ?";

        try (Connection conn = db.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int width = rs.getInt("width");
                    int height = rs.getInt("height");
                    return new WorldData(id, width, height);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<ObstacleData> loadObstacles(int worldId) {
        String sql = "SELECT id, x, y FROM obstacles WHERE world_id = ?";
        List<ObstacleData> list = new ArrayList<>();

        try (Connection conn = db.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, worldId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    list.add(new ObstacleData(id, worldId, x, y));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<PitData> loadPits(int worldId) {
        String sql = "SELECT id, x, y FROM pits WHERE world_id = ?";
        List<PitData> list = new ArrayList<>();

        try (Connection conn = db.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, worldId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    list.add(new PitData(id, worldId, x, y));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<MineData> loadMines(int worldId) {
        String sql = "SELECT id, x, y FROM mines WHERE world_id = ?";
        List<MineData> list = new ArrayList<>();

        try (Connection conn = db.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, worldId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    list.add(new MineData(id, worldId, x, y));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void restoreWorld(World world, WorldData worldData) {
        // 1️⃣ Resize world if needed
        int savedWidth = worldData.getWidth();
        int savedHeight = worldData.getHeight();
        if (savedWidth != world.getWidth() || savedHeight != world.getHeight()) {
            System.out.println("⚠ Resizing world to match saved dimensions...");
            world.resizeWorld(savedWidth, savedHeight);
        }

        // 2️⃣ Clear current obstacles and mines
        world.clearObstacles();
        world.clearMines();

        // 3️⃣ Restore obstacles
        List<ObstacleData> obstacles = loadObstacles(worldData.getWorldId());
        for (ObstacleData obs : obstacles) {
            Position topLeft = new Position(obs.getX(), obs.getY());
            Position bottomRight = new Position(obs.getX() + 4, obs.getY() + 4);
            Mountain mountain = new Mountain(
                    topLeft.getX(), topLeft.getY(),
                    bottomRight.getX(), bottomRight.getY()
            );
            world.addObstacle(mountain);
        }

        // 4️⃣ Restore pits
        List<PitData> pits = loadPits(worldData.getWorldId());
        for (PitData pit : pits) {
            Position topLeft = new Position(pit.getX(), pit.getY());
            Position bottomRight = new Position(pit.getX() + 4, pit.getY() + 4);
            BottomLessPit bottomlessPit = new BottomLessPit(topLeft, bottomRight);
            world.addObstacle(bottomlessPit);
        }

        // 5️⃣ Restore mines
        List<MineData> mines = loadMines(worldData.getWorldId());
        for (MineData mineData : mines) {
            Mine mine = new Mine(mineData.getX(), mineData.getY());
            world.addMine(mine);
        }

        System.out.println("World restored successfully from '" + worldData.getWorldId() + "'");
    }

}
