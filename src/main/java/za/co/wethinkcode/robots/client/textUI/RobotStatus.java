package za.co.wethinkcode.robots.client.textUI;

import java.util.List;

// this is a helper class for TextUserInterface class
public class RobotStatus {
    private final int shield;
    private final int shots;
    private final String direction;
    private final String position;
    private final String state;
    private final List<List<String>> directionList;



    public RobotStatus(int shield, int shots, String direction, String position, String state, List<List<String>> directionList ){
        this.shield = shield;
        this.shots = shots;
        this.direction = direction;
        this.state = state;
        this.directionList = directionList;
        this.position = position;
    }

    //getters

    public int getShield(){
        return shield;
    }
    public int getShots(){
        return shots;
    }

    public String getDirection(){
        return direction;
    }
    public String getPosition(){
        return position;
    }
    public String getState(){
        return state;
    }
    public List<List<String>> getDirectionList() {
        return directionList; }

}

