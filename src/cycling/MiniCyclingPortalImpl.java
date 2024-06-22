package cycling;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.time.Duration;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MiniCyclingPortalImpl implements MiniCyclingPortal {
	/** The Master List of Races */
    private List<Race> races = new ArrayList<Race>();
    /** The Master List of teams */
    private List<Team> teams = new ArrayList<Team>();

    @Override
	/** Get the races currently created in the platform. */
	public int[] getRaceIds() {
        int[] raceIds = new int[races.size()];
        for (int i = 0; i < raceIds.length; i++) {
            raceIds[i] = races.get(i).getRaceID();
        }
		return raceIds;
	}

	@Override
	/**creates a staged race in the platform with the given name and description.*/
	public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
		//Exception thrown if the entered name does not abide by the naming conventions for races
        if (name.equals(null) || name.equals("") || name.length() > 30 || name.indexOf(' ') != -1) {
            throw new InvalidNameException("The name of the race entered is invalid. The name must not be empty or have any spaces");
        }
        
        //Exception thrown if the entered name is the name of an existing race.
        for (int i = 0; i < races.size(); i++) {
            if (name.equals(races.get(i).getRaceName())) {
                throw new IllegalNameException("This race's name is already in use.");
            }
        }

        //the race is created and added to the list if no errors apply
        Race newRace = new Race(name, description);
        races.add(newRace);

        //returns the automatically generated id of the new race.
        return newRace.getRaceID();
	}

	@Override
	/**returns the description of the race matching the entered race id*/
	public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
		//Gathers the array of race ids to verify if the entered id is present in the active races
        int[] raceIds = getRaceIds();
        int index = -1;
        for(int i = 0; i < raceIds.length; i++) {
            if (raceId == raceIds[i]) {index = i; break;} //If an element of the array matches the id, the index becomes i
            //the index will point to that location in the race list, so as to gather the decription of that race in the list
        }

        //if the index has not changed, it means the race id is not in the race id array, throwing an exception
        if (index == -1) {throw new IDNotRecognisedException("The entered race ID is not present in the current list of races.");}

        //returns the race description of the race with the index
        return races.get(index).getRaceDescription();
	}

	@Override
	/**removes the race and all its related information, i.e., stages, checkpoints, and results.*/
	public void removeRaceById(int raceId) throws IDNotRecognisedException {
		//variable indicating whether or not the race's id is present
        boolean exists = false;

        //iterates through each element of the race id array, until the ids match
        for (int i = 0; i < getRaceIds().length; i++) {
            if (getRaceIds()[i] == raceId) {
                //the race is removed from the array. it's stages and checkpoints are consequentially inaccessible.
                races.set(i, null);
                races.remove(i);
                Race.deleteRace(i);
                exists = true;
                break;
            }
        }
        //exception thrown if no race is removed
        if (!exists) {throw new IDNotRecognisedException("The entered race ID does not exist in the list of active races.");}
	}

	@Override
	/** returns the stage ids of the stages in an arbitrary race */
	public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
		//index to know where in the list the race resides if present
        int index = -1;

        //iterates through each race id until one matches; the index gets a new value pointing to the race we are checking for stages
        for (int i = 0; i < getRaceIds().length; i++) {
            if (raceId == getRaceIds()[i]) {index = i;}
        }

        //an exception can be thrown if index does not change, indicating the race id is not in the race list
        if (index == -1) {throw new IDNotRecognisedException("The entered race id is not recognised in the list of races.");}

        return races.get(index).getStages().length;
	}

	@Override
	/**
    *With the arguments handed to this method, it creates a stage object 
    *and adds said stage to an existing race object
    */
	public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
			StageType type)
			throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
		//Exception thrown if the value of length is too small
		if (length < 5){
			throw new InvalidLengthException("Invalid Length entered. A race must be at least 5km");
		}
        //Exception thrown if the entered name does not abide by the naming conventions for stages
        if (stageName.equals(null) || stageName.equals("") || stageName.length() > 30 || stageName.indexOf(' ') != -1) {
            throw new InvalidNameException("The name of the stage entered is invalid. The name must not be empty or have any spaces");
        }

        //fetches the index of the race in the list the stage is to be added to
        int[] raceIDs = getRaceIds();
        int index = -1;

        //Iterate through races until raceId matches a number in the race ID array
        for (int i = 0; i < races.size(); i++) {
            if (raceId == getRaceIds()[i]) {
                index = i;
            };
        }

        //Loop to search the stages in the list and determine if the name is already in use
        for (int i = 0; i < raceIDs.length; i++){
            if (raceIDs[i] == index){
                Stage[] stages = races.get(i).getStages();
                for (int j = 0; i < stages.length; i++){
                    if (stages[j].getStageName().equals(stageName)) {
                        throw new IllegalNameException("This stage's name is already in use.");
                    }
                }
                break;
            }
        }

        //Exception for if the race id is not in the array of race ids (if an index of -1 is returned from getRaceListIndex)
        if (index == -1) {
            throw new IDNotRecognisedException("The entered race ID is not present in the current list of races.");
        }
        
        //If no exceptions are thrown, a stage is created and added to the race with the corresponding race Id
        Stage newStage = new Stage(raceId, stageName, description, type, length, startTime);
        races.get(index).addStage(newStage);

        for (Team team : teams) {
            for (Rider rider : team.getRiders()) {
                rider.addPointsClassificationTime(null, newStage.getStageID(), raceId);
            }
        }

        //returns the id of the new stage in it's race
        return newStage.getStageID();
	}

	@Override
	/** Retrieves the list of stage IDs of a race. */
	public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
		//Gathers the array of race ids to verify if the entered id is present in the active races
        int[] raceIds = getRaceIds();
        int index = -1;
        for(int i = 0; i < raceIds.length; i++) {
            if (raceId == raceIds[i]) {index = i; break;} //If an element of the array matches the id, the index becomes i
            //the index will point to that location in the race list, so as to gather the stage ids of that race in the list
        }

        //if the index has not changed, it means the race id is not in the race id array, throwing an exception
        if (index == -1) {throw new IDNotRecognisedException("The entered race ID is not present in the current list of races.");}

        //Two arrays
        //the first array is used to gauge how large the int array should be, as well as to take ids from each stage in the array
        Stage[] stageArray = races.get(index).getStages();
        //this array will have the stage ids entered into it
        int[] stageIdArray = new int[getNumberOfStages(raceId)];
        for (int i = 0; i < getNumberOfStages(raceId); i++) {stageIdArray[i] = stageArray[i].getStageID();}
        return stageIdArray;
	}

	@Override
	/** Retrieves the length of a stage. */
	public double getStageLength(int stageId) throws IDNotRecognisedException {
		//goes through each race until the matching stage id is found
        for (int i = 0; i < races.size(); i++) {
			//uses another function we've made to attain the stage ids of the race with index i
			int[] stageArray = getRaceStages(races.get(i).getRaceID());
			for (int j = 0; j < stageArray.length; j++) {
			    //if the stage id matches, the length of the stage with the corresponding indices is returned.
			    if (stageArray[j] == stageId) {
					return races.get(i).getStages()[j].getLength();
				}
			}
		}
		throw new IDNotRecognisedException("The entered stage ID is not present in any of the races.");
	}

	@Override
	/**Removes a stage and all its related data, i.e., checkpoints and results.*/
	public void removeStageById(int stageId) throws IDNotRecognisedException {
		//gets the race ids
        int[] raceIds = getRaceIds();
        boolean exists = false;
        //each race is searched, as each stage id is completely unique and we are solely using stage id to find the stage
        for (int i = 0; i < raceIds.length; i++) {
            //gets the ids of the stages in the race
            int[] stageArray = getRaceStages(raceIds[i]);
            for (int j = 0; j < stageArray.length; j++) {
                //if the stage ids match, a method is called from it's race's instance, removing it from the stage list.
                if (stageId == stageArray[j]) {
                    races.get(i).deleteStage(j);
                    exists = true;
                    break;
                }
            }
            if (exists) {break;}
        }

        //throws exception if the id does not match at any point during the method.
        if (!exists) {
            throw new IDNotRecognisedException("The entered stage ID is not present in any of the races.");
        }
	}

	@Override
	/**Adds a climb checkpoint to a stage.*/
	public int addCategorizedClimbToStage(int stageId, Double location, CheckpointType type, Double averageGradient,
			Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
			InvalidStageTypeException {
		int[] raceIDs = getRaceIds();
        
        //values to store the indices of the race and stage respectively in the lists
        int tempI = -1;
        int tempJ = -1;

        //searches the race list until a stage with the entered id is present in one of them
        for (int i = 0; i < raceIDs.length; i++){
            Stage[] stages = races.get(i).getStages();
            for (int j = 0; j < stages.length; j++){
                if (stages[j].getStageID() == stageId) {
                    tempI = i;
                    tempJ = j;
                    break;
                }
            }
            if (tempI != -1){
                break;
            }
        }

        //Exception for if the race pointer has not changed value, used this instead of a boolean variable to optimise memory where I see it possible
        if (tempI == -1) {
            throw new IDNotRecognisedException("This stage ID does not exist in any of the races");
        }
        
        //Exception for if the location is negative or greater than the length of the stage
		if (location.equals(null) || location <= 0 || location >= races.get(tempI).getStages()[tempJ].getLength()) {
            throw new InvalidLocationException("The location of the checkpoint is outside of the bounds for the stage length");
        }
        
        //Exception thrown for if the Stage is a time trial
        if (races.get(tempI).getStages()[tempJ].getType() == StageType.TT) {
            throw new InvalidStageTypeException("This stage is an individual Time Trial, it cannot have any checkpoints");
        }

        //Exception for if the stage's state is "waiting for results"
        if (races.get(tempI).getStages()[tempJ].getState() == "waiting for results") {
            throw new InvalidStageStateException("The stage with your entered id is ready and waiting for results. Therefore, the stage can no longer be edited");
        }

        //Creates new checkpoint if no errors are thrown, tempJ is used as the checkpoint
        Checkpoint newCheckpoint = new Checkpoint(stageId, location, type, averageGradient, length);

        //Using tempI and tempJ as pointers to the race and stage respectively, the checkpoint is then added to the stage's checkpoint list.
        races.get(tempI).getStages()[tempJ].addCheckpoint(newCheckpoint);

        //returns the automatically generated checkpoint id, unique to the checkpoint
		return newCheckpoint.getCheckpointId();
	}

	@Override
	/**Adds an intermediate sprint to a stage*/
	public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
			InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		/*
         * Functionality and exception handling are identical to addCategorisedClimbToStage, with some changes:
         * averageGradient and length are set to 0.00, not to be changed
         * the checkpoint type is fixed at SPRINT
        */

        int[] raceIDs = getRaceIds();
        
        int tempI = -1;
        int tempJ = -1;

        for (int i = 0; i < raceIDs.length; i++){

            Stage[] stages = races.get(i).getStages();
            for (int j = 0; j < stages.length; j++){
                if (stages[j].getStageID() == stageId) {
                    tempI = i;
                    tempJ = j;
                    break;
                }
            }
            if (tempI != -1){
                break;
            }
        }

        if (tempI == -1) {
            throw new IDNotRecognisedException("This stage ID does not exist in any of the races");
        }

        Double locationObject = location;

		if (locationObject.equals(null) || location <= 0 || location >= races.get(tempI).getStages()[tempJ].getLength()) {
            throw new InvalidLocationException();
        }
        
        if (races.get(tempI).getStages()[tempJ].getType() == StageType.TT) {
            throw new InvalidStageTypeException();
        }

        if (races.get(tempI).getStages()[tempJ].getState() == "waiting for results") {
            throw new InvalidStageStateException("The stage with your entered id is ready for the race and is waiting for results. Therefore, it can no longer be altered.");
        }

        Checkpoint newCheckpoint = new Checkpoint(stageId, location, CheckpointType.SPRINT, 0.00, 0.00);
        races.get(tempI).getStages()[tempJ].addCheckpoint(newCheckpoint);

        return newCheckpoint.getCheckpointId();
	}

	@Override
	/**removes a checkpoint from a stage*/
	public void removeCheckpoint(int checkpointId) throws IDNotRecognisedException, InvalidStageStateException {
		//Retrieves the race ids to further identify the stage in which the checkpoint lies
        int[] raceIds = getRaceIds();
        //boolean variable to verify if the entered id exists in the lists.
        boolean exists = false;
        for (int i = 0; i < raceIds.length; i++) {
            //retrieves the stage ids
            int[] stageArray = getRaceStages(raceIds[i]);
            for (int j = 0; j < stageArray.length; j++) {
                //then retrieves the ids of that stage and iterates through each id
                int[] checkpointArray = getStageCheckpoints(stageArray[j]);
                for (int k = 0; k < checkpointArray.length; k++) {
                    //if the id is found, the checkpoint is removed from the stage and all loops are broken
                    if (checkpointArray[k] == checkpointId) {
                        if (races.get(i).getStages()[j].getState() == "waiting for results") {
                            throw new InvalidStageStateException("The stage with your entered id is ready and is waiting for results. It can no longer be altered");
                        }
                        races.get(i).getStages()[j].deleteCheckpoint(k);
                        exists = true;
                        break;
                    }
                }
                if (exists) {break;}
            }
            if (exists) {break;}
        }
        //if the condition in the innermost loop is not carried out at any point in the method, an exception is thrown
        if (!exists) {
            throw new IDNotRecognisedException("The entered checkpoint ID is not present in any of the stages.");
        }

	}

	@Override
	/**method to signify when a stage can no longer be altered and is now ready for the riders to race on accurately*/
	public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
		//Retrieves the race ids to further identify the stage
        int[] raceIds = getRaceIds();
        //boolean variable to verify if the entered id exists in the lists.
        boolean exists = false;
        for (int i = 0; i < raceIds.length; i++) {
            //retrieves the stage ids
            int[] stageArray = getRaceStages(raceIds[i]);
            for (int j = 0; j < stageArray.length; j++) {
                //if ids match,
                if (stageId == stageArray[j]) {
                    //if stage preparations have already ended, nothing is changed and an exception is thrown.
                    if (races.get(i).getStages()[j].getState() == "waiting for results") {
                        throw new InvalidStageStateException("The stage preparations have already concluded.");
                    }
                    //otherwise, the stage preparations are closed and results can be recorded for each rider.
                    races.get(i).getStages()[j].endStage();
                    exists = true;
                }
            }
            if (exists) {break;}
        }
        //if the condition in the innermost loop is not carried out at any point in the method, an exception is thrown
        if (!exists) {
            throw new IDNotRecognisedException("The entered stage ID is not present in any of the stages.");
        }

	}

	@Override
	/** Retrieves the list of checkpoint (mountains and sprints) IDs of a stage. */
	public int[] getStageCheckpoints(int stageId) throws IDNotRecognisedException {
		//goes through each race until the matching stage id is found
        for (int i = 0; i < races.size(); i++) {
            //uses another function we've made to attain the stage ids of the race with index i
            int[] stageArray = getRaceStages(races.get(i).getRaceID());
            for (int j = 0; j < stageArray.length; j++) {
                //if the stage id matches, an array is created, placing the ids of the checkpoints in that stage into said array
                if (stageArray[j] == stageId) {
                    int[] checkpointArray = new int[races.get(i).getStages()[j].getCheckpoints().length];
                    for (int k = 0; k < races.get(i).getStages()[j].getCheckpoints().length; k++) {
                        checkpointArray[k] = races.get(i).getStages()[j].getCheckpoints()[k].getCheckpointId();
                    }
                    return checkpointArray;
                }
            }
        }
        throw new IDNotRecognisedException("The entered stage ID is not present in any of the races.");
	}

	@Override
	/**creates a staged race in the platform with the given name and description.*/
	public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
		//Exception thrown if the entered name does not abide by the naming conventions for races
        if (name.equals(null) || name.equals("") || name.length() > 30 || name.indexOf(' ') != -1) {
            throw new InvalidNameException("The name of the team entered is invalid. The name must not be empty or have any spaces");
        }
        
        //Exception thrown if the entered name is the name of an existing race.
        for (int i = 0; i < races.size(); i++) {
            if (name.equals(races.get(i).getRaceName())) {
                throw new IllegalNameException("This team's name is already in use.");
            }
        }

        //the race is created and added to the list if no errors apply
        Team newTeam = new Team(name, description);
        teams.add(newTeam);

        //returns the automatically generated id of the new race.
        return newTeam.getTeamId();
	}

	@Override
	/**removes the race and all its related information, i.e., stages, checkpoints, and results.*/
	public void removeTeam(int teamId) throws IDNotRecognisedException {
		//variable indicating whether or not the team's id is present
        boolean exists = false;

        //iterates through each element of the team id array, until the ids match
        for (int i = 0; i < getTeams().length; i++) {
            if (getTeams()[i] == teamId) {
                //the team is removed from the array. it's riders are consequentially inaccessible.
                teams.remove(i);
                exists = true;
                break;
            }
        }
        //exception thrown if no team is removed
        if (!exists) {throw new IDNotRecognisedException("The entered race ID does not exist in the list of active races.");}

	}

	@Override
	/** gets the team id of each team in the teams list, in the order they arrive in the list */
	public int[] getTeams() {
		int[] teamIdsArray = new int[teams.size()];
        for (int i = 0; i < teamIdsArray.length; i++) {
            teamIdsArray[i] = teams.get(i).getTeamId();
        }
        return teamIdsArray;
	}

	@Override
	/** finds a team in the teams list matching the team id and gets an array of the rider ids from the team */
	public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
		for (int i = 0; i < getTeams().length; i++) {
            if (teams.get(i).getTeamId() == teamId) {
                return teams.get(i).getRiderIds();
            }
        }
        throw new IDNotRecognisedException("The team id entered is not present for any of the currently active teams in the list.");
	}

	@Override
	/**creates a new rider and adds them to an existing team*/
	public int createRider(int teamID, String name, int yearOfBirth)
			throws IDNotRecognisedException, IllegalArgumentException {
		//Argument exception if the name is empty or the rider is not realistically alive
        if (name.equals(null) || name.equals("") || yearOfBirth < 1900) {
            throw new IllegalArgumentException("The name is empty or the year of birth of the rider is less than 1900.");
        }
        
        //index to call a team the id points to 
        int index = -1;

        for (int i  =  0 ; i < getTeams().length; i++) {
            if (teamID == getTeams()[i]) {
                index = i;
                break;
            }
        }

        //if the index does not change, an exception is thrown
        if (index == -1) {throw new IDNotRecognisedException("The entered team ID for the new rider does not exist in the list of teams.");}
        
        //creates new rider and adds said rider to the team the entered id points to
        Rider newRider = new Rider(teamID, name, yearOfBirth);
        teams.get(index).addRider(newRider);

        return newRider.getRiderId();
	}

	@Override
	/**removes a rider from its team*/
    public void removeRider(int riderId) throws IDNotRecognisedException{
        //gets the team ids
        int[] teamIds = getTeams();
        boolean exists = false;
        //each team is searched, as each rdier id is completely unique and we are solely using rider id to find the stage
        for (int i = 0; i < teamIds.length; i++) {
            //gets the ids of the riders in a team
            int[] riderArray = getTeamRiders(teamIds[i]);
            for (int j = 0; j < riderArray.length; j++) {
                //if the rider ids match, a method is called from it's team's instance, removing it from the team list.
                if (riderId == riderArray[j]) {
                    teams.get(i).deleteRider(j);
                    exists = true;
                    break;
                }
            }
            if (exists) {break;}
        }

        //throws exception if the id does not match at any point during the method.
        if (!exists) {
            throw new IDNotRecognisedException("The entered checkpoint ID is not present in any of the stages.");
        }
    }

	@Override
	/**sets the adjusted elapsed times and climb times for a rider in a stage*/
	public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
			throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException,
			InvalidStageStateException {
		//Retrieves the race ids to further identify the stage
        int[] raceIds = getRaceIds();
        //boolean variable to verify if the entered id exists in the lists.
        int tempI = -1;
        int tempJ = -1;

        int raceId = -1;

        for (int i = 0; i < raceIds.length; i++) {
            //retrieves the stage ids
            int[] stageArray = getRaceStages(raceIds[i]);
            for (int j = 0; j < stageArray.length; j++) {
                //if ids match,
                if (stageId == stageArray[j]) {
                    tempI = i;
                    tempJ = j;
                    raceId = raceIds[i];
                }
            }
            if (tempI != -1) {break;}
        }

        //if the condition in the innermost loop is not carried out at any point in the method, an exception is thrown
        if (tempI == -1) {
            throw new IDNotRecognisedException("The entered stage ID is not present in any of the stages.");
        }

        //if the stage is still in preparation and not waiting for results, an exception is thrown
        if (races.get(tempI).getStages()[tempJ].getState() != "waiting for results") {
            throw new InvalidStageStateException("The stage you are requesting is still marked as in preparation.");
        }

        //If the checkpoints length does not equal with the number of checkpoints in the stage, an exception is thrown
        if (races.get(tempI).getStages()[tempJ].getCheckpoints().length != checkpoints.length - 2) {
            throw new InvalidCheckpointTimesException("The length of the entered checkpoint array and the number of checkpoints, including the start and end, are not equal.");
        }

        boolean exists = false;
        //Searches every team and rider until the rider with matching id is found
        for (Team team : teams) {
            for (Rider rider : team.getRiders()) {
                if (rider.getRiderId() == riderId) {
                    //If there is already a value for the stage time, an exception is thrown.
                    if (rider.getSpecificStageTime(stageId, tempI) != null) {
                        throw new DuplicatedResultException("A valid entry has already been made for this rider.");
                    }
                    //using the duration java library to calculate the difference between the start time and the rider's finish
                    Duration duration = Duration.between(races.get(tempI).getStages()[tempJ].getStartTime().toLocalTime(), checkpoints[checkpoints.length - 1]);
                    //in order for this value to be a LocalTime, it is split into hours minutes and seconds
                    long hours = duration.toHours();
                    long minutes = duration.toMinutes() % 60;
                    long seconds = duration.getSeconds() % 60;
                    //It is then converted here.
                    LocalTime adjustedTime = LocalTime.of((int) hours, (int) minutes, (int) seconds);

                    //The time is added to the list of the race with matching race id, in the position of the index equal to the stage id
                    rider.addPointsClassificationTime(adjustedTime, stageId, raceId);
                    //The same is done for each checkpoint

                    for (int i = 1; i < checkpoints.length - 1; i++) {
                        duration = Duration.between(races.get(tempI).getStages()[tempJ].getStartTime().toLocalTime(), checkpoints[i]);
                        hours = duration.toHours();
                        minutes = duration.toMinutes() % 60;
                        seconds = duration.getSeconds() % 60;
                        adjustedTime = LocalTime.of((int) hours, (int) minutes, (int) seconds);

                        rider.addMountainClassificationTime(adjustedTime, getStageCheckpoints(stageId)[i-1], raceId);
                    }
                    exists = true;
                    break;
                }
            }
            if (exists) {break;}
        }
        if (!exists) {throw new IDNotRecognisedException("The rider id is not present in any active teams.");}

	}

	@Override
    /**Lists all the times the rider reached each checkpoint and the end of the stage */
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        //iterates through each race until a stage matching the entered id is found
		for (Race race : races) {
            for (Stage stage : race.getStages()) {
                if (stageId == stage.getStageID()) {
                    LocalTime[] checkpointTimes = new LocalTime[stage.getCheckpoints().length + 1];
                    //iterates through each team until a rider matching the entered rider id is found
                    for (Team team : teams) {
                        for (Rider rider : team.getRiders()) {
                            if (riderId == rider.getRiderId()) {
                                //iterates through each checkpoint in the stage and puts the rider's time for that checkpoint into an array
                                for (int i = 0; i < stage.getCheckpoints().length; i++) {
                                    checkpointTimes[i] = rider.getSpecificClimbTime(stage.getCheckpoints()[i].getCheckpointId(), race.getRaceID());
                                }
                                checkpointTimes[checkpointTimes.length - 1] = getRiderAdjustedElapsedTimeInStage(stageId, riderId);
                                return checkpointTimes;
                            }
                        }
                    }
                    //exception thrown if rider id is not found
                    throw new IDNotRecognisedException("The rider id is not present in any active teams.");
                }
            }
        }
        //exception thrown if stage id is not recognised
        throw new IDNotRecognisedException("The stage id is not present in any active races.");
	}

	@Override
	/** Get the stage time of a specific rider */
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
		//integer to determine the id of the race the stage is present in
		int raceId = -1;

		for (int i : getRaceIds()) {
			for (int j : getRaceStages(i)) {
				if (j == stageId) {
					raceId = i;
					break;
				}
			}
			if (raceId != -1) {break;}
		}
 
		//throws error if the stage id is not matched at any point (evidenced in raceId staying at -1)
		if (raceId == -1) {throw new IDNotRecognisedException("The entered stage ID is not present in any of our active races.");}
		 
		//Goes through each team and every rider in each team until one with a matching rider id is found
		for (Team team : teams) {
			for (int i = 0; i < getTeamRiders(team.getTeamId()).length; i++) {
				if (team.getRiderIds()[i] == riderId) {
					//At which point their stage time is returned
					return team.getRiders()[i].getSpecificStageTime(stageId, raceId);
				}
			}
		}
		throw new IDNotRecognisedException("The entered rider id was not recognised in the active list of teams and riders.");
	}

	@Override
	/**method to remove a selected rider's results in a selected stage*/
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		//integer to determine the id of the race the stage is present in
        int raceId = -1;
        int tempI = -1;
        int tempJ = -1;

        for (int i : getRaceIds()) {
            tempI++;
            for (int j : getRaceStages(i)) {
                tempJ++;
                if (j == stageId) {
                    raceId = i;
                    break;
                }
            }
            if (raceId != -1) {break;}
        }

        //throws error if the stage id is not matched at any point (evidenced in raceId staying at -1)
        if (raceId == -1) {throw new IDNotRecognisedException("The entered stage ID is not present in any of our active races.");}
        
        //Goes through each team and every rider in each team until one with a matching rider id is found
        for (Team team : teams) {
            for (int i = 0; i < getTeamRiders(team.getTeamId()).length; i++) {
                if (team.getRiderIds()[i] == riderId) {
                    //At which point their stage time and checkpoint times are set to null
                    team.getRiders()[i].addPointsClassificationTime(null, stageId, raceId);
                    for (int j = 1; j < races.get(tempI).getStages()[tempJ].getCheckpoints().length - 1; j++) {
                        team.getRiders()[i].addMountainClassificationTime(null, stageId, raceId);
                    }
                }
            }
        }
        throw new IDNotRecognisedException("The entered rider id was not recognised in the active list of teams and riders.");

	}

	@Override
	/**distributes stage ranking based on the elapsed times in a stage */
	public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
		for (Race race : races) {
            for (int i : getRaceStages(race.getRaceID())) {
                if (stageId == i) {
                    LocalTime[] stageTimes = getRankedAdjustedElapsedTimesInStage(stageId);

                    //stores the ids of the riders in the order the reached the end
                    int[] stageRankedRiders = new int[stageTimes.length];
                    java.util.Arrays.sort(stageTimes);
                    
                    int rankIndex = 0;
                    //the ids of the riders are taken in the new order and given a second index value, this time being their rank for the race.
                    //it was easier to determine every rider's rank in this function as opposed to getRidersGeneralClassification
                    for (Team team : teams) {
                        for (Rider rider : team.getRiders()) {
                            while (rider.getSpecificStageTime(stageId, race.getRaceID()) != stageTimes[rankIndex]) {
                                rankIndex++;
                            }
                            stageRankedRiders[rankIndex] = rider.getRiderId();
                            rankIndex = 0;
                        }
                    }
                    return stageRankedRiders;
                }
            }
        }
        
        throw new IDNotRecognisedException("The race id entered does not match any in the active race list.");
	}

	@Override
	/** Get the times of riders participating in an arbitrary stage */
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
		//integer to determine the id of the race the stage is present in
        int raceId = -1;

        for (int i : getRaceIds()) {
            for (int j : getRaceStages(i)) {
                if (j == stageId) {
                    raceId = i;
                    break;
                }
            }
            if (raceId != -1) {break;}
        }

        //throws error if the stage id is not matched at any point (evidenced in raceId staying at -1)
        if (raceId == -1) {throw new IDNotRecognisedException("The entered stage ID is not present in any of our active races.");}
        
        //Accumulates the times each rider took in a stage. It is a list at this point as we cannot determine how many riders are partaking
        List<LocalTime> stageTimes = new ArrayList<>();
        //For every team in the list
        for (Team team : teams) {
            //for every rider in the team
            for (Rider rider : team.getRiders()) {
                //Add the rider's time from their instance, to the list
                stageTimes.add(rider.getSpecificStageTime(stageId, raceId));
            }
        }
        //Converts the list into a LocalTime Array
        return stageTimes.toArray(new LocalTime[0]);
	}

	@Override
	/** gets the scores of each rider within a stage for points classification */
	public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
		//The amount of scores higher than 0 on offer, irrespective of the stage type, will be 15
        final int WINNING_POSITIONS = 15;

        //the points are distributed by rank
        int[] rankedRiders = getRidersRankInStage(stageId);
        StageType type;

        //the list of earnt points for this stage per rider
        List<Integer> riderEarnedPoints = new ArrayList<>();

        //index incrementing to select each element of rankedRiders
        

        //iterates through each stage until one matches
        for (Race race : races) {
            for (int i = 0 ; i < getRaceStages(race.getRaceID()).length; i++) {
                if (stageId == race.getStages()[i].getStageID()) {
                    //StageType is acquired. we can now get points from the enum and distribute accordingly
                    type = race.getStages()[i].getType();
                    for (Team team : teams) {
                        //finds matching rider ids and adds points accordingly
                        for (Rider rider : team.getRiders()) {
                            for (int index = 0; index < rankedRiders.length; index++) {
                                if (rider.getRiderId() == rankedRiders[index]) {
                                    //if there are still winning positions, points are earnt
                                    if (index < WINNING_POSITIONS) {
                                        riderEarnedPoints.add(type.getPoints(index));
                                        rider.setPointsClassificationScore(type.getPoints(index), race.getRaceID());
                                    }
                                    //otherwise, nothing is earnt
                                    else {
                                        riderEarnedPoints.add(0);
                                        rider.setPointsClassificationScore(0, race.getRaceID());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    Collections.sort(riderEarnedPoints, Collections.reverseOrder());

                    //turns the earnedPoints list into an array and returns it
                    int[] pointsArray = riderEarnedPoints.stream().mapToInt(Integer::intValue).toArray();
                    return pointsArray;
                }
            }
        }
        //id thrown if stage id is not matched at any given point
        throw new IDNotRecognisedException("The stage id entered does not match any in the active race lists.");
	}

	@Override
	/**gets the climb points of each rider amassed within a stage for mountain classification*/
	public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
        List<List<LocalTime>> riderTimesPerCheckpoint = new ArrayList<>(); //nested lists created, storing all the times per checkpoint
        List<Integer> ridersPointsThisStage = new ArrayList<>(); // the accumulated points each rider has for this stage in particular
        int totalCheckpoints = 0; //keeps track of how many checkpoints are being examined
        CheckpointType type; //the type of checkpoint is recorded per iteration to make sure points are given accordingly

        //iterates through the stage's checkpoints, adding empty lists to the master times lists
        for (Race race : races) {
            for (Stage stage : race.getStages()) {
                if (stage.getStageID() == stageId) {
                    for (Checkpoint checkpoint : stage.getCheckpoints()) {
                        riderTimesPerCheckpoint.add(new ArrayList<>(0));
                        totalCheckpoints++;
                    }
                }
            }
        }

        for (Team team : teams) {
            for (Rider rider : team.getRiders()) {
                ridersPointsThisStage.add(0);
            }
        }

        if (totalCheckpoints == 0) {
            throw new IDNotRecognisedException("The stage id entered was not recognised or there are no checkpoints in this stage");
        }

        //iterates through each stage until a matching id is found
        for (Race race : races) {
            for (Stage stage : race.getStages()) {
                if (stage.getStageID() == stageId) {
                    //adds the times each rider got in each checkpoint to a nested list. the index of the nested list represents the checkpoint
                    for (int i = 0; i < riderTimesPerCheckpoint.size(); i++) {
                        for (Team team : teams) {
                            for (Rider rider : team.getRiders()) {
                                riderTimesPerCheckpoint.get(i).add(rider.getSpecificClimbTime(getStageCheckpoints(stageId)[i], race.getRaceID()));
                            }
                        }
                        Collections.sort(riderTimesPerCheckpoint.get(i));
                    }

                    int counter = 0;

                    /** 
                     * iterates through each rider and compares the checkpoint times in that list to the rider's individual times
                     * if a match is made, an index that has been incrementing and resetting accordingly with each loop will be
                     * used as a pointer to an element in the enum array matching the checkpoint type, retrieving the points
                     * that are then added to the rider's total
                    */
                    for (int j = 0; j < totalCheckpoints; j++) {
                        for (Team team : teams) {
                            for (Rider rider : team.getRiders()) {
                                for (int i = 0; i < riderTimesPerCheckpoint.get(j).size(); i++) {
                                    type = stage.getCheckpoints()[i].getType();
                                    if (rider.getSpecificClimbTime(stage.getCheckpoints()[j].getCheckpointId(), race.getRaceID()) == riderTimesPerCheckpoint.get(j).get(i)) {
                                        if (type.getCheckPointType().length > i) {
                                            rider.setPointsClassificationScore(type.getPoints(i), race.getRaceID());
                                            int oldValue = ridersPointsThisStage.get(counter);
                                            int newValue = oldValue + type.getPoints(i);
                                            ridersPointsThisStage.set(counter, newValue);
                                        }
                                        else {
                                            rider.setPointsClassificationScore(0, race.getRaceID());
                                        }
                                        
                                        break;
                                    }
                                    
                                }

                                counter++;

                                if (counter == ridersPointsThisStage.size()) {
                                    counter = 0;
                                }
                                
                            }
                        }
                        counter = 0;
                    }
                    Collections.sort(ridersPointsThisStage, Collections.reverseOrder());

                    int[] pointsArray = ridersPointsThisStage.stream().mapToInt(Integer::intValue).toArray();
                    return pointsArray;
                }
            }
        }

        //exception thrown if the stage is never found
        throw new IDNotRecognisedException("The stage id entered does not match any in the active race lists.");
	}

	@Override
	/** Sets all the lists and objects to null */
	public void eraseCyclingPortal() {
		//erase all checkpoints, stages, races
        for (Race race : races) {
            for (Stage stage : race.getStages()) {
                for (Checkpoint checkpoint : stage.getCheckpoints()) {
                    checkpoint = null;
                }
                stage = null;
            }
            race = null;
        }
        races = null;

        //erase all riders, teams
        for (Team team : teams) {
            for (Rider rider : team.getRiders()) {
                rider = null;
            }
            team = null;
        }
        teams = null;

        //Sets the indices that determine ids back to 0.
        Checkpoint.reset();
        Stage.reset();
        Race.reset();
        Team.reset();
        Rider.reset();

	}

	@Override
	/** Writes all of the races, stages, checkpoints, teams and riders to a serialised file */
	public void saveCyclingPortal(String filename) throws IOException {
		//output stream initialised to write contents to the file
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));

        //Lists are created for stages, checkpoints and riders, so as to partition the data classes entered in the file
        //This makes the contents easier to read and plug in to the portal
        //The master lists for races and teams are also used for data collection
        List<Stage> stages = new ArrayList<Stage>();
        List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
        List<Rider> riders = new ArrayList<Rider>();

        //For every race
        for (Race race : races) {
            //For every stage within that race
            for (Stage stage : race.getStages()) {
                //Every checkpoint in that stage is place into the checkpoint array
                for (Checkpoint checkpoint : stage.getCheckpoints()) {
                    checkpoints.add(checkpoint);
                }
                //The stage is added to the stage list
                stages.add(stage);
            }
        }

        for (Team team : teams) {
            //The riders in every team are added to a list
            for (Rider rider : team.getRiders()) {
                riders.add(rider);
            }
        }

        //A data container object is created, holding the lists just made, along with the lists for races and teams that sits outside the scope
        DataContainer data = new DataContainer(races, stages, checkpoints, teams, riders);

        //This data is written to the created file
        outputStream.writeObject(data);

        //Good practise to always close the streamer once it is no longer needed
        outputStream.close();

        System.out.println("The portal was saved successfully!");

	}

	@Override
	/** Reads a file and restores it's object instances to their respective places. */
	public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
		//eradicates the previous portal in place of the loaded one
        eraseCyclingPortal();

        //Input stream as data is being inputted into the backend and fed to data structures
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));
        //Data container, this time to store the lists from the file
        DataContainer data = (DataContainer) inputStream.readObject();
        //And gets the lists back to their respective positions
        //The race and team lists can get their objects back easily
        races = data.getRaces();
        teams = data.getTeams();

        //new lists must be created for stages, checkpoints and riders
        //this is because the lists for these classes exist with in races, teams, etc.
        List<Stage> stages = data.getStages();
        List<Checkpoint> checkpoints = data.getCheckpoints();
        List<Rider> riders = data.getRiders();

        //The data has all been read, so the input stream can be closed.
        inputStream.close();
        
        for (Team team : teams) {
            //if a position in the teams list is null, it skips entering any data for teams there
            if (team != null) {
                for (Rider rider : riders) {
                    //if a position in the rider list is null, it skips adding any data to a team object there
                    if (rider != null) {
                        //every rider is checked against the team by way of sharing a team id
                        if (rider.getTeamId() == team.getTeamId()) {
                            //if they match, the rider is added to the team
                            team.addRider(rider);
                        }
                    }
                }
            }
        }

        for (Stage stage : stages) {
            if (stage != null) {
                //if a position in the stages list is null, it skips entering any data for stages there
                for (Checkpoint checkpoint : checkpoints) {
                    //if a position in the checkpoints list is null, it skips adding any data to a stage object there
                    if (checkpoint != null) {
                        //every checkpoint is checked against the stage by way of sharing a stage id
                        if (checkpoint.getStageID() == stage.getStageID()) {
                            //if they match, the checkpoint is added to the stage
                            stage.addCheckpoint(checkpoint);
                        }
                    }
                }
            }
        }

        for (Race race: races) {
            if (race != null) {
                //if a position in the races list is null, it skips entering any data for stages there
                for (Stage stage : stages) {
                    //if a position in the stage list is null, it skips adding any data to a race object there
                    if (stage != null) {
                        //every stage is checked against the race by way of sharing a race id
                        if (race.getRaceID() == stage.getRaceID()) {
                            //if they match, the stage is added to the race
                            race.addStage(stage);
                        }
                    }
                }
            }
        }
	}
}
