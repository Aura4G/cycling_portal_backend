package cycling;

/**
  Checkpoint class to make individual stages and add them to races
  @author Aria Noroozi
*/
public class Checkpoint implements java.io.Serializable {
    //initialising instance attributes each checkpoint must have
    private int stageID;
    private double location;
    private CheckpointType type;
    private double averageGradient;
    private double length;
    private int checkpointId;

    //static attribute that increments with each checkpoint created; it is then assigned to the checkpoint as its unique id
    private static int i = 0;

    //get methods
    public int getStageID() {return stageID;}
    public double getLocation() {return location;}
    public CheckpointType getType() {return type;}
    public double getAverageGradient() {return averageGradient;}
    public double getLength() {return length;}
    public int getCheckpointId() {return checkpointId;}

    //set methods
    public void setStageID(int stageID) {this.stageID = stageID;}
    public void setLocation(double location) {this.location = location;}
    public void setType(CheckpointType type) {this.type = type;}
    public void setAverageGradient(int averageGradient) {this.averageGradient = averageGradient;}
    public void setLength(int length) {this.length = length;}
    public void setCheckpointId(int checkpointId) {this.checkpointId = checkpointId;}

    //Called when the portal is erased, so that ids can start from 0 again.
    public static void reset() {i = 0;};

    //Constructor
    public Checkpoint(int stageID, Double location, CheckpointType type, Double averageGradient, Double length) {
        this.stageID = stageID;
        this.location = location;
        this.type = type;
        this.averageGradient = averageGradient;
        this.length = length;
        this.checkpointId = i++;
    }

}
