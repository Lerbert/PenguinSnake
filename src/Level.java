
public class Level {
	public static final int OUTSIDE = -1;
	public static final int FREE = -2;
	public static final int WALL = -3;
	
	private int[][] maze;
	private String name;
	
	private int width;
	private int height;
	
	private int startX;
	private int startY;
	private Direction startDirection;
	
	public Level(int[][] maze, String name, int startX, int startY, Direction startDirection) {
		this.maze = maze;
		this.name = name;
		
		this.startX = startX;
		this.startY = startY;
		this.startDirection = startDirection;
		
		this.width = this.maze.length;
		this.height = this.maze[0].length;
	}

	public int[][] getMaze() {
		int[][] mazeCopy = new int[width][height];
		
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				mazeCopy[i][j] = maze[i][j];
			}
		}
		
		return mazeCopy;
	}

	public String getName() {
		return name;
	}
	
	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public Direction getStartDirection() {
		return startDirection;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
