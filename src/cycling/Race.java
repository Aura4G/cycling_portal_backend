package cycling;

import java.util.ArrayList;
import java.util.List;

/**
*  Race class to make individual races, as well as chart an array of unique race IDs that can be called upon as identifiers
*  @author Aria Noroozi
*/
public class Race implements java.io.Serializable {
    /*
      initialise instance attributes for the race's name, description, ID and stages
      The ID and stages are not set by the user: The ID is given automatically and not to be changed
      The stage list starts empty but gets filled up when addStageToRace is correctly called
    */
    private String raceName;
    private String raceDescription;
    private int raceID;
    private List<Stage> stages = new ArrayList<>();

    /*
      Static attributes initialised to account for the number of races and ensure a unique ID is generated
      The IDs are to be put into a list that can be checked to verify races
    */
    private static int numberOfRaces = 0;
    private static List<Integer> raceIDs = new ArrayList<>();

    //Get methods
    public String getRaceName() {return raceName;}
    public String getRaceDescription() {return raceDescription;}
    public int getRaceID() {return raceID;}
    public Stage[] getStages() {return stages.toArray(new Stage[0]);}

    //Adds the new stage from addStageToRace to the race instance's stage list as the next stage in the list.
    public void addStage(Stage newStage) {this.stages.add(newStage);}
    public void deleteStage(int index) {this.stages.set(index, null); this.stages.remove(index);}

    //decrements the race count and removes the Id of the race from the Id's list
    public static void deleteRace(int raceId) {
      raceIDs.set(raceId, null);
      raceIDs.remove(raceId);
    }

    //Called when the portal is erased, so that ids can start from 0 again.
    public static void reset() {numberOfRaces = 0;};

    //Constructor for the race, along with updating the static list of race IDs
    public Race(String raceName, String raceDescription) {
        this.raceName = raceName;
        this.raceDescription = raceDescription;
        raceID = numberOfRaces++;
        raceIDs.add(raceID);
    }
}
