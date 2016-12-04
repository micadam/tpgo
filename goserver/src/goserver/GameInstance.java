package goserver;

import java.util.ArrayList;
import java.util.List;

public class GameInstance implements Runnable {
	
	Thread thread;
	public static final int BOARD_SIZE = 19;
	private int[][] gameBoard;
	private List<GameRule> rules;
	int currentColor;
	
	Player whitePlayer;
	Player blackPlayer;
	
	private int currentState;
	private String keyCode;
	
	public int getState() {
		return currentState;
	}
	public String getKeyCode() {
		return keyCode;
	}
	
	public boolean addPlayer(Player player) {
		if(whitePlayer == null) {			
			whitePlayer = player;
			thread = new Thread(this);
			thread.start();
			return true;
		} else {
			return false;
		}
	}
	@Override
	public void run() {
		gameBoard = new int[BOARD_SIZE][BOARD_SIZE];
		currentColor = Move.BLACK_NUMBER;
		rules = new ArrayList<GameRule>();
		rules.add(new CaptureRule());
		boolean gameOver = false;
		while(gameOver == false) {
			Player currentPlayer = (currentColor == Move.BLACK_NUMBER ? blackPlayer : whitePlayer);
			Player notCurrentPlayer = (currentColor == Move.BLACK_NUMBER ? whitePlayer : blackPlayer);
			
			currentPlayer.sendAlert(currentColor);
			Move move = currentPlayer.getMove();
			int x = move.getX();
			int y = move.getY();
			if(x == -1) {
				currentPlayer.sendResponse("OK");
				System.out.println("Player passed");
			}
			else if( x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE ) {
				currentPlayer.sendResponse("NO");
				currentColor *= -1;			//the player who played an invalid move moves again
				
			} else if (gameBoard[x][y] != 0){
				currentPlayer.sendResponse("NO");
				currentPlayer.sendCancellingMove(new Move(x,y,gameBoard[x][y]));
				currentColor *= -1;			
			} else {
				gameBoard[x][y] = currentColor;
				currentPlayer.sendResponse("OK");
				boolean boardChanged = false;
				for(GameRule gameRule : rules) {
					boolean result =  gameRule.verifyMove(x, y, gameBoard);
					boardChanged = (boardChanged || result);
					System.out.println("PP" + boardChanged + result);
				}
				if(boardChanged) {
					currentPlayer.sendResponse("SYNC");
					notCurrentPlayer.sendResponse("SYNC");
					sendBoardToPlayers();
				}
				notCurrentPlayer.sendOpponentsMove(new Move(x, y, currentColor));
				
			}
			
			currentColor *= -1;
		}
		
	}
	
	private void sendBoardToPlayers() {
		String boardRaw = "";
		for(int i = 0; i < BOARD_SIZE; i++) {
			for(int j = 0; j < BOARD_SIZE; j++) {
				if(gameBoard[i][j] == -1) {
					boardRaw += '2';
				} else {
					boardRaw += gameBoard[i][j];
				}
			}
		}
		
		whitePlayer.sendBoard(boardRaw, BOARD_SIZE);
		blackPlayer.sendBoard(boardRaw, BOARD_SIZE);
	}
	
	public GameInstance(Player player, String keyCode) {
		blackPlayer = player;
		this.keyCode = keyCode;
	}

}
