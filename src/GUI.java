import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI {
	private static final int IWH = 40;
	private static double scale = 1.0;
	
	private JPanel fieldPanel;
	private JFrame frame;
	
	private Image[] penguins = new Image[2];

	private int[][] state;

	public GUI(int[][] startState) {
		// Load images
		for (int i = 1; i <= 2; i++) {
			File f = new File("tux" + i + ".png");
			if(f.exists() && !f.isDirectory()) { 
				penguins[i-1] = Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath());
			}
		}
		
//		System.out.println("Loading images...");
//
//		try { 
//			Thread.sleep(3000);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}

		state = new int[startState.length][startState[0].length];

		for (int i = 0; i < startState.length; i++) {
			for (int j = 0; j < startState[0].length; j++) {
				state[i][j] = startState[i][j];
			}
		}
		
		frame = new JFrame("PenguinSnake");
		fieldPanel = new JPanel();
		
		//init fieldPanel
		for (int y = 0; y < state[0].length; y++) {
			for (int x = 0; x < state.length; x++) {
				fieldPanel.add(new Field(x, y));
			}
		}
		
		fieldPanel.setLayout(new GridLayout(state[0].length, state.length));
	    frame.getContentPane().add(fieldPanel);
	    frame.setSize
	      ((int)(IWH * scale) * state.length,
	       (int)(IWH * scale) * state[0].length);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setResizable(false);
	    // frame.addKeyListener(new KeyHandler());
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
			if (state[x][y] == PenguinSnake.WALL) {
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
