import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

public class Editor {
	private static final int IWH = 40;
	private static double scale = 1.0;
	
	private static final String defaultName = "Custom";
	private static final int defaultStartX = 0;
	private static final int defaultStartY = 0;
	private static final Direction defaultStartDirection = Direction.DOWN;
	
	
	private JFrame frame;
	private JPanel fieldPanel;
	
	private int[][] state;
	private int width;
	private int height;
	private String name;
	private int startX;
	private int startY;
	private Direction startDirection;
	
	public Editor(int width, int height) {
		this.width = width;
		this.height = height;
		this.state = LevelFactory.createEmptyLevel(width, height).getMaze();
		
		frame = new JFrame("Level Editor");
		frame.setLayout(new BorderLayout());
		
		JMenuBar menubar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem open = new JMenuItem("Open");
		fileMenu.add(open);
		
		JMenuItem save = new JMenuItem("Save");
		fileMenu.add(save);
		
		menubar.add(fileMenu);
		
		JMenu toolMenu = new JMenu("Tools");
		
		JMenuItem size = new JMenuItem("Set size");
		toolMenu.add(size);
		
		JMenu startDirection = new JMenu("Direction");
		ButtonGroup directions = new ButtonGroup();
		for (Direction d : Direction.values()) {
			JRadioButtonMenuItem dir = new JRadioButtonMenuItem(d.toString());
			dir.setActionCommand(d.name());
			dir.addActionListener(new DirectionListener());
			if (d == defaultStartDirection) {
				dir.setSelected(true);
			}
			directions.add(dir);
			startDirection.add(dir);
		}
		toolMenu.add(startDirection);
		
		menubar.add(toolMenu);
		
		frame.add(menubar, BorderLayout.NORTH);
		
		fieldPanel = new JPanel();
		//init fieldPanel
		for (int y = 0; y < state[0].length; y++) {
			for (int x = 0; x < state.length; x++) {
				Field f = new Field(x, y);
				f.addMouseListener(new FieldListener(f));
				fieldPanel.add(f);
			}
		}
		
		fieldPanel.setLayout(new GridLayout(state[0].length, state.length));
	    frame.add(fieldPanel, BorderLayout.CENTER);
	    frame.setSize
	      ((int)(IWH * scale) * state.length,
	       (int)(IWH * scale) * state[0].length);
	    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	    frame.setResizable(false);
	    
	    open();
	}

	public void open() {
		frame.setVisible(true);
	}
	
	public void close() {
		frame.setVisible(false);
	}
	
	public Level getLevel() {
		int[][] maze = new int[width][height];
		
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[0].length; j++) {
				maze[i][j] = state[i][j];
			}
		}
		
		return new Level(maze, name, startX, startY, startDirection);
	}
	
	private class FieldListener implements MouseListener {
		private Field field;
		
		public FieldListener(Field field) {
			super();
			this.field = field;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			int x = field.getXCoord();
			int y = field.getYCoord();
			int button = e.getButton();
			
			if (state[x][y] == Level.WALL && button == 1) {
				state[x][y] = Level.FREE;
			} else if (state[x][y] == Level.FREE && button == 1) {
				state[x][y] = Level.WALL;
			} else if (button == 3) {
				state[startX][startY] = Level.FREE;
				state[x][y] = PenguinSnake.SNAKE_HEAD;
				startX = x;
				startY = y;
			}
			
			fieldPanel.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// Intentionally left blank
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// Intentionally left blank
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// Intentionally left blank
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// Intentionally left blank
		}
		
	}
	
	private class DirectionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			for (Direction d : Direction.values()) {
				if (d.name().equals(cmd)) {
					startDirection = d;
					return;
				}
			}
		}
		
	}
	
	// From PGDP TUM WS 17/18
	@SuppressWarnings("serial")
	private class Field extends JPanel {
		private Point p;
		private int x,y;

		public Field(int x, int y) {
			this.x = x;
			this.y = y;
			p = getLocation();
		}

		public void paint(Graphics g) {
			super.paint(g);
			if (state[x][y] == Level.WALL) {
				GradientPaint gradient =
						new GradientPaint(10, 50, Color.GRAY, getWidth(), 0, Color.DARK_GRAY);
				((Graphics2D) g).setPaint(gradient);
			} else {
				GradientPaint gradient = new GradientPaint(0, 50, Color.WHITE, getWidth(), 0, Color.GRAY);
				((Graphics2D) g).setPaint(gradient);
			}

			g.fillRect(p.getLocation().x, p.getLocation().y, getWidth() * 2, getHeight());
			
			if (state[x][y] == PenguinSnake.SNAKE_HEAD) {
				paintSymbol(g, Color.BLUE);
			}
		}

		private void paintSymbol(Graphics g, Color c) {
			GradientPaint gradient = new GradientPaint(15, 0, c, getWidth(), 0, Color.LIGHT_GRAY);
			((Graphics2D) g).setPaint(gradient);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.fillOval((int) (getWidth() * 0.3), (int) (getHeight() * 0.3), (int) (getWidth() * 0.5),
					(int) (getHeight() * 0.5));
		}

		public int getXCoord() {
			return x;      
		}                  

		public int getYCoord() {
			return y;      
		}
	}
}
