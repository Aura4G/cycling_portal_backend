package cycling;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

/**
  Stage class to make individual stages and add them to races
  @author Aria Noroozi
*/
public class Stage implements java.io.Serializable {

    /*
      initialise instance attributes for the stages's name, description, stage and race IDs,
      stage type, length, startTime, and checkpoints
      The IDs and checkpoint are not set by the user: The ID is given automatically and not to be changed
      The stage list starts empty but gets filled up when addStageToRace is correctly called
    */

    private int raceID;
    private String stageName;
    private String description;
    private StageType type;
    private double length;
    private LocalDateTime startTime;
    private int stageID;
    private List<Checkpoint> checkPoints = new ArrayList<>();
    private String state;

    //static variable, acting as an index to give each instance a unique stage ID
    private static int i = 0;

    //get methods
    public int getRaceID() {return raceID;}
    public String getStageName() {return stageName;}
    public String getDescription() {return description;}
    public StageType getType() {return type;}
    public double getLength() {return length;}
    public LocalDateTime getStartTime() {return startTime;}
    public int getStageID() {return stageID;}
    public Checkpoint[] getCheckpoints() {return checkPoints.toArray(new Checkpoint[0]);}
    public String getState() {return state;}

    //set methods
    public void setRaceID(int raceID) {this.raceID = raceID;}
    public void setStageName(String stageName) {this.stageName = stageName;}
    public void setDescription(String description) {this.description = description;}
    public void setType(StageType type) {this.type = type;}
    public void setLength(double length) {this.length = length;}
    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}
    public void setStageID(int stageID) {this.stageID = stageID;}

    //the state of the stage changes when stage preparations are concluded
    public void endStage() {state = "waiting for results";}
    
    //Method to add a checkpoint to an instance's checkpoint list
    public void addCheckpoint(Checkpoint newCheckpoint) {
      this.checkPoints.add(newCheckpoint); 
      Collections.sort(this.checkPoints, new Comparator<Checkpoint>() {
        public int compare(Checkpoint c1, Checkpoint c2) {
          return Double.compare(c1.getLocation(), c2.getLocation());
        }
      });}

    //Method to remove a checkpoint based on a fetched index
    public void deleteCheckpoint(int index) {
      this.checkPoints.set(index, null);
      this.checkPoints.remove(index);

    }

    //Called when the portal is erased, so that ids can start from 0 again.
    public static void reset() {i = 0;};

    //Constructor
    public Stage(int raceID, String stageName, String description, StageType type, double length, LocalDateTime startTime){
        this.raceID = raceID;
        this.stageName = stageName;
        this.description = description;
        this.type = type;
        this.length = length;
        this.startTime = startTime;
        this.stageID = i++;
        state = "in preparation";
    };

}
