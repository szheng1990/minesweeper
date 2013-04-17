package minesweeper;

import static org.junit.Assert.assertEquals;
import minesweeper.Square.boomException;

import org.junit.Test;

/**
 * 
 * Test Strategy:
 * 
 * (A) Test that the Board can load a text file with legal format
 * (B) Test that the Board throws FileException if the file is illegal
 *     i. The x and y dimensions of the file are different
 *     ii. The file contain numbers other than 0 and 1
 * (C) Test the boolean constructor of the Board
 * (D) Test that 'flag' and 'deflag' lead to the expected behavior of the Board state
 * (E) Test the Board state following a 'dig' message
 *     i. single digging
 *     ii. recursive digging
 *     iii. correct state update after a BOOM message
 *
 */

public class BoardTest {

    @Test
    public void testgoodBoard() {
        Board board = new Board("src/minesweeper/server/goodBoard.txt");
        String expected = "- - - - -\n- - - - -\n- - - - -\n- - - - -\n- - - - -\n";
        assertEquals(5, board.size());
        assertEquals(expected, board.toString());
        expected = "2 B 2 2 B\nB 3 B 3 2\n1 2 3 B 2\n0 0 2 B 2\n0 0 1 1 1\n";
        assertEquals(expected, board.bombDistribution());
        board.dig(0, 0);
        expected = "2 - - - -\n- - - - -\n- - - - -\n- - - - -\n- - - - -\n";
        assertEquals(expected, board.toString());

    }

    @Test
    public void testBooleanConstructor() {
    	// 3 by 3
        boolean[][] input = { { false, true, false }, { true, false, true },
                { false, false, false } };
        Board b = new Board(input);
        assertEquals("- - -\n- - -\n- - -\n", b.toString());
        assertEquals("2 B 2\nB 3 B\n1 2 1\n", b.bombDistribution());

    }

    @Test(expected = Exception.class)
    public void testBadBoard1() throws Exception {
    	// expected size error
        new Board("src/minesweeper/server/badBoard1.txt");
    }
    
    
    
    @Test(expected = Exception.class)
    public void testBadBoard2() throws Exception {
    	// board file incorrectly contains a "2"
        new Board("src/minesweeper/server/badBoard2.txt");
    }
    
    @Test
    public void testFlag() {
        boolean[][] input = {{true}};
        Board b = new Board(input);
        b.flag(0, 0);
        assertEquals("F\n", b.toString());

    }

    @Test
    public void testDeflag() {
        boolean[][] input = {{true}};
        Board b = new Board(input);
        b.flag(0, 0);
        b.deflag(0, 0);
        assertEquals("-\n", b.toString());
    }

    @Test
    public void testNonRecursiveDig() {
    	Board board = new Board("src/minesweeper/server/goodBoard.txt");
        board.dig(0, 2);
        String expected = "- - 2 - -\n- - - - -\n- - - - -\n- - - - -\n- - - - -\n";
        assertEquals(expected, board.toString());
    }

    @Test
    public void testRecursiveDig() {
        boolean[][] input = { { true, true, false }, { true, false, false },
                { false, false, false } };
        Board board = new Board(input);
        board.dig(2, 2);
        assertEquals("- - -\n- 3 1\n- 1  \n", board.toString());
    }


    @Test
    public void testBoomMessage() {
    	Board board = new Board("src/minesweeper/server/goodBoard.txt");
        assertEquals(boomException.message, board.dig(0, 1));
        String expected = "- 2 1 - -\n- - - - -\n- - - - -\n- - - - -\n- - - - -\n";
        assertEquals(expected, board.dig(0, 2));

    }

}