import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;

import javax.swing.JFileChooser;

public class LevelFactory {
	public static final String[] levelNames = {"Classic", "Arena", "Sectors", "Load..."};
	private static final LevelCreator[] creators = {LevelFactory::createClassicLevel, LevelFactory::createArenaLevel, LevelFactory::createSectorsLevel, LevelFactory::loadLevel};
	
	public static Level createLevel(int index) {
		if (index < 0 || index >= creators.length) {
			System.err.println("Level creation index out of bounds.");
			return null;
		}
		
		return creators[index].create();
	}
	
	public static Level createClassicLevel() {
		int[][] level0 = new int[21][21];

		for (int i = 0; i < level0.length; i++) {
			for (int j = 0; j < level0[i].length; j++) {
				level0[i][j] = Level.FREE;
			}
		}

		return new Level(level0, "Classic", 10, 10, Direction.DOWN);
	}
	
	public static Level createArenaLevel() {
		int[][] level1 = new int[21][21];

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
	    
	    return new Level(level1, "Arena", 10, 10, Direction.DOWN);
	}
	
	public static Level createSectorsLevel() {
		int[][] level2 = new int[21][21];

		for (int i = 0; i < level2.length; i++) {
			for (int j = 0; j < level2[i].length; j++) {
				level2[i][j] = Level.FREE;
			}
		}
		
		for (int i = 0; i < level2.length; i++) {
			level2[i][10] = Level.WALL;
		}

		for (int i = 0; i < level2[10].length; i++) {
			level2[10][i] = Level.WALL;
		}

		return new Level(level2, "Sectors", 5, 5, Direction.UP);
	}
	
	public static Level loadLevel() {
		JFileChooser fileChooser = new JFileChooser();
		File levelDir = new File("./levels");
		if (!levelDir.exists()) {
			levelDir = new File(System.getProperty("user.dir"));
		}
		
		fileChooser.setCurrentDirectory(levelDir);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		
		File saveLocation = fileChooser.getSelectedFile();
		
		try {
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(saveLocation));
			Object in = is.readObject();
			is.close();
			
			Level level = null;
			if (in instanceof Level) {
				level = (Level) in;
			} else {
				System.err.println("Choose a level file.");
			}
			
			return level;
		} catch (FileNotFoundException e) {
			System.err.println("Specified path does not point to a file.");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Level createEmptyLevel(int width, int height) {
		int[][] emptyLevel = new int[width][height];

		for (int i = 0; i < emptyLevel.length; i++) {
			for (int j = 0; j < emptyLevel[i].length; j++) {
				emptyLevel[i][j] = Level.FREE;
			}
		}

		return new Level(emptyLevel, "Empty", 0, 0, Direction.DOWN);
	}
	
	private interface LevelCreator {
		public Level create();
	}
		
}
