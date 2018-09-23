import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
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
	private static final Direction defaultDirection = Direction.DOWN;
	
	private JFrame frame;
	
	private int[][] state;
	private int width;
	private int height;
	
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
		
		JMenuItem startPos = new JMenuItem("Set start position");
		toolMenu.add(startPos);
		
		JMenu startDirection = new JMenu("Direction");
		ButtonGroup directions = new ButtonGroup();
		for (Direction d : Direction.values()) {
			JRadioButtonMenuItem dir = new JRadioButtonMenuItem(d.toString());
			if (d == defaultDirection) {
				dir.setSelected(true);
			}
			directions.add(dir);
			startDirection.add(dir);
		}
		toolMenu.add(startDirection);
		
		menubar.add(toolMenu);
		
		frame.add(menubar, BorderLayout.NORTH);
		
		JPanel fieldPanel = new JPanel();
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
	
	private class FieldListener implements MouseListener {
		private Field field;
		
		public FieldListener(Field field) {
			super();
			this.field = field;
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			int x = field.getXCoord();
			int y = field.getYCoord();
			
			if (state[x][y] == Level.WALL) {
				state[x][y] = Level.FREE;
			} else {
				state[x][y] = Level.WALL;
			}
			
			field.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// Intentionally left blank
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// Intentionally left blank
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// Intentionally left blank
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// Intentionally left blank
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
		}


		public int getXCoord() {
			return x;      
		}                  

		public int getYCoord() {
			return y;      
		}
	}
}
