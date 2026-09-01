package za.co.wethinkcode.robots.persistence;

import za.co.wethinkcode.robots.obstacle.BottomLessPit;
import za.co.wethinkcode.robots.obstacle.Mine;
import za.co.wethinkcode.robots.obstacle.Obstacle;

import java.sql.*;
import java.util.List;

public class SaveWorldData {

    DatabaseConnection db = new DatabaseConnection();

    /**
     * Save a world by name. If a world with the same name exists, overwrite it:
     *  - update world width/height
     *  - delete existing pits/mines/obstacles for that world
     *  - insert new pits/mines/obstacles
     *
     * @return worldId or -1 on failure
     */
    public int saveWorld(String name, int width, int height) {
        String selectSql = "SELECT id FROM world WHERE name = ?";
        String insertSql = "INSERT INTO world (name, width, height) VALUES (?, ?, ?)";
        String updateSql = "UPDATE world SET width = ?, height = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement selectStmt = null;
        ResultSet rs = null;

        try {
            conn = db.connection();
            conn.setAutoCommit(false); // transaction

            // 1) check if world exists
            selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setString(1, name);
            rs = selectStmt.executeQuery();

            if (rs.next()) {
                // World exists -> overwrite
                int worldId = rs.getInt("id");

                // update dimensions
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, width);
                    updateStmt.setInt(2, height);
                    updateStmt.setInt(3, worldId);
                    updateStmt.executeUpdate();
                }

                // delete old related rows before inserting new ones
                try (PreparedStatement delPits = conn.prepareStatement("DELETE FROM pits WHERE world_id = ?");
                     PreparedStatement delMines = conn.prepareStatement("DELETE FROM mines WHERE world_id = ?");
                     PreparedStatement delObstacles = conn.prepareStatement("DELETE FROM obstacles WHERE world_id = ?")) {

                    delPits.setInt(1, worldId); delPits.executeUpdate();
                    delMines.setInt(1, worldId); delMines.executeUpdate();
                    delObstacles.setInt(1, worldId); delObstacles.executeUpdate();
                }

                conn.commit();
                System.out.println("World '" + name + "' updated (id=" + worldId + ")");
                return worldId;
            } else {
                // Insert new world
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, name);
                    insertStmt.setInt(2, width);
                    insertStmt.setInt(3, height);
                    insertStmt.executeUpdate();

                    try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            int newId = keys.getInt(1);
                            conn.commit();
                            System.out.println("World saved with ID: " + newId);
                            return newId;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (selectStmt != null) selectStmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }

        return -1; // failed
    }

    public void saveObstaclesAndPits(int worldId, List<Obstacle> obstacles) {

        String sqlObstacle = "INSERT INTO obstacles (world_id, x, y) VALUES (?, ?, ?)";
        String sqlPit = "INSERT INTO pits (world_id, x, y) VALUES (?, ?, ?)";

        try (Connection conn = db.connection();
             PreparedStatement obsStmt = conn.prepareStatement(sqlObstacle);
             PreparedStatement pitStmt = conn.prepareStatement(sqlPit)) {

            for (Obstacle o : obstacles) {

                int x = o.getTopLeftCorner().getX();
                int y = o.getTopLeftCorner().getY();

                if (o instanceof BottomLessPit) {
                    // Save in pits table
                    pitStmt.setInt(1, worldId);
                    pitStmt.setInt(2, x);
                    pitStmt.setInt(3, y);
                    pitStmt.addBatch();

                } else {
                    // Lake or Mountain → save in obstacles table
                    obsStmt.setInt(1, worldId);
                    obsStmt.setInt(2, x);
                    obsStmt.setInt(3, y);
                    obsStmt.addBatch();
                }
            }

            obsStmt.executeBatch();
            pitStmt.executeBatch();

            System.out.println("Obstacles & pits saved for world_id=" + worldId);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void saveMines(int worldId, List<Mine> mines) {
        String sql = "INSERT INTO mines (world_id, x, y) VALUES (?, ?, ?)";

        try (Connection conn = db.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Mine m : mines) {
                stmt.setInt(1, worldId);
                stmt.setInt(2, m.getPosition().getX());
                stmt.setInt(3, m.getPosition().getY());
                stmt.addBatch();
            }

            stmt.executeBatch();
            System.out.println("Mines saved for world_id=" + worldId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
