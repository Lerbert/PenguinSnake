import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class PenguinSnake {
	public static final int WALL = -3;
	public static final int FREE = -2;
	public static final int OUTSIDE = -1;
	public static final int FOOD = 0;
	public static final int SNAKE_HEAD = 1;
	public static final int SNAKE_BODY = 2;
	
	private int width;
	private int height;
	
	private int[][] maze;
	private int food;
	
	private volatile Direction direction;
	private volatile Direction lockedDirection;
	
	private boolean finished;
	private boolean pause;
	
	private ArrayList<Integer> snake;
	private int head; // still in list
	
	private int score;
	
	private GUI gui;
	
	public PenguinSnake() {
		score = 0;
		
		width = 20;
		height = 20;
		
		maze = generateLevel0();
		
		snake = new ArrayList<Integer>();
		snake.add(pos(9, 9));
		head = pos(9, 9);
		
		generateFood();
		
		direction = Direction.DOWN;
		lockedDirection = Direction.UP;
		
		finished = false;
		pause = false;
		
		gui = new GUI(maze, new LevelListener());
		gui.getFrame().addKeyListener(new KeyHandler());
		
		display();
		// sort of fix image loading
		System.out.println("Loading images...");
		try { 
			Thread.sleep(3000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		display();
		
		gameLoop();
	}
	
	private void gameLoop() {
		int sleep = 500;
		int untilReduce = 5;
		
		while(!finished) {
			if (!pause) {
				int next = move();
				if (checkCollision(next)) {
					finished = true;
					System.out.println("Game over!\nScore: " + score);
					continue;
				}
				// update snake
				head = next;
				snake.add(next);
				// check food
				if (checkFood(next)) {
					// Not removing the end means growing by 1
					score++;
					generateFood();
				} else {
					snake.remove(0);
				}
				// update GUI
				display();
				
				if (sleep > 250 && untilReduce-- == 0) {
					untilReduce = 5;
					sleep -= 1;
				}
			}
			// Timer
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private int move() {
		int current = snake.get(snake.size() - 1);
		
		int nextX = xPos(current);
		int nextY = yPos(current);
		
		switch (direction) {
			case UP:
				nextY--;
				lockedDirection = Direction.DOWN;
				break;
			case DOWN:
				nextY++;
				lockedDirection = Direction.UP;
				break;
			case LEFT:
				nextX--;
				lockedDirection = Direction.RIGHT;
				break;
			case RIGHT:
				lockedDirection = Direction.LEFT;
				nextX++;
				break;
			default:
				// Should never happen
				throw new RuntimeException("Undefined Direction!");
		}
		
		return wrapPosition(nextX, nextY);
	}
	
	private int wrapPosition(int x, int y) {
		
		x += width;
		x %= width;
		
		y += height;
		y %= height;
		
		return pos(x, y);
	}
	
	private boolean checkCollision(int pos) {
		boolean collideSnake = snake.contains(pos);
		boolean collideWall = maze[xPos(pos)][yPos(pos)] == WALL;
		
		return collideSnake || collideWall;
	}
	
	private boolean checkFood(int pos) {
		return pos == food;
	}
	
	private void generateFood() {
		Random r = new Random();
		int x;
		int y;
		
		do {
		x = r.nextInt(width);
		y = r.nextInt(height);
		} while (maze[x][y] != FREE || snake.contains(pos(x, y)));
		
		food = pos(x, y);
	}
	
	private void display() {
		int[][] dis = new int[maze.length][maze[0].length];
		
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				dis[i][j] = maze[i][j];
			}
		}
		
		dis[xPos(food)][yPos(food)] = FOOD;
		
		for (Integer pos : snake) {
			dis[xPos(pos)][yPos(pos)] = SNAKE_BODY;
		}
		
		// Set head extra
		dis[xPos(head)][yPos(head)] = SNAKE_HEAD;
		
		gui.updateState(dis);
	}
	
	// x: pos >> 16 y: pos & 0xFFFF
	private int xPos(int pos) {
		return pos >>> 16;
	}
	
	private int yPos(int pos) {
		int y = pos & 0xFFFF;
		// sign extension
		if ((y & 0x8000) != 0) {
			y |= 0xFFFF_0000;
		}
		return y;
	}
	
	private int pos(int x, int y) {
		return (x << 16) | (y & 0xFFFF);
	}
	
	// Classic
	private int[][] generateLevel0() {
		int[][] level0 = new int[width][height];
		
	    for (int i = 0; i < level0.length; i++) {
	      for (int j = 0; j < level0[i].length; j++) {
	        level0[i][j] = FREE;
	      }
	    }
	    
	    return level0;
	}
	
	// Arena
	private int[][] generateLevel1() {
		int[][] level1 = new int[width][height];

	    for (int i = 0; i < level1.length; i++) {
	      for (int j = 0; j < level1[i].length; j++) {
	        level1[i][j] = FREE;
	      }
	      level1[i][0] = WALL;
	      level1[i][level1[i].length - 1] = WALL;
	    }
	    for (int j = 0; j < level1[0].length; j++) {
	      level1[0][j] = WALL;
	    }
	    for (int j = 0; j < level1[level1.length - 1].length; j++) {
	      level1[level1.length - 1][j] = WALL;
	    }
	    
	    return level1;
	}
	
	public static void main(String[] args) {
		new PenguinSnake();
	}
	
	private class KeyHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			switch (ke.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			case KeyEvent.VK_SPACE:
				pause = !pause;
				System.out.println(pause?"break":"continue");
				break;
			case KeyEvent.VK_LEFT:
				if (!pause && Direction.LEFT != lockedDirection) {
					direction = Direction.LEFT;
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (!pause && Direction.RIGHT != lockedDirection) {
					direction = Direction.RIGHT;
				}
				break;
			case KeyEvent.VK_UP:
				if (!pause && Direction.UP != lockedDirection) {
					direction = Direction.UP;
				}
				break;
			case KeyEvent.VK_DOWN:
				if (!pause && Direction.DOWN != lockedDirection) {
					direction = Direction.DOWN;
				}
				break;
			default:
				break;
			}
		}
	}
	
	private class LevelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("Level");
		}
		
	}
}
