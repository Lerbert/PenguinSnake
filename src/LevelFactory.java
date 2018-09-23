
public class LevelFactory {
	public static Level createClassicLevel() {
		int[][] level0 = new int[20][20];

		for (int i = 0; i < level0.length; i++) {
			for (int j = 0; j < level0[i].length; j++) {
				level0[i][j] = Level.FREE;
			}
		}

		return new Level(level0, "Classic", 9, 9, Direction.DOWN);
	}
	
	public static Level createArenaLevel() {
		int[][] level1 = new int[20][20];

	    for (int i = 0; i < level1.length; i++) {
	      for (int j = 0; j < level1[i].length; j++) {
	        level1[i][j] = Level.FREE;
	      }
	      level1[i][0] = Level.WALL;
	      level1[i][level1[i].length - 1] = Level.WALL;
	    }
	    for (int j = 0; j < level1[0].length; j++) {
	      level1[0][j] = Level.WALL;
	    }
	    for (int j = 0; j < level1[level1.length - 1].length; j++) {
	      level1[level1.length - 1][j] = Level.WALL;
	    }
	    
	    return new Level(level1, "Arena", 9, 9, Direction.DOWN);
	}
}
