package goserver;

import java.util.ArrayList;
import java.util.List;

public class GameInstance implements Runnable {
	
	Thread thread;
	private static int BOARD_SIZE = 19;
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
	public int getBoardSize() {
		return BOARD_SIZE;
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
		Player loser=blackPlayer;
		Player winner=whitePlayer;
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
			}else if(x==-2){		//surrender
				currentPlayer.sendResponse("OK");
				gameOver=true;
				winner=notCurrentPlayer;
				loser=currentPlayer;
				System.out.println("Game is over, winner is player: " + -1*currentColor);
			}else if( x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE ) {
				currentPlayer.sendResponse("NO");
				currentColor *= -1;			//the player who played an invalid move moves again
				
			} else if (gameBoard[x][y] != 0){
				currentPlayer.sendResponse("NO");
				currentPlayer.sendCancellingMove(new Move(x,y,gameBoard[x][y]));
				currentColor *= -1;			
			} else {
				int boardChanged = 0;
				for(GameRule gameRule : rules) {
					int result =  gameRule.verifyMove(x, y, gameBoard, currentColor);
					if(result == -1) {
						boardChanged = -1;
						break;
					}
					boardChanged = boardChanged + result;
				}
				if(boardChanged > 0) {
					gameBoard[x][y] = currentColor;
					currentPlayer.sendResponse("OK");
					currentPlayer.sendResponse("SYNC");
					notCurrentPlayer.sendResponse("SYNC");
					sendBoardToPlayers();
				} else if(boardChanged == -1) {
					currentPlayer.sendResponse("NO");
					currentPlayer.sendCancellingMove(new Move(x,y,gameBoard[x][y]));
					currentColor *= -1;			
				} else {
					gameBoard[x][y] = currentColor;
					currentPlayer.sendResponse("OK");
					notCurrentPlayer.sendOpponentsMove(new Move(x, y, currentColor));
					
				}
				
			}
			
			currentColor *= -1;
		}
		winner.sendResponse("END WIN");
		loser.sendResponse("END LOSE");
		
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
	
	public GameInstance(Player player, String keyCode, int boardSize) {
		blackPlayer = player;
		this.keyCode = keyCode;
		BOARD_SIZE = boardSize;
	}

}
