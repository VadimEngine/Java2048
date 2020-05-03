import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

/**
 * The board class which hold the tiles and handles the movement of the tiles. The game logic is to add a new tile
 * of random value of 2 and 4 in a random untaken location. the move methods move all the tiles to the respective
 * location and merge the tiles of same length. On every move, the undos stack is updated with the board tiles position
 * to allow resetting the board to previous state.
 * 
 * Could use with reorganizations such as Tile class. moveUp(), moveDown(), moveLeft(), moveRight() could me merged 
 * into one method move(Direction) to help with maintainability and reusability 
 * @author Vadim
 */
public class Board {
	private static final HashMap<Integer, Color> colormap = new HashMap<>();
	private static final Random RAND = new Random();
	
	private Integer tiles[][];
	private Stack<Integer[][]> undos;
	private int width;
	private int score;
	
	static {
		buildMap();
	}

	/**
	 * Constructor that sets game wth x by y tiles and adds 2 tiles to the game
	 * @param x Number of tiles wide
	 * @param y Number of tiles high 
	 */
	public Board(int x, int y) {
		score = 0;
		tiles = new Integer[y][x];
		width = 128;
		add();
		add();
		undos = new Stack<>();
	}

	/**
	 * Draws the board with the borders and the tiles
	 * @param g the Game graphics object
	 */
	public void draw(Graphics g) {
		g.setColor(Color.gray);
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

	/**
	 * Adds a tile to the board at a available random location with a value of 2 or 4 and increments the score by
	 * the value of the new tile
	 */
	public void add() {
		int randx = RAND.nextInt(tiles[0].length);
		int randy = RAND.nextInt(tiles.length);
		int tileVal = (RAND.nextInt(2) + 1) * 2;

		while (tiles[randy][randx] != null && !isFull()) {
			randx = RAND.nextInt(tiles[0].length);
			randy = RAND.nextInt(tiles.length);
		}

		if (!isFull()) {
			tiles[randy][randx] = tileVal;
			score += tileVal;
		} else {
			System.out.println("The board is full");
		}
	}

	/**
	 * Move all the tiles up and merges the tiles with the same value and increments the score when tiles are merged.
	 * The previous board state is added to the Undo stack but there is no tile movement the the added board is popped.
	 * Iterates the entire board x times (incase a tile needs to move all the way to the end), moves the tiles and
	 * merges them if the next tile over is the same value
	 * 
	 * Better solution could be to iterate through the rows/column and slide them until the cant move anymore. Then 
	 * merge from outside to in (if up then merge from up to down) then slide again?
	 */
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
					} else if (!merged[i] && tiles[i][j] != null 
							&& tiles[i][j-1].intValue() == tiles[i][j].intValue()) {
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

	/**
	 * Move all the right up and merges the tiles with the same value and increments the score when tiles are merged.
	 * The previous board state is added to the Undo stack but there is no tile movement the the added board is popped.
	 * Iterates the entire board x times (incase a tile needs to move all the way to the end), moves the tiles and
	 * merges them if the next tile over is the same value
	 * 
	 * Better solution could be to iterate through the rows/column and slide them until the can't move anymore. Then 
	 * merge from outside to in (if up then merge from up to down) then slide again?
	 */
	public void moveRight() {
		addToStack();
		boolean moved = false;
		boolean[] merged = new boolean[tiles[0].length];
		for (int k = 0; k < tiles.length - 1; k++) {
			for (int i = tiles.length - 2; i >= 0; i--) {
				for (int j = 0; j < tiles[0].length; j++) {
					if (tiles[i][j] != null && tiles[i + 1][j] == null) {
						tiles[i+1][j] = tiles[i][j];
						tiles[i][j] = null;
						moved = true;
					} else if (!merged[j] && tiles[i][j] != null 
							&& tiles[i+1][j].intValue() == tiles[i][j].intValue()) {
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

	/**
	 * Move all the tiles down and merges the tiles with the same value and increments the score when tiles are merged.
	 * The previous board state is added to the Undo stack but there is no tile movement the the added board is popped.
	 * Iterates the entire board x times (incase a tile needs to move all the way to the end), moves the tiles and
	 * merges them if the next tile over is the same value
	 * 
	 * Better solution could be to iterate through the rows/column and slide them until the cant move anymore. Then 
	 * merge from outside to in (if up then merge from up to down) then slide again?
	 */
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
					} else if (!merged[i] && tiles[i][j] != null 
							&& tiles[i][j+1].intValue() == tiles[i][j].intValue()) {
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

	/**
	 * Move all the tiles left and merges the tiles with the same value and increments the score when tiles are merged.
	 * The previous board state is added to the Undo stack but there is no tile movement the the added board is popped.
	 * Iterates the entire board x times (incase a tile needs to move all the way to the end), moves the tiles and
	 * merges them if the next tile over is the same value
	 * 
	 * Better solution could be to iterate through the rows/column and slide them until the cant move anymore. Then 
	 * merge from outside to in (if up then merge from up to down) then slide again?
	 */
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
					} else if (!merged[j] && tiles[i][j] != null 
							&& tiles[i-1][j].intValue() == tiles[i][j].intValue()) {
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

	/**
	 * Sets the board back to the previous saved state.
	 * Bug where the old score is not undone, need to save the previous score.
	 */
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

	/**
	 * Used to save the board state to a stack so the previous state can be displayed
	 * 
	 * Should also save the score so when undo is called, the previous score is score is set.
	 */
	private void addToStack() {
		Integer[][] temp = new Integer[tiles.length][tiles[0].length];
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				temp[i][j] = tiles[i][j];
			}
		}
		undos.push(temp);
	}
	
	/**
	 * Checks if the board is full of tiles.
	 * 
	 * Better approach would be to track how many tiles there are and then return if numberOfTiels==x*y
	 * 
	 * @return if the board is full of tiles
	 */
	private boolean isFull() {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (tiles[i][j] == null) {
					return false;
				}
			}
		}
		return true;
	}
	
	//Getters and Setters-----------------------------------------------------------------------------------------------
	
	public int getBoardWidth() {
		return tiles[0].length;
	}

	public int getBoardHeight() {
		return tiles.length;
	}

	public int getScore() {
		return score;
	}
	
	/**
	 * Checks if board is full and if any of the moves can move in any directions
	 * @return if game is over and no more moves can be made
	 */
	public boolean isOver() {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (tiles[i][j] == null) {
					return false;
				}
				if (i !=0 && tiles[i-1][j] != null && tiles[i][j].intValue() == tiles[i-1][j].intValue()) {
					return false;
				}
				if (i != tiles.length - 1 && tiles[i+1][j] != null 
						&& tiles[i][j].intValue() == tiles[i+1][j].intValue()) {
					return false;
				}
				if (j !=0 && tiles[i][j-1] != null && tiles[i][j].intValue() == tiles[i][j-1].intValue()) {
					return false;
				}

				if (j != tiles[0].length - 1 && tiles[i][j+1] != null 
						&& tiles[i][j].intValue() == tiles[i][j+1].intValue()) {
					return false;
				}
			}
		}
		return true;
	}
	
	//Static Methods----------------------------------------------------------------------------------------------------

	/**
	 * Fills colormap with colors for each tile value.
	 */
	private static void buildMap() {
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