import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class Game extends Canvas implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;

	private boolean running;
	private Thread thread;
	private Board board;

	private int bestScore = 0;

	public Game() {
		Board.buildMap();
		board = new Board(2,2);

		Dimension size = new Dimension(128*board.getBoardHeight()+200, (128*board.getBoardWidth()+200));
		setPreferredSize(size);
		addKeyListener(this);
	}



	public synchronized void start() {
		if (running)
			return;

		running = true;
		thread = new Thread(this);
		thread.start();
	}


	public void run() {
		int frames = 0;

		double unprocessedSeconds = 0;
		long lastTime = System.nanoTime();
		double secondsPerTick = 1 / 60.0;
		int tickCount = 0;

		requestFocus();

		while (running) {
			long now = System.nanoTime();
			long passedTime = now - lastTime;
			lastTime = now;
			if (passedTime < 0) passedTime = 0;
			if (passedTime > 100000000) passedTime = 100000000;

			unprocessedSeconds += passedTime / 1000000000.0;

			boolean ticked = false;
			while (unprocessedSeconds > secondsPerTick) {
				tick();
				unprocessedSeconds -= secondsPerTick;
				ticked = true;

				tickCount++;
				if (tickCount % 60 == 0) {
					//System.out.println(frames + " fps");
					lastTime += 1000;
					frames = 0;
				}
			}

			if (ticked) {
				render();
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		Font f = new Font("serif", Font.PLAIN, 32);
		g.setFont(f);


		board.draw(g);
		if (board.isOver()) {
			g.setColor(Color.BLACK);
			g.drawString("GAME OVER!", getWidth() / 2 - 64, getHeight() / 2);
		}
		g.drawString("Score:" + board.getScore() + " Best Score: " + bestScore, 8, getHeight() - 16);

		g.dispose();
		bs.show();

	}

	public void tick() {

		if(board.getScore() > bestScore) {
			bestScore = board.getScore();
		}
	}




	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {

		case KeyEvent.VK_UP: 
			board.moveUp();
			break;

		case KeyEvent.VK_RIGHT: 
			board.moveRight();
			break;

		case KeyEvent.VK_DOWN: 
			board.moveDown();
			break;

		case KeyEvent.VK_LEFT: 
			board.moveLeft();
			break;
		}


	}


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		Game game = new Game();
		game.setFocusable(true);
		JFrame frame = new JFrame("2048");

		JPanel top = new JPanel();
		JPanel bottom = new JPanel();


		JButton newGame = new JButton("New Game");

		JSlider xslide = new JSlider(JSlider.HORIZONTAL, 2, 6, 4);
		xslide.setMajorTickSpacing(1);
		xslide.setPaintTicks(true);
		xslide.setPaintTicks(true);

		newGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game.board = new Board(xslide.getValue(), xslide.getValue());
				Dimension size = new Dimension(128*game.board.getBoardHeight()+200, (128*game.board.getBoardWidth()+200));
				System.out.println(128*game.board.getBoardHeight()+200 + ", " + (128*game.board.getBoardWidth() + 200));
				System.out.println(game.size());
				top.setPreferredSize(size);
				game.setPreferredSize(size);
				
				frame.pack();
				game.requestFocus();
				
			}

		});

		JButton undo = new JButton("Undo");



		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game.board.undo();
				game.requestFocus();

			}

		});

		bottom.add(newGame);
		bottom.add(undo);
		bottom.add(xslide);
		top.add(game);
		frame.add(top, BorderLayout.NORTH);
		frame.add(bottom, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.pack();
		game.start();
	}

}



