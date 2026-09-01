package za.co.wethinkcode.robots;

import org.junit.jupiter.api.*;
import za.co.wethinkcode.robots.persistence.*;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RestoreWorldTest {

    static DatabaseConnection testDb;
    static RestoreWorldData restore;

    @BeforeAll
    static void setup() throws Exception {
        // Create a new test database
        testDb = new DatabaseConnection("jdbc:sqlite:test.db");

        try (Connection conn = testDb.connection()) {
            // Drop existing tables if they exist
            conn.createStatement().execute("DROP TABLE IF EXISTS obstacles");
            conn.createStatement().execute("DROP TABLE IF EXISTS pits");
            conn.createStatement().execute("DROP TABLE IF EXISTS mines");
            conn.createStatement().execute("DROP TABLE IF EXISTS world");

            // Create fresh tables with correct schema
            conn.createStatement().execute("""
                CREATE TABLE world (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL,
                    width INTEGER NOT NULL,
                    height INTEGER NOT NULL
                );
            """);

            conn.createStatement().execute("""
                CREATE TABLE obstacles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    world_id INTEGER NOT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    FOREIGN KEY (world_id) REFERENCES world(id)
                );
            """);

            conn.createStatement().execute("""
                CREATE TABLE pits (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    world_id INTEGER NOT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    FOREIGN KEY (world_id) REFERENCES world(id)
                );
            """);

            conn.createStatement().execute("""
                CREATE TABLE mines (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    world_id INTEGER NOT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    FOREIGN KEY (world_id) REFERENCES world(id)
                );
            """);
        }

        // Initialize RestoreWorldData with the test DB
        restore = new RestoreWorldData(testDb);
    }

    @BeforeEach
    void clearTables() throws Exception {
        try (Connection conn = testDb.connection()) {
            conn.createStatement().execute("DELETE FROM obstacles");
            conn.createStatement().execute("DELETE FROM pits");
            conn.createStatement().execute("DELETE FROM mines");
            conn.createStatement().execute("DELETE FROM world");
        }
    }

    @Test
    void testLoadWorld() throws Exception {
        try (Connection conn = testDb.connection()) {
            conn.createStatement().execute(
                    "INSERT INTO world (name, width, height) VALUES ('testWorld', 20, 30)"
            );
        }

        WorldData w = restore.loadWorld("testWorld");

        assertNotNull(w);
        assertEquals(20, w.getWidth());
        assertEquals(30, w.getHeight());
    }

    @Test
    void testLoadObstacles() throws Exception {
        try (Connection conn = testDb.connection()) {
            conn.createStatement().execute("INSERT INTO world (name, width, height) VALUES ('w1',10,10)");
            conn.createStatement().execute("INSERT INTO obstacles (world_id, x, y) VALUES (1, 5, 5)");
            conn.createStatement().execute("INSERT INTO obstacles (world_id, x, y) VALUES (1, 2, 3)");
        }

        List<ObstacleData> list = restore.loadObstacles(1);

        assertEquals(2, list.size());
        assertEquals(5, list.get(0).getX());
        assertEquals(5, list.get(0).getY());
    }

    @Test
    void testLoadPits() throws Exception {
        try (Connection conn = testDb.connection()) {
            conn.createStatement().execute("INSERT INTO world (name, width, height) VALUES ('w2',10,10)");
            conn.createStatement().execute("INSERT INTO pits (world_id, x, y) VALUES (1, 7, 7)");
        }

        List<PitData> pits = restore.loadPits(1);

        assertEquals(1, pits.size());
        assertEquals(7, pits.get(0).getX());
    }

    @Test
    void testLoadMines() throws Exception {
        try (Connection conn = testDb.connection()) {
            conn.createStatement().execute("INSERT INTO world (name, width, height) VALUES ('w3',10,10)");
            conn.createStatement().execute("INSERT INTO mines (world_id, x, y) VALUES (1, 9, 9)");
        }

        List<MineData> mines = restore.loadMines(1);

        assertEquals(1, mines.size());
        assertEquals(9, mines.get(0).getX());
    }
}
