
public enum Direction {
	UP("Up"),DOWN("Down"),LEFT("Left"),RIGHT("Right");
	
	private final String desc;
	
	private Direction(String desc) {
		this.desc = desc;
	}
	
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
	
	@Override
	public String toString() {
		return this.desc;
	}
}
