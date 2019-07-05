import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

public class GameGUI {
	private static final int IWH = 40;
	private static double scale = 1.0;
	
	private JPanel fieldPanel;
	private JFrame frame;
	
	private Image[] penguins = new Image[2];

	private int[][] state;

	public GameGUI(int[][] startState, ActionListener levelListener) {
		// Load images
		for (int i = 1; i <= 2; i++) {
			File f = new File("tux" + i + ".png");
			if(f.exists() && !f.isDirectory()) { 
				penguins[i-1] = Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath());
			}
		}

		state = startState;
		
		frame = new JFrame("PenguinSnake");
		frame.setLayout(new BorderLayout());
		
		JMenuBar menubar = new JMenuBar();
		
		JMenu levels = new JMenu("Level");
		
		ButtonGroup levelButtons = new ButtonGroup();
		
		for (int i = 0; i < LevelFactory.levelNames.length; i++) {
			if (i == LevelFactory.levelNames.length - 1) {
				levels.add(new JSeparator());
			}
			JRadioButtonMenuItem level = new JRadioButtonMenuItem(LevelFactory.levelNames[i]);
			level.addActionListener(levelListener);
			level.setActionCommand(Integer.toString(i));
			levelButtons.add(level);
			levels.add(level);
			if (i == 0) {
				level.setSelected(true);
			}
		}
		
		menubar.add(levels);
		
		frame.add(menubar, BorderLayout.NORTH);
		
		fieldPanel = new JPanel();
		//init fieldPanel
		for (int y = 0; y < state[0].length; y++) {
			for (int x = 0; x < state.length; x++) {
				fieldPanel.add(new Field(x, y));
			}
		}
		
		fieldPanel.setLayout(new GridLayout(state[0].length, state.length));
	    frame.add(fieldPanel, BorderLayout.CENTER);
	    frame.setSize
	      ((int)(IWH * scale) * state.length,
	       (int)(IWH * scale) * state[0].length);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setResizable(false);
	    frame.setVisible(true);
	}
	
	public void updateState(int[][] state) {
		for (int x = 0; x < this.state.length; x++) {
			for (int y = 0; y < this.state[0].length; y++) {
				this.state[x][y] = state[x][y];
			}
		}
		
		fieldPanel.repaint();
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	
	public void displayMessage(String message, String title) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
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

			switch (state[x][y]) {
			case PenguinSnake.FOOD:
				paintSymbol(g, Color.RED);
				break;
			case PenguinSnake.SNAKE_HEAD:
				drawPeng(g, 0);
				break;
			case PenguinSnake.SNAKE_BODY:
				drawPeng(g, 1);
				break;
			default:
				break;
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
		private void drawPeng(Graphics g, int index) {
			if (penguins[index] == null) {
				if (index == 0) paintSymbol(g, Color.YELLOW);
				if (index == 1) paintSymbol(g, Color.BLUE);
				return;
			}
			((Graphics2D) g).drawImage
			(penguins[index], 0, 0,
					getWidth(), getHeight(), 0, 0,
					penguins[index].getWidth(null),
					penguins[index].getHeight(null),
					null);
		}
	}
}
