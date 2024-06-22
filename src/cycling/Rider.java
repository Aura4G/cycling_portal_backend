package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Rider class to create individual riders and place them under teams.
 * @author Aria Noroozi
 */
public class Rider implements java.io.Serializable {
    private int riderId;
    private String riderName;
    private int teamId;
    private int yearOfBirth;

    //Competition-based lists:
    //Points Classification (Stage-based)
    private List<List<LocalTime>> pointsClassificationTimes = new ArrayList<>(0);
    private List<Integer> pointsClassificationScore = new ArrayList<>(0);
    private List<Integer> pointsClassificationRank = new ArrayList<>(0);
    //Mountain Classification (Checkpoint-based)
    private List<List<LocalTime>> mountainClassificationTimes = new ArrayList<>(0);
    private List<Integer> mountainClassificationScore = new ArrayList<>(0);
    private List<Integer> mountainClassificationRank = new ArrayList<>(0);

    //static variable that increments with each rider created to give said rider a unique id
    private static int i = 0;

    //get methods for rider information
    public int getRiderId() {return riderId;}
    public String getRiderName() {return riderName;}
    public int getTeamId() {return teamId;}
    public int getYearOfBirth() {return yearOfBirth;}

    //Points Classification (Stage-based)
    /** 
     * adds a new time for the rider, detailing the time in which they reached the end of a stage
     * @param newTime the elapsed time since the start of the stage, when the rider reaches the end
     * @param stageId the id of the stage the rider's results are being registered in
     * @param raceId the id of the race said stage is contained in
    */
    public void addPointsClassificationTime(LocalTime newTime, int stageId, int raceId) {
        int initialSize = pointsClassificationTimes.size();
        for (int i = initialSize; i <= raceId; i++) {
            pointsClassificationTimes.add(new ArrayList<>(0));
        }
        initialSize = pointsClassificationTimes.get(raceId).size();
        for (int i = initialSize; i <= stageId; i++) {
            pointsClassificationTimes.get(raceId).add(null);
        }
        pointsClassificationTimes.get(raceId).set(stageId, newTime);
    }

    /** 
     * updates the points classification score for the rider
     * @param newStagePoints the points added to the rider's score, retrieved from StageType
     * @param raceId the id of the race the stage is in
    */
    public void setPointsClassificationScore(int newStagePoints, int raceId) {
        int initialSize = pointsClassificationScore.size();
        for (int i = initialSize; i <= raceId; i++) {
            pointsClassificationScore.add(0);
        }
        pointsClassificationScore.set(raceId, pointsClassificationScore.get(raceId) + newStagePoints);
    }

    //unused, redundant method for when i was initially doing pair programming
    public void setPointsClassificationRank(int pcRank, int raceId) {pointsClassificationRank.set(raceId, pcRank);}

    /** 
     * retrieves an arbitrary finishing stage time for the rider
     * @param stageId the stage the rider is currently desiring times from
     * @param raceId the id of the race the stage is in
     * @return the finishing time rider had in the specified stage
    */
    public LocalTime getSpecificStageTime(int stageId, int raceId) {
        if (pointsClassificationTimes.size() <= raceId) {
            return null;
        }
        else if (pointsClassificationTimes.get(raceId).size() <= stageId) {
            return null;
        }

        return pointsClassificationTimes.get(raceId).get(stageId);
    }

    //methods to retrieve the relevant rider data, should it ever be needed (these are not used in any of the implemented methods)
    public int getPointsClassificationScore(int raceId) {return pointsClassificationScore.get(raceId);}
    public int getPointsClassificationRank(int raceId) {return pointsClassificationRank.get(raceId);}

    //Mountain Classification (Checkpoint-based)
    /** 
     * adds a new time for the rider, detailing the time in which they reached the end of a checkpoint
     * @param newTime the elapsed time since the start of the stage, when the rider reaches the checkpoint
     * @param checkpointId the id of the checkpoint the rider's results are being registered in
     * @param raceId the id of the race said stage is contained in
    */
    public void addMountainClassificationTime(LocalTime newTime, int checkpointId, int raceId) {
        int initialSize = mountainClassificationTimes.size();
        for (int i = initialSize; i <= raceId; i++) {
            mountainClassificationTimes.add(new ArrayList<>(0));
        }
        initialSize = mountainClassificationTimes.get(raceId).size();
        for (int i = initialSize; i <= checkpointId; i++) {
            mountainClassificationTimes.get(raceId).add(null);
        }
        mountainClassificationTimes.get(raceId).set(checkpointId, newTime);
    }

    /** 
     * updates the mountain classification score for the rider in this race
     * @param newClimbPoints the points added to the rider's score, retrieved from CheckpointType
     * @param raceId the id of the race the stage is in
    */
    public void setMountainClassificationScore(int newClimbPoints, int raceId) {
        int initialSize = mountainClassificationScore.size();
        for (int i = initialSize; i <= raceId; i++) {
            mountainClassificationScore.add(0);
        }
        mountainClassificationScore.set(raceId, mountainClassificationScore.get(raceId) + newClimbPoints);
    }

    //unused, redundant method for when i was initially doing pair programming
    public void setMountainClassificationRank(int mcRank, int raceId) {mountainClassificationRank.set(raceId, mcRank);}

    /** 
     * retrieves an arbitrary climb time for the rider
     * @param checkpointId the checkpoint the rider is currently desiring times from
     * @param raceId the id of the race the stage is in
     * @return the finishing time rider had in the specified climb
    */
    public LocalTime getSpecificClimbTime(int checkpointId, int raceId) {
        if (mountainClassificationTimes.size() <= raceId) {
            return null;
        }
        else if (mountainClassificationTimes.get(raceId).size() <= checkpointId) {
            return null;
        }

        return mountainClassificationTimes.get(raceId).get(checkpointId);
    }

    //methods to retrieve the relevant rider data, should it ever be needed (these are not used in any of the implemented methods)
    public int getMountainClassificationScore(int raceId) {return mountainClassificationScore.get(raceId);}
    public int getMountainClassificationRank(int raceId) {return mountainClassificationRank.get(raceId);}

    //Called when the portal is erased, so that ids can start from 0 again.
    public static void reset() {i = 0;};

    //Constructor
    public Rider(int teamId, String riderName, int yearOfBirth) {
        this.teamId = teamId;
        this.riderName = riderName;
        this.yearOfBirth = yearOfBirth;
        this.riderId = i++;
    }

    
}
