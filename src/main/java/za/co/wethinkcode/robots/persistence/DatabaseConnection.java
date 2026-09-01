package za.co.wethinkcode.robots.persistence;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class DatabaseConnection {
    private  final String  DB_URL;

    //Default constructor
    public DatabaseConnection(){
        this.DB_URL = "jdbc:sqlite:robotworlds.db";
    }

    public DatabaseConnection( String url){
        this.DB_URL = url;
    }


    public Connection connection () {
        try {
            Connection connect = DriverManager.getConnection(DB_URL);
            return connect;
        } catch (SQLException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Connection closed successfully.");
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
    }

    public void runSchema(Connection conn){
        try{
            String sql = Files.readString(Path.of("src/main/resources/world-schema.sql"));
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            System.out.println("Schema loaded/verified");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
