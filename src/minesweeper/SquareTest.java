package minesweeper;

import static org.junit.Assert.assertEquals;
import minesweeper.Square.boomException;

import org.junit.Test;

/**
 * 
 * Testing for an instance of the Square class, which is essentially a 1-by-1 mine field
 * 
 * Test Strategy:
 * 
 * (A) Test 'dig' behavior for a square
 *     i. with bomb
 *     ii. without bomb
 * (B) Test for expected 'flag' and 'deflag' behaviors
 */

public class SquareTest {
    
    @Test(expected=boomException.class)
    public void bombTest1() throws boomException{
        Square singleSquare = new Square(true);
        singleSquare.dig();
    }
    
    @Test
    public void bombTest2() throws boomException{
        Square singleSquare = new Square(false);
        singleSquare.dig();
        assertEquals(" ",singleSquare.toString());
    }
    
    @Test
    public void FlagAndDeflag(){
        Square singleSquare = new Square(false);
        singleSquare.flag();
        assertEquals("F",singleSquare.toString());
        singleSquare.deflag();
        assertEquals("-",singleSquare.toString());

    } 
}