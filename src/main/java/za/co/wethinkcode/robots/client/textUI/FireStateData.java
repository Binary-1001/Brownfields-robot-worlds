package za.co.wethinkcode.robots.client.textUI;

public class FireStateData {
 private final String impact;
 private final int distance;
 private final String robotName;
 private final int shield;

 public FireStateData (String impact, int distance, String robotName, int shield ){
     this.impact = impact;
     this.distance = distance;
     this.robotName = robotName;
     this.shield = shield;
 }

 public String getImpact(){
     return impact;
 }
 public int getDistance(){
     return distance;
 }
 public String getRobotName(){
     return robotName;
 }
 public int getShield(){
     return shield;
 }

}
