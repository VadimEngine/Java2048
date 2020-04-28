import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class Board {

	private Integer tiles[][];
	static HashMap<Integer, Color> colormap = new HashMap<>();
	private Stack<Integer[][]> undos;
	private int width;
	private int score;
	private boolean gameOver;


	private Random rand = new Random();

	public Board(int x, int y) {
		gameOver = false;
		score = 0;
		tiles = new Integer[y][x];
		width = 128;
		add();
		add();
		undos = new Stack<>();
	}

	public void draw(Graphics g) {

		g.setColor(Color.gray);
		//Font f = new Font("serif", Font.PLAIN, 32);
		//g.setFont(f);
		Graphics2D g2 =  (Graphics2D) g;
		g2.setStroke(new BasicStroke(16));

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				g.drawRect(i*(width+ 16) + 16, j*(width + 16)+16, width + 16, width + 16);
				if (tiles[i][j] != null) {
					g.setColor(colormap.get(tiles[i][j]));
					g.fillRect(i*(width + 16) +8 + 16, j* (width + 16) +8 + 16, width, width);
					g.setColor(Color.black);
					String a = "" + tiles[i][j]; 
					g.drawString(a, i*(width + 16) +8  + 16 + (width/2) - (a.length()*8),
							j* (width + 16) +8 + 16 + (width/2));
					g.setColor(Color.gray);
				}
			}
		}

	}

	public void add() {
		int randx = rand.nextInt(tiles[0].length);
		int randy = rand.nextInt(tiles.length);
		int tileVal = (rand.nextInt(2) + 1) * 2;

		while (tiles[randy][randx] != null && !isFull()) {
			randx = rand.nextInt(tiles[0].length);
			randy = rand.nextInt(tiles.length);
		}

		if (!isFull()) {
			tiles[randy][randx] = tileVal;
			score += tileVal;
		} else {
			System.out.println("The board is full");
		}
	}

	public void moveTest(int dir) {//moveup/down? range i/jmin and i/jmax
		int xOffset;
		int yOffset;

		boolean moved = false;
		boolean[] merged = new boolean[tiles.length];
		for (int k = 0; k < tiles[0].length - 1; k++) {

			for (int i = 0; i < tiles.length; i++) {
				for (int j = 1; j < tiles[0].length; j++) {
					if (tiles[i][j] != null && tiles[i][j-1] == null) {
						tiles[i][j - 1] = tiles[i][j];
						tiles[i][j] = null;
						moved = true;
					} else if (!merged[i] && tiles[i][j] != null && tiles[i][j-1].intValue() == tiles[i][j].intValue()) {
						tiles[i][j - 1] *= 2;
						tiles[i][j] = null;
						score += tiles[i][j - 1];
						merged[i] = true;
						moved = true;
					}

				}
			}

		}
		if (moved) {
			add();
		} else {
			undos.pop();
		}

	}


	public void moveUp() {
		addToStack();
		boolean moved = false;
		boolean[] merged = new boolean[tiles.length];
		for (int k = 0; k < tiles[0].length - 1; k++) {

			for (int i = 0; i < tiles.length; i++) {
				for (int j = 1; j < tiles[0].length; j++) {
					if (tiles[i][j] != null && tiles[i][j-1] == null) {
						tiles[i][j - 1] = tiles[i][j];
						tiles[i][j] = null;
						moved = true;
					} else if (!merged[i] && tiles[i][j] != null && tiles[i][j-1].intValue() == tiles[i][j].intValue()) {
						tiles[i][j - 1] *= 2;
						tiles[i][j] = null;
						score += tiles[i][j - 1];
						merged[i] = true;
						moved = true;
					}

				}
			}

		}
		if (moved) {
			add();
		} else {
			undos.pop();
		}

	}

	public void moveRight() {
		addToStack();
		boolean moved = false;
		boolean[] merged = new boolean[tiles[0].length];
		for (int k = 0; k < tiles.length - 1; k++) {

			for (int i = tiles.length - 2; i >= 0; i--) {//FIX THIS
				for (int j = 0; j < tiles[0].length; j++) {
					if (tiles[i][j] != null && tiles[i + 1][j] == null) {
						tiles[i+1][j] = tiles[i][j];
						tiles[i][j] = null;
						moved = true;
					} else if (!merged[j] && tiles[i][j] != null && tiles[i+1][j].intValue() == tiles[i][j].intValue()) {
						tiles[i+1][j] *= 2;
						tiles[i][j] = null;
						score += tiles[i+1][j];
						merged[j] = true;
						moved = true;
					}

				}
			}

		}

		if (moved) {
			add();
		} else {
			undos.pop();
		}

	}

	public void moveDown() {
		addToStack();
		boolean moved = false;
		boolean[] merged = new boolean[tiles.length];
		for (int k = 0; k < tiles[0].length - 1; k++) {

			for (int i = 0; i < tiles.length; i++) {
				for (int j = tiles[0].length - 2; j >= 0; j--) {//ignore the last row
					if (tiles[i][j] != null && tiles[i][j+1] == null) {
						tiles[i][j+1] = tiles[i][j];
						tiles[i][j] = null;
						moved = true;
					} else if (!merged[i] && tiles[i][j] != null && tiles[i][j+1].intValue() == tiles[i][j].intValue()) {
						tiles[i][j+1] *= 2;
						tiles[i][j] = null;
						score += tiles[i][j+1];
						merged[i] = true;
						moved = true;
					}

				}
			}

		}

		if (moved) {
			add();
		}  else {
			undos.pop();
		}
	}

	public void moveLeft() {
		addToStack();
		boolean moved = false;
		boolean[] merged = new boolean[tiles[0].length];
		for (int k = 0; k < tiles.length - 1; k++) {

			for (int i = 1; i < tiles.length; i++) {
				for (int j = 0; j < tiles[0].length; j++) {
					if (tiles[i][j] != null && tiles[i - 1][j] == null) {
						tiles[i-1][j] = tiles[i][j];
						tiles[i][j] = null;
						moved = true;
					} else if (!merged[j] && tiles[i][j] != null && tiles[i-1][j].intValue() == tiles[i][j].intValue()) {
						tiles[i-1][j] *= 2;
						tiles[i][j] = null;
						score += tiles[i-1][j];
						merged[j] = true;
						moved = true;
					}

				}
			}

		}
		if (moved) {
			add();
		} else {
			undos.pop();
		}

	}

	public boolean isFull() {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (tiles[i][j] == null) {
					return false;
				}
			}
		}

		return true;
	}

	public int getBoardWidth() {
		return tiles[0].length;
	}

	public int getBoardHeight() {
		return tiles.length;
	}

	public int getScore() {
		return score;
	}

	public boolean isOver() {//update only on move
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (tiles[i][j] == null) {
					return false;
				}

				if (i !=0 && tiles[i-1][j] != null &&tiles[i][j].intValue() == tiles[i-1][j].intValue()) {
					return false;
				}

				if (i != tiles.length - 1 && tiles[i+1][j] != null && tiles[i][j].intValue() == tiles[i+1][j].intValue()) {
					return false;
				}

				if (j !=0 && tiles[i][j-1] != null && tiles[i][j].intValue() == tiles[i][j-1].intValue()) {
					return false;
				}

				if (j != tiles[0].length - 1 && tiles[i][j+1] != null && tiles[i][j].intValue() == tiles[i][j+1].intValue()) {
					return false;
				}
			}
		}

		return true;
	}

	public void undo() {//REMOVE POINTS
		if (!undos.isEmpty()) {
			Integer temp[][] = undos.pop();
			for (int i = 0; i < temp.length; i++) {
				for (int j = 0; j < temp[0].length; j++) {
					tiles[i][j] = temp[i][j];
				}
			}
		}

	}

	private void printArray(Integer[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				System.out.print("[" + a[j][i] + "]");
				if (j == a[0].length - 1) {
					System.out.print("\n");
				}
			}
		}
	}

	private void addToStack() {
		Integer[][] temp = new Integer[tiles.length][tiles[0].length];
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				temp[i][j] = tiles[i][j];
			}
		}
		undos.push(temp);
	}

	public static void buildMap() {
		colormap.put(2, new Color(220, 220, 220));
		colormap.put(4, new Color(230,170,120));
		colormap.put(8, new Color(240,150,100));
		colormap.put(16, new Color(240,120,100));
		colormap.put(32, new Color(240,200,80));
		colormap.put(64, new Color(240,200,100));
		colormap.put(128, new Color(240,90,60));
		colormap.put(256, new Color(240,200,60));
		colormap.put(512, new Color(255,60, 60));
		colormap.put(1024, new Color(255,30,30));
		colormap.put(2048, new Color(255,20,20));
		colormap.put(4096, new Color(255,10,10));
		colormap.put(8192, new Color(255,0,0));
		colormap.put(131072, new Color(100,0,0));
	}

}


