package cycling;

/**
 * This enum is used to represent the stage types  on road races.
 * 
 * @author Diogo Pacheco
 * @version 1.0
 *
 */
public enum StageType {
	
	/**
	 * Used for mostly flat stages.
	 */
	FLAT(new int[]{50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2}),
	
	/**
	 * Used for hilly finish or stages with moderate amounts of mountains.
	 */
	MEDIUM_MOUNTAIN(new int[]{30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2}),
	
	/**
	 * Used for high mountain finish or stages with multiple categorised climbs.
	 */
	HIGH_MOUNTAIN(new int[]{20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}),
	
	/**
	 * Used for time trials. 
	 */
	TT(new int[]{20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1});
	
	private final int[] stage;

	StageType(int[] stage){
		this.stage = stage;
	}

	public int[] getStageType() {
		return stage;
	}

	public int getPoints(int index){
		if (index >= 0 && index < stage.length){
			return stage[index];
		}
		return 0;
	}
}
