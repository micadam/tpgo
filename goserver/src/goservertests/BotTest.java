package goservertests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import goserver.Bot;
import goserver.Move;

public class BotTest {
	Bot bot;
	int BOARD_SIZE = 19;

	@Before
	public void init() {
		bot = new Bot(BOARD_SIZE);
	}

	@Test
	public void testBotResponseToGetMove() {
		Move move = bot.getMove();
		int x = move.getX();
		int y = move.getY();
		if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE)
			fail("Bot's move was out of board");
	}
	
	@Test (timeout=1000)
	public void testBotWhenNoMoveIsPossible() {
		String board="";
		for (int i = 0; i < BOARD_SIZE; i++) {
			for(int j=0 ; j< BOARD_SIZE ; j++){
				board+='1';
			}
		}
		bot.sendBoard(board, BOARD_SIZE);
		Move move=new Move(1,1,1);
		while(move.getX()!=-1){	//until pass
			move=bot.getMove();
			bot.sendResponse("NO");
		}
	}
	@Test
	public void testBotCorrectingMistake(){
		Move move1=bot.getMove();
		bot.sendResponse("NO");
		Move move2=bot.getMove();
		if(move1.getX()==move2.getX() && move1.getX()==move2.getY())
			fail("Made the same move");
	}
	@Test
	public void testBotInTerritoriesMode(){
		bot.sendResponse("TERRITORIES");
		bot.getMove();		//do anything
	}
	@After
	public void clean() {
		bot=null;
	}

}
