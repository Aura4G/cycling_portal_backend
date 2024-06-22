package cycling;

import java.util.ArrayList;
import java.util.List;

/**
 * Team class
 * While riders are examined and compared individually in the back end,
 * in the context of a bike app, the cyclists must be cast into teams
 * as with any stage race.
 * This class creates teams that have unique team ids,
 * and an initially empty rider list that gets added to.
 * @author Aria Noroozi
 */
public class Team implements java.io.Serializable {
    //instance attributes that each team has
    private int teamId;
    private String teamName;
    private String teamDescription;
    private List<Rider> teamRiders = new ArrayList<>();

    //static integer attribute to increment and give each team a unique id
    private static int i = 0;

    //get methods
    public int getTeamId() {return teamId;}
    public String getTeamName() {return teamName;}
    public String getTeamDescription() {return teamDescription;}
    public Rider[] getRiders() {return teamRiders.toArray(new Rider[0]);}
    public int[] getRiderIds() {
        int[] idArray = new int[teamRiders.size()];
        for (int j = 0; j < teamRiders.size(); j++) {
            idArray[j] = teamRiders.get(j).getRiderId();
        }
        return idArray;
    }

    //adds a rider to a team.
    public void addRider(Rider newRider) {teamRiders.add(newRider);}
    //removes a rider
    public void deleteRider(int index) {teamRiders.set(index, null); teamRiders.remove(index);}

    //Called when the portal is erased, so that ids can start from 0 again.
    public static void reset() {i = 0;};

    //constructor.
    public Team(String teamName, String teamDescription) {
        this.teamName = teamName;
        this.teamDescription = teamDescription;
        this.teamId = i++;
    }
}
