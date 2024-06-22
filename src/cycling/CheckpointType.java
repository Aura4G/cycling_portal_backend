package cycling;

/**
 * This enum is used to represent the checkpoint types within stages on road races.
 * 
 * @author Diogo Pacheco
 * @version 2.0
 *
 */
public enum CheckpointType {

	/**
	 * An intermediate sprint.
	 */
	SPRINT(new int[0]),

	/**
	 * A categorised 4 climb. The easiest categorised climbs of all, under 2km long
	 * with an average grade of around 5% or 2-3% up to 5km long.
	 */
	C4(new int[]{1}),

	/**
	 * A categorised 3 climb. This could be a climb as short as 1km with a steep
	 * gradient of about 10% or a mellower climb up to 10km long with up to a 5%
	 * gradient.
	 */
	C3(new int[]{2, 1}),

	/**
	 * A categorised 2 climb. Category 2 could be a short climb, for example 5km at
	 * 8 percent, or as long as 15km at 4. percent
	 */
	C2(new int[]{5, 3, 2, 1}),

	/**
	 * A categorised 1 climb. Still a very significant climb, it could be a big
	 * mountain climb with a lesser gradient or a shorter climb with a steep pitch,
	 * for example 8km at 8% through to 20km at 5%.
	 */
	C1(new int[]{10, 8, 6, 4, 2, 1}),

	/**
	 * From the French term "Hors Categorie" (HC) meaning beyond categorisation. The
	 * toughest of the tough. The longest or steepest climbs, often both combined.
	 */
	HC(new int[]{20, 15, 12, 10, 8, 6, 4, 2});

	private final int[] checkpoints;

	CheckpointType(int[] checkpoints){
		this.checkpoints = checkpoints;
	}

	public int[] getCheckPointType() {
		return checkpoints;
	}

	public int getPoints(int index){
		if (index >= 0 && index < checkpoints.length){
			return checkpoints[index];
		}
		return 0;
	}
}