package goservertests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import goserver.CaptureRule;

public class CaptureRuleTest {
	CaptureRule captureRule;
	int[][] board;
	int BOARD_SIZE=19;
	@Before
	public void init(){
		captureRule=new CaptureRule();
		board=new int[BOARD_SIZE][BOARD_SIZE];
		board[0][1]=1;
		board[1][0]=1;
		board[1][1]=-1;
		board[2][0]=-1;
		board[2][2]=-1;
		board[1][2]=1;
		board[3][1]=-1;
		board[0][3]=1;
	}
	
	@Test
	public void testKoRule(){
		captureRule.verifyMove(2, 1, board, 1);
		board[2][1]=1;
		assertTrue("Ko rule not spotted",captureRule.verifyMove(1, 1, board, -1)==-1);
	}
	@Test
	public void testSuicideRule(){
		assertEquals("Suicide in that possition should not be possible",captureRule.verifyMove(0,2,board,-1),-1);
		assertEquals("Capture was not made, but should be possible",captureRule.verifyMove(2, 1, board, 1),1);
	}
	@Test
	public void testBoardChanges(){
		board[2][1]=0;
		captureRule.verifyMove(2, 1, board, 1);
		assertEquals("Board should not change",board[2][1],0);
		board[5][5]=0;
		captureRule.verifyMove(2, 1, board, 1);
		assertFalse("Board Should not change",board[5][5]==1);
	}
	@Test
	public void testScoreCounting(){
		assertTrue("Score should be zero",captureRule.getScore()==0);
		captureRule.verifyMove(2, 1, board, 1);
		assertFalse("Score should not be zero",captureRule.getScore()==0);
	}
	
	@After
	public void clear(){
		captureRule=null;
		board=null;
	}
}

