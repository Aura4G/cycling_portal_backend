package cycling;

import java.util.List;

/**
 * This object is used to store lists of each object type I have made
 * for when data is being written and read
 * @author Aria Noroozi
 */
public class DataContainer implements java.io.Serializable{
    //A list per class
    private List<Race> races;
    private List<Stage> stages;
    private List<Checkpoint> checkpoints;
    private List<Team> teams;
    private List<Rider> riders;

    //Get methods, used for when the file is being read from
    public List<Race> getRaces() {return races;}
    public List<Stage> getStages() {return stages;}
    public List<Checkpoint> getCheckpoints() {return checkpoints;}
    public List<Team> getTeams() {return teams;}
    public List<Rider> getRiders() {return riders;}

    //Constructor
    public DataContainer(List<Race> races, List<Stage> stages, List<Checkpoint> checkpoints, List<Team> teams, List<Rider> riders) {
        this.races = races;
        this.stages = stages;
        this.checkpoints = checkpoints;
        this.teams = teams;
        this.riders = riders;
    }
}
