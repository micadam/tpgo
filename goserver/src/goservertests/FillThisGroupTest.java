package goservertests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import goserver.PawnGroupAlgorithm;

public class FillThisGroupTest {

	int[][] gameBoard;
	int[][] territoriesBoard;
	boolean[][] visitedBoard;
	int boardSize = 19;
	@Before
	public void init(){
		gameBoard = new int[boardSize][boardSize];
		territoriesBoard = new int[boardSize][boardSize];
		visitedBoard = new boolean[boardSize][boardSize];
	}
	@Test
	public void testOnEmpty() {
		PawnGroupAlgorithm.fillThisGroup(0, 0, visitedBoard, territoriesBoard, gameBoard);
		for(int i =0 ; i < boardSize ; i ++ ) { 
			for(int j = 0 ; j < boardSize; j ++ ) {
				assertEquals("Algorithm foud a group that does not exist",territoriesBoard[i][j],0);
			}
		}
	}
	
	@Test
	public void testAlgorithm() {
		gameBoard[5][5] = 1;
		gameBoard[3][5] = 1;
		gameBoard[4][4] = 1;
		gameBoard[4][6] = 1;
		
		gameBoard[0][1] = -1;
		gameBoard[1][1] = -1;
		gameBoard[2][0] = -1;
		int white=0,black=0;
		for(int i =0 ; i < boardSize ; i ++ ) { 
			for(int j = 0 ; j < boardSize; j ++ ) {
				if(!visitedBoard[i][j])
					PawnGroupAlgorithm.fillThisGroup(i, j, visitedBoard, territoriesBoard, gameBoard);
				if(territoriesBoard[i][j] == 1)
					white++;
				else if (territoriesBoard[i][j] == -1 )
					black++;
			}
		}
		assertEquals(black,2);
		assertEquals(white,1);
	}
	@Test
	public void testOnCaptured(){
		int counter=0;
		gameBoard[5][5] = 1;
		PawnGroupAlgorithm.fillThisGroup(5, 5, visitedBoard, territoriesBoard, gameBoard);
		for(int i =0 ; i < boardSize ; i ++ ) { 
			for(int j = 0 ; j < boardSize; j ++ ) {
				if(territoriesBoard[i][j] != 0 )
					counter++;
			}
		}
		assertEquals(counter,0);
	}
	
	@After 
	public void clean(){
		gameBoard = null;
		territoriesBoard = null;
		visitedBoard = null;
	}

}
