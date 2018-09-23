
public enum Direction {
	UP,DOWN,LEFT,RIGHT;
	
	public static Direction opposite(Direction d) {
		switch (d) {
			case UP:
				return DOWN;
			case DOWN:
				return UP;
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			default:
				throw new RuntimeException("Undefined Direction!");
		}
	}
}
