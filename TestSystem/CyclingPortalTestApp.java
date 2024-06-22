import cycling.MiniCyclingPortalImpl;
import cycling.NameNotRecognisedException;
import cycling.StageType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import cycling.CheckpointType;
import cycling.DuplicatedResultException;
import cycling.IDNotRecognisedException;
import cycling.IllegalNameException;
import cycling.InvalidCheckpointTimesException;
import cycling.InvalidLengthException;
import cycling.InvalidLocationException;
import cycling.InvalidNameException;
import cycling.InvalidStageStateException;
import cycling.InvalidStageTypeException;
import cycling.MiniCyclingPortal;

/**
 * A short program to illustrate an app testing some minimal functionality of a
 * concrete implementation of the CyclingPortal interface -- note you
 * will want to increase these checks, and run it on your CyclingPortalImpl class
 * (not the BadCyclingPortal class).
 *
 * 
 * @author Diogo Pacheco
 * @version 2.0
 */
public class CyclingPortalTestApp {

	/**
	 * Test method.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		System.out.println("The system compiled and started the execution...");

		MiniCyclingPortal portal1 = new MiniCyclingPortalImpl();
		MiniCyclingPortal portal2 = new MiniCyclingPortalImpl();

		assert (portal1.getRaceIds().length == 0)
				: "Innitial Portal not empty as required or not returning an empty array.";
		assert (portal1.getTeams().length == 0)
				: "Innitial Portal not empty as required or not returning an empty array.";

		try {
			portal1.createTeam("TeamOne", "My favorite");
			portal2.createTeam("TeamOne", "My favorite");
		} catch (IllegalNameException e) {
			e.printStackTrace();
		} catch (InvalidNameException e) {
			e.printStackTrace();
		}

		assert (portal1.getTeams().length == 1)
				: "Portal1 should have one team.";

		assert (portal2.getTeams().length == 1)
				: "Portal2 should have one team.";
		
		try {
            portal1.createRace("Joinville_tourney", "test");
            portal2.createRace("Devon_loop", "Journey around the county of Devon");
            portal1.createRace("Assburn_hills", "Cycle through the Swedish Highlands of Assburn");
            //Should print "0 2"
            for (int i = 0; i < portal1.getRaceIds().length; i++) {System.out.print(portal1.getRaceIds()[i] + " ");}
            System.out.println("");

			//should print 1
			for (int i = 0; i < portal2.getRaceIds().length; i++) {System.out.print(portal2.getRaceIds()[i] + " ");}
            System.out.println("");

            //creates and adds 3 stages to the race with id 1
            portal1.addStageToRace(0, "exe_to_exm", "11 miles from exeter to exmouth", 17.70, LocalDateTime.of(2024, 3, 3, 7, 30), StageType.MEDIUM_MOUNTAIN);
            portal1.addStageToRace(2, "exm_to_sdm", "13.9 miles from exmouth to sidmouth", 22.37, LocalDateTime.of(2024, 3, 4, 6, 30), StageType.FLAT);
            portal1.addStageToRace(2, "sdm_to_sen", "10 miles from sidmouth to seaton", 16.09, LocalDateTime.of(2024, 3, 5, 8, 0), StageType.FLAT);
            //Should print "1 2"
            for (int i = 0; i < portal1.getNumberOfStages(2); i++) {System.out.print(portal1.getRaceStages(2)[i] + " ");}
            System.out.println("");

            //creates and adds 2 checkpoints to the stage with id 0. This stage resides in the race with id 1
            portal1.addCategorizedClimbToStage(0, 3.12, CheckpointType.C4, 0.5, 6.0);
            portal1.addCategorizedClimbToStage(0, 11.34, CheckpointType.C2, 10.0, 4.0);
            //creates and adds another checkpoint to the stage with id 1
            portal1.addIntermediateSprintToStage(1, 12.40);
            //should print "0 1"
            for (int i = 0; i < portal1.getStageCheckpoints(0).length; i++) {System.out.print(portal1.getStageCheckpoints(0)[i] + " ");}
            System.out.println("");

            //finds and removes the checkpoint with id 0 from its stage, which has id 0
            portal1.removeCheckpoint(0);
            //should print "1"
            for (int i = 0; i < portal1.getStageCheckpoints(0).length; i++) {System.out.print(portal1.getStageCheckpoints(0)[i] + " ");}
            System.out.println("");

			//should print "Cycle through the swedish highlands of Assburn"
            System.out.println(portal1.viewRaceDetails(2));

            System.out.println("Stage id 2 length: " + portal1.getStageLength(2));

            //removes the race with id 2
            portal1.removeRaceById(2);
            //should print "0"
            for (int i = 0; i < portal1.getRaceIds().length; i++) {System.out.print(portal1.getRaceIds()[i] + " ");}
            System.out.println("");
            System.out.println("");  

            //Should print "0"
            for (int i = 0; i < portal1.getTeams().length; i++) {System.out.print(portal1.getTeams()[i] + " ");}
            System.out.println("");

            //Create 3 riders and place them into teams
            portal1.createRider(0, "Bikel_Schumacher", 1987);
            portal1.createRider(0, "Whatsapp_Rider", 2001);
            portal1.createRider(0, "Sukuna", 1975);
            //Should print "0 1 2"
            for (int i = 0; i < portal1.getTeamRiders(0).length; i++) {System.out.print(portal1.getTeamRiders(0)[i] + " ");}
            System.out.println("");
			System.out.println("");

			portal1.concludeStagePreparation(0);

			portal1.registerRiderResultsInStage(0, 2, LocalTime.of(7, 30, 00), LocalTime.of(8,45, 00), LocalTime.of(12, 41, 59));
            System.out.println("Rider with id 2, finishing time: " + portal1.getRiderAdjustedElapsedTimeInStage(0, 2));

            portal1.registerRiderResultsInStage(0, 1, LocalTime.of(7, 30, 00), LocalTime.of(9,12, 15), LocalTime.of(14, 02, 37));
            System.out.println("Rider with id 1, finishing time: " + portal1.getRiderAdjustedElapsedTimeInStage(0, 1));

            portal1.registerRiderResultsInStage(0, 0, LocalTime.of(7, 30, 00), LocalTime.of(8,27, 9), LocalTime.of(12, 10, 48));
            System.out.println("Rider with id 0, finishing time: " + portal1.getRiderAdjustedElapsedTimeInStage(0, 0));

            System.out.println("");

            System.out.println("The rider times for stage 0 come as follows: " + Arrays.toString(portal1.getRankedAdjustedElapsedTimesInStage(0)));
            System.out.println("");

            System.out.println(Arrays.toString(portal1.getRidersRankInStage(0)));

            System.out.println(Arrays.toString(portal1.getRidersPointsInStage(0)));

            System.out.println(Arrays.toString(portal1.getRiderResultsInStage(0, 1)));

            portal1.removeRider(1);

            portal1.addStageToRace(0, "balls", "ambatukum", 14.23, LocalDateTime.of(2024, 3, 7, 8, 0), StageType.HIGH_MOUNTAIN);

            portal1.concludeStagePreparation(3);

            System.out.println(Arrays.toString(portal1.getRaceStages(0)));

            portal1.registerRiderResultsInStage(3, 2, LocalTime.of(6, 30, 00), LocalTime.of(11, 26, 5));
            System.out.println("Rider with id 2, finishing time: " + portal1.getRiderAdjustedElapsedTimeInStage(3, 2));

            portal1.registerRiderResultsInStage(3, 0, LocalTime.of(6, 30, 00), LocalTime.of(10, 30, 50));
            System.out.println("Rider with id 0, finishing time: " + portal1.getRiderAdjustedElapsedTimeInStage(3, 0));

            System.out.println("");

            System.out.println("The rider times for stage 3 come as follows: " + Arrays.toString(portal1.getRankedAdjustedElapsedTimesInStage(3)));
            System.out.println("");

            System.out.println(Arrays.toString(portal1.getRidersRankInStage(3)));

            portal2.createTeam("teamTwo", "everybody hates them");
            portal2.createRider(2, "Jason_cummings", 1999);
            portal2.createRider(2, "amba_singh", 1987);
            portal2.addStageToRace(1, "hell", "we just wanna see this team suffer", 50.00, LocalDateTime.of(2025, 4, 5, 9, 0), StageType.HIGH_MOUNTAIN);
            portal2.addCategorizedClimbToStage(4, 10.00, CheckpointType.HC, 20.00, 5.00);
            portal2.addCategorizedClimbToStage(4, 20.00, CheckpointType.HC, 20.00, 5.00);
            portal2.addCategorizedClimbToStage(4, 33.00, CheckpointType.HC, 20.00, 5.00);
            portal2.concludeStagePreparation(4);
            portal2.registerRiderResultsInStage(4, 3, LocalTime.of(11, 03, 12), LocalTime.of(13, 57, 38), LocalTime.of(16, 27, 54), LocalTime.of(21, 14, 30), LocalTime.of(23, 7, 28));
            portal2.registerRiderResultsInStage(4, 4, LocalTime.of(11, 03, 12), LocalTime.of(14, 57, 38), LocalTime.of(15, 03, 2), LocalTime.of(22, 14, 30), LocalTime.of(23, 58, 28));
            System.out.println(Arrays.toString(portal2.getRidersRankInStage(4)));
            System.out.println(Arrays.toString(portal2.getRidersPointsInStage(4)));
            System.out.println(Arrays.toString(portal2.getRidersMountainPointsInStage(4)));
            portal2.removeTeam(2);

            System.out.println(Arrays.toString(portal1.getRiderResultsInStage(0, 2)));

            portal1.saveCyclingPortal("testPortal");
            portal2.loadCyclingPortal("testPortal");

            System.out.println(portal2.getRiderAdjustedElapsedTimeInStage(0, 2));

		}
		catch (InvalidLengthException | IDNotRecognisedException | IllegalNameException | InvalidNameException | InvalidLocationException | InvalidStageStateException | InvalidStageTypeException | IllegalArgumentException | DuplicatedResultException | InvalidCheckpointTimesException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
        }
	}

}
