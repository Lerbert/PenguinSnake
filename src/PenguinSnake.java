import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class PenguinSnake {
	public static final int FOOD = 0;
	public static final int SNAKE_HEAD = 1;
	public static final int SNAKE_BODY = 2;
	
	private Level level;
	private volatile Level nextLevel;
	
	private volatile Direction direction;
	private volatile Direction lockedDirection;
	
	private ArrayList<Integer> snake;
	private int head; // still in list
	private int food;
	
	private int score;
	private boolean finished;
	private boolean paused;
	
	private GameGUI gameGUI;

	private Editor levelEditor;
	
	public PenguinSnake() {
		snake = new ArrayList<Integer>();
		level = LevelFactory.createLevel(0);
		gameGUI = new GameGUI(level.getMaze(), new LevelListener());
		gameGUI.getFrame().addKeyListener(new KeyHandler());
		init(level);
	}
	
	private void gameLoop() {
		int sleep = 500;
		int untilReduce = 5;
		
		while(!finished) {
			if (!paused) {
				int next = move();
				if (checkCollision(next)) {
					finished = true;
					System.out.println("Game over!\nScore: " + score);
					System.out.println("Choose another level to continue or hit ESC to quit");
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
		
		while (nextLevel == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		
		init(nextLevel);
	}
	
	private int move() {
		int current = snake.get(snake.size() - 1);
		
		int nextX = xPos(current);
		int nextY = yPos(current);
		
		switch (direction) {
			case UP:
				nextY--;
				break;
			case DOWN:
				nextY++;
				break;
			case LEFT:
				nextX--;
				break;
			case RIGHT:
				nextX++;
				break;
			default:
				// Should never happen
				throw new RuntimeException("Undefined Direction!");
		}
		
		lockedDirection = Direction.opposite(direction);
		
		return wrapPosition(nextX, nextY);
	}
	
	private int wrapPosition(int x, int y) {
		
		x += level.getWidth();
		x %= level.getWidth();
		
		y += level.getHeight();
		y %= level.getHeight();
		
		return pos(x, y);
	}
	
	private boolean checkCollision(int pos) {
		boolean collideSnake = snake.contains(pos);
		boolean collideWall = level.getMaze()[xPos(pos)][yPos(pos)] == Level.WALL;
		
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
		x = r.nextInt(level.getWidth());
		y = r.nextInt(level.getHeight());
		} while (level.getMaze()[x][y] != Level.FREE || snake.contains(pos(x, y)));
		
		food = pos(x, y);
	}
	
	private void display() {
		int[][] dis = level.getMaze();
		
		dis[xPos(food)][yPos(food)] = FOOD;
		
		for (Integer pos : snake) {
			dis[xPos(pos)][yPos(pos)] = SNAKE_BODY;
		}
		
		// Set head extra
		dis[xPos(head)][yPos(head)] = SNAKE_HEAD;
		
		gameGUI.updateState(dis);
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
	
	private void requestRestart(Level level) {
		System.out.println("Starting level \"" + level.getName() + "\"...");
		finished = true;
		nextLevel = level;
	}
	
	private void init(Level level) {
		score = 0;
		finished = false;
		paused = true;
		snake.clear();
		
		this.level = level;
		this.nextLevel = null;
		 
		int x = level.getStartX();
		int y = level.getStartY();
		
		snake.add(pos(x, y));
		head = pos(x, y);
		
		generateFood();
		
		direction = level.getStartDirection();
		lockedDirection = Direction.opposite(direction);
		
		gameGUI.updateState(level.getMaze());
		
		display();
		// sort of fix image loading
		System.out.println("Loading images...");
		try { 
			Thread.sleep(2000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Finished loading");
		display();
		
		System.out.println("Hit space to start");
		
		gameLoop();
	}
	
	public static void main(String[] args) {
		new Editor(21, 21);
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
				paused = !paused;
				System.out.println(paused?"break":"continue");
				break;
			case KeyEvent.VK_LEFT:
				if (!paused && Direction.LEFT != lockedDirection) {
					direction = Direction.LEFT;
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (!paused && Direction.RIGHT != lockedDirection) {
					direction = Direction.RIGHT;
				}
				break;
			case KeyEvent.VK_UP:
				if (!paused && Direction.UP != lockedDirection) {
					direction = Direction.UP;
				}
				break;
			case KeyEvent.VK_DOWN:
				if (!paused && Direction.DOWN != lockedDirection) {
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
		public void actionPerformed(ActionEvent e) {
			int levelIndex = Integer.parseInt(e.getActionCommand());
			requestRestart(LevelFactory.createLevel(levelIndex));
		}
		
	}
}
