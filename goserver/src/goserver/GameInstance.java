package goserver;

import java.util.ArrayList;
import java.util.List;

public class GameInstance extends Thread {
	
	Thread thread;
	private static int BOARD_SIZE = 19;
	private int[][] gameBoard;
	private List<GameRule> rules;
	int currentColor;
	private GoServer goServer;
	
	Player whitePlayer;
	Player blackPlayer;
	
	//private int currentState;
	private String keyCode;
	/*
	public int getState() {
		return currentState;
	}
	*/
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
		CaptureRule captureRule=new CaptureRule();
		rules.add(captureRule);
		Player loser=blackPlayer;
		Player winner=whitePlayer;
		boolean passFlag=false;
		boolean gameOver = false;
		while(gameOver == false) {
			Player currentPlayer = (currentColor == Move.BLACK_NUMBER ? blackPlayer : whitePlayer);
			Player notCurrentPlayer = (currentColor == Move.BLACK_NUMBER ? whitePlayer : blackPlayer);
			
			currentPlayer.sendAlert(currentColor);
			Move move = currentPlayer.getMove();
			int x = move.getX();
			int y = move.getY();
			int timeout=50;
			while(x==-100 && timeout>0){
				try {
					sleep(100);
					move=currentPlayer.getMove();
					x=move.getX();
					timeout--;
				} catch (InterruptedException e) {
					System.out.println("[Game Instance] timeout interrupt( InterruptedException)");
				}
			}
			if(timeout==0){
				x=-2;
			}
			if(x == -1) {
				currentPlayer.sendResponse("OK");
				System.out.println("Player passed");
				captureRule.dismissKo();
				if(passFlag){
					int[] arguments=new int[2];
					arguments[0]=currentColor;
					arguments[1]=captureRule.getScore();
					matchTerritories(arguments);
					if(arguments[0]!=0){
						currentColor=arguments[0];
						System.out.println("[GAME INSTANCE] game is being continued");
					}else if(arguments[1]>=0){	//>=because kumi rule adds 6,5 not 6 points to white
						winner=whitePlayer;
						loser=blackPlayer;
						gameOver=true;
					} else{
						winner=blackPlayer;
						loser=whitePlayer;
						gameOver=true;
					}
				}
				passFlag=true;
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
			if(x!=-1){
				passFlag=false;
			}
			
			currentColor *= -1;
		}
		winner.sendResponse("END WIN");
		loser.sendResponse("END LOSE");	
		winner.endCommunication();
		loser.endCommunication();
		goServer.remove(this);
	}
	private void matchTerritories(int[] args){
		int currentColor=args[0];
		int currentScore=args[1];
		whitePlayer.sendResponse("TERRITORIES START");
		blackPlayer.sendResponse("TERRITORIES START");
		System.out.println("[Server] territories start");
		int[][] board=new int[BOARD_SIZE][BOARD_SIZE];
		boolean done=false;
		boolean passFlag=false;
		while(!done){
			Player currentPlayer = (currentColor == Move.BLACK_NUMBER ? blackPlayer : whitePlayer);
			Player notCurrentPlayer = (currentColor == Move.BLACK_NUMBER ? whitePlayer : blackPlayer);
			currentPlayer.sendAlert(currentColor);
			Move move=currentPlayer.getMove();
			int x=move.getX();
			int y=move.getY();
			if(x==-1){		//fine with me
				currentPlayer.sendResponse("OK");
				currentColor*=-1;
				if(passFlag){
					done=true;
					currentScore=calculateScore(board,currentScore);
					currentColor=0;
				}
				passFlag=true;
			} else if(x==-2){		//Disagree, 
				currentPlayer.sendResponse("OK");
				done=true;
			}else if( x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE ) {
				currentPlayer.sendResponse("NO");
			} else{
				currentPlayer.sendResponse("OK");
				board[x][y]=currentColor;
				System.out.println("Sending opponenst move in territories");
				notCurrentPlayer.sendOpponentsMove(new Move(x,y,currentColor));
				//currentColor*=-1;		
			}
			if(x!=-1){
				passFlag=false;
			}
		}
		whitePlayer.sendResponse("TERRITORIES END");
		blackPlayer.sendResponse("TERRITORIES END");
		System.out.println("[Server] territories end");
		args[0]=currentColor;
		args[1]=currentScore;
		System.out.println("SCORE IS : "+ currentScore);
	}
	
	//score >= 0 -> white won
	private int calculateScore(int[][] board,int currentScore){
		System.out.println("Calculating score");
		int score=currentScore;
		for(int[] i : board){
			for(int j : i){
				score+=j;
			}
		}
		score+=6;		//KUMI RULE
		return score;
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
	
	public GameInstance(Player player, String keyCode, int boardSize,GoServer goServer) {
		blackPlayer = player;
		this.keyCode = keyCode;
		BOARD_SIZE = boardSize;
		this.goServer=goServer;
	}

}
