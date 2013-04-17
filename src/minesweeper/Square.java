package minesweeper;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * The Square class is thread safe. 
 * Fields objects (state, hasBomb, proximal, numBomb) are all private;
 * All the mutator methods are synchronized
 * 
 */

public class Square {
	private State state;
	private boolean hasBomb;
	private List<Square> proximal = new ArrayList<Square>(); // (up to 8) squares surrounding a given square
	private int numBomb;

	private static enum State {
		// three states defined according to problem set instructions
		// http://www.mit.edu/~6.005/fa12/psets/ps3/
		untouched, flagged, dug
	}

	public Square() {
		this.state = State.untouched;
		this.hasBomb = false;
	}

	public Square(boolean bombState) {
		this.state = State.untouched;
		this.hasBomb = bombState;
	}


	public synchronized boolean isBomb() {
		return hasBomb;
	}
	
	// a set of state-machine like mutations
	public synchronized void flag() {
		if (state == State.untouched) {
			state = State.flagged;
		}
	}

	public synchronized void deflag() {
		if (state == State.flagged) {
			state = State.untouched;
		}
	}

	public synchronized void dig() throws boomException {

		if (state == State.untouched) {
			boolean badState = false;
			state = State.dug;
			if (hasBomb) {
				hasBomb = false;
				for (Square sq : proximal) {
					sq.decreaseBomb();
				}
				badState = true;

			}
			if (numBomb == 0) {
				for (Square sq : proximal) {
					// dig surrounding squares recursively
					sq.dig();
				}

			}
			if (badState) {
				// for modularity, always throw boomException without any knowledge of DEBUG flag (true or false)
				throw new boomException();
			}
		}
	}

	/**
	 * after one player digs a bomb, this method is called to decrease
	 * surrounding bomb numbers (8 maximal); otherwise the method is unvisited
	 * 
	 * @modify the field numBomb of the class Square
	 */
	private synchronized void decreaseBomb() {
		numBomb--;
	}

	// add a new square to the adjacency list of the square currently being explored
	public synchronized void addAdjacent(Square square) {
		proximal.add(square);
		if (square.isBomb()) {
			numBomb++;
		}
	};

	@Override
	public synchronized String toString() {
		switch (state) {
		case flagged:
			return "F";
		case untouched:
			return "-";
		case dug: {
			if (numBomb == 0) {
				return " ";
			} else {
				return Integer.toString(numBomb);
			}
		}
		default:
			throw new RuntimeException("invalid state!");
		}
	}

	/** 
	 * 
	 * @return the string representation of the actual bomb state of a 1-by-1 square
	 * a state string could either by 'B' (i.e., bomb) or 'Integer' (number of neighboring bombs)
	 * 
	 */
	public synchronized String actualState() {
		if (hasBomb) {
			return "B";
		}
		return Integer.toString(numBomb);
	}

	/**
	 * called when the user digs a square with bomb
	 * 
	 * @return string "BOOM"
	 */
	@SuppressWarnings("serial")
	public static class boomException extends Exception {
		public static String message = "BOOM!\n";

		public final String message() {
			return message;
		}
	}
}