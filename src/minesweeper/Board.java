package minesweeper;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import minesweeper.Square.boomException;

/**
 * The Board class is thread safe. 
 * 
 * Mutator methods except the three constructor cases have been synchronized
 * 
 * These synchronizations are needed because 'dig', 'flag', and 'deflag' would
 * all potentially be influenced by race conditions in a multi-player (i.e., 
 * multi-threaded) setting. Similarly, toString, which reflects the current board 
 * state, is also synchronized to ensure valid state update on the board. 
 * 
 */


public class Board {
	
	/* field definitions:
	 * field "squares" is a two dimensional array of instances of square
	 * field "size" is an integer indicating the row and column numbers
	 */
    private final Square[][] squares;
    private final int size;

    // constructor
    public Board(boolean[][] bombState) {
        this.size = bombState.length;
        this.squares = new Square[size][size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                squares[x][y] = new Square(bombState[x][y]);
            }
        }

        generateNeighborList();

    }

    // constructor based on size
    public Board(int size) {
        final double bombProbability = 0.25;
        this.size = size;
        this.squares = new Square[size][size];
        // randomly generate Board
        Random randomBomb = new Random();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                squares[x][y] = new Square(randomBomb.nextDouble() < bombProbability);
            }
        }

        generateNeighborList();

    }

    // constructor based on filename
    @SuppressWarnings("resource")
    public Board(String filename) {
        DataInputStream input = null;
        try {
            final FileInputStream stream = new FileInputStream(filename);
            input = new DataInputStream(stream);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(
                    input));
            String currentLine = reader.readLine();
            String[] tokens = currentLine.split("\\s");
            size = tokens.length;
            squares = new Square[size][size];
            int x = 0;
            while (currentLine != null) {
                tokens = currentLine.split("\\s");
                if (tokens.length != size) {
                	// horizontal dimension != size; throw exception
                    throw new FileException();
                }
                for (int y = 0; y < size; y++) {
                	// input file must contain zero's and one's only
                	if (!tokens[y].equals("1") && !tokens[y].equals("0")){
                		throw new FileException();
                	}
                	else {
                    squares[x][y] = new Square(tokens[y].equals("1"));
                	}
                }
                currentLine = reader.readLine();
                x++;
            }
            if (x != size) {
            	// vertical dimension != size; throw exception
                throw new FileException();
            }
        } catch (FileException e) {
            throw new RuntimeException("invalid file format!");

        } catch (IOException e) {
            throw new RuntimeException("IO exception");

        } finally {
            try {
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("game cannot close properly!");
            }
        }

        generateNeighborList();

    }

    /*
     *  when this method is called by a square instance, a list containing all of its 
     *  neighboring squares (maximal = 8) is generated
     */
    private synchronized void generateNeighborList() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int diffx = -1; diffx <= 1; diffx++) { // diffx = -1, 0, or 1
                    for (int diffy = -1; diffy <= 1; diffy++) { // diffy = -1, 0, or 1
                        final int xcurrent = x + diffx;
                        final int ycurrent = y + diffy;
                        if (xcurrent >= 0 && ycurrent >= 0 && xcurrent < size
                                && ycurrent < size) {
                        	if (diffx == 0 && diffy == 0){
                        		// it's the same square as the dug square, don't do anything
                        	}else{
                        		squares[x][y].addAdjacent(squares[xcurrent][ycurrent]);
                        	}
                            
                        }

                    }
                }
            }
        }

    }

    /**
     * @param x: horizontal int coordinate
     * @param y: vertical int coordinate
     * @return a specific string messages after digging
     **/
    public String dig(int x, int y) {
        if (x >= 0 && y >= 0 && x < size && y < size) {
            try {
                squares[x][y].dig();
            } catch (boomException b) {
                return b.message();
            }

        }
        // if there is no BOOM! message, return board message
        return toString();
    }

    public String flag(int x, int y) {

        if (x >= 0 && y >= 0 && x < size && y < size) {
            squares[x][y].flag();
        }
        // return board message
        return toString();
    }

    public String deflag(int x, int y) {

        if (x >= 0 && y >= 0 && x < size && y < size) {
            squares[x][y].deflag();

        }
        // return board message
        return toString();

    }

    /**
     * @returns the string representation of the board
     **/
    public synchronized String toString() {
        StringBuilder output = new StringBuilder();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                output.append(squares[x][y].toString());
                if (y != size - 1) {
                    output.append(' ');
                } else {
                    output.append('\n');
                }
            }
        }
        return output.toString();
    }

    
    /**
     * (for test only! not part of the implementation code)
     * 
     * @return int size n of an n-by-n mine field
     */
    public int size() {
        return size;
    }

    /**
     * (for test only! not part of the implementation code)
     * 
     * @return string representation of the actual bomb state of a mine field
     * a state string could either by 'B' (i.e., bomb) or 'Integer' (number of neighboring bombs)
     */
    public synchronized String bombDistribution() {
        StringBuilder output = new StringBuilder();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                output.append(squares[x][y].actualState());
                if (y != size - 1) {
                    output.append(' ');
                } else {
                    output.append('\n');
                }
            }
        }
        return output.toString();
    }

    @SuppressWarnings("serial")
    private static class FileException extends Exception {
    }
}