package goserver;

import java.util.ArrayList;
import java.util.List;

public class GameInstance extends Thread {
	
	private final int boardSize ;
	//private int[][] gameBoard;
	private List<GameRule> rules;
	int currentColor;
	private GoServer goServer;
	
	Player whitePlayer;
	Player blackPlayer;
	
	private String keyCode;
	
	public int getBoardSize() {
		return boardSize;
	}
	public String getKeyCode() {
		return keyCode;
	}
	
	public boolean addPlayer(Player player) {
		if(whitePlayer == null) {			
			whitePlayer = player;
			return true;
		} else {
			return false;
		}
	}
	@Override
	public void run() {
		int[][] gameBoard = new int[boardSize][boardSize];
		currentColor = Move.BLACK_NUMBER;
		rules = new ArrayList<GameRule>();
		CaptureRule captureRule=new CaptureRule();
		rules.add(captureRule);
		Player loser=blackPlayer;
		Player winner=whitePlayer;
		boolean passFlag=false;
		boolean gameOver = false;
		
		while(whitePlayer == null && !gameOver){		//waiting for player two
			gameOver = ! blackPlayer.isActive();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
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
					Thread.sleep(100);
					move = currentPlayer.getMove();
					x = move.getX();
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
					matchTerritories(arguments,gameBoard);
					if(arguments[0]!=0){
						currentColor=arguments[0];
						currentColor*=-1;		//because later it is changed
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
					//System.out.println("[Game instance] Score is "+ arguments[1]);
				}
				passFlag=true;
			}else if(x==-2){		//surrender
				currentPlayer.sendResponse("OK");
				gameOver=true;
				winner=notCurrentPlayer;
				loser=currentPlayer;
				System.out.println("Game is over, winner is player: " + -1*currentColor);
			}else if( x < 0 || y < 0 || x >= boardSize || y >= boardSize ) {
				currentPlayer.sendResponse("NO");
				currentColor *= -1;			//the player who played an invalid move moves again
				
			} else if (gameBoard[x][y] != 0){
				currentPlayer.sendResponse("NO");
				System.out.println("This field is not empty");
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
				System.out.println("Board changed has value: " + boardChanged);
				if(boardChanged > 0) {
					gameBoard[x][y] = currentColor;
					currentPlayer.sendResponse("OK");
					currentPlayer.sendResponse("SYNC");
					notCurrentPlayer.sendResponse("SYNC");
					sendBoardToPlayers(gameBoard);
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
			if(x != -1){
				passFlag=false;
			}
			
			currentColor *= -1;
		}
		if(winner!= null){
			winner.sendResponse("END WIN");
			winner.endCommunication();
		}
		if(loser != null){
			loser.sendResponse("END LOSE");	
			loser.endCommunication();
		}
		System.out.println("Game ended");
		goServer.remove(this);
	}
	private void matchTerritories(int[] args,int[][] gameBoard){
		int currentColor=args[0];
		int currentScore=args[1];
		whitePlayer.sendResponse("TERRITORIES START");
		blackPlayer.sendResponse("TERRITORIES START");
		System.out.println("[Server] territories start");
		int[][] board=new int[boardSize][boardSize];
		boolean done=false;
		boolean passFlag=false;
		boolean[][] visitedTemp = new boolean[boardSize][boardSize];
		for(int x =0; x < boardSize; x ++ ){
			for( int y =0 ; y < boardSize ; y ++ ) {
				if(!visitedTemp[x][y]){
					PawnGroupAlgorithm.fillThisGroup(x, y, visitedTemp, board, gameBoard);
				}
			}
		}
		whitePlayer.sendResponse("SYNC");
		blackPlayer.sendResponse("SYNC");
		sendBoardToPlayers(board);
		Move lastMove=null;
		while(!done){
			Player currentPlayer = (currentColor == Move.BLACK_NUMBER ? blackPlayer : whitePlayer);
			Player notCurrentPlayer = (currentColor == Move.BLACK_NUMBER ? whitePlayer : blackPlayer);
			currentPlayer.sendAlert(currentColor);
			Move move=currentPlayer.getMove();
			int x=move.getX();
			int y=move.getY();
			int timeout=50;
			while(x==-100 && timeout>0){
				try {
					Thread.sleep(100);
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
			if(x==-1){		//fine with me
				lastMove=null;
				currentPlayer.sendResponse("OK");
				currentColor*=-1;
				if(passFlag){
					done=true;
					currentScore=calculateScore(board,currentScore,gameBoard);
					currentColor=0;
				}
				passFlag=true;
			} else if(x==-2){		//stop this and continue playing
				currentPlayer.sendResponse("OK");
				done=true;
			}else if (x==-3){		//disagree
				if(lastMove!=null){
					currentPlayer.sendResponse("OK");
					boolean[][] visited = new boolean[boardSize][boardSize];
					PawnGroupAlgorithm.getBreathsOfThisGroup(lastMove.getX(),lastMove.getY(),visited,board,gameBoard,0,0,1);
					currentPlayer.sendResponse("SYNC");
					notCurrentPlayer.sendResponse("SYNC");
					sendBoardToPlayers(board);
					lastMove=null;
				}else 
					currentPlayer.sendResponse("NO");
			}else if( x < 0 || y < 0 || x >= boardSize || y >= boardSize ) {
				currentPlayer.sendResponse("NO");
			} else{
				currentPlayer.sendResponse("OK");
				lastMove=new Move(x,y,currentColor);
				boolean[][] visited = new boolean[boardSize][boardSize];
				int tempColor = move.getColor() == 2 ? 0 : currentColor;
				System.out.println("Temp color is " + tempColor);
				PawnGroupAlgorithm.getBreathsOfThisGroup(x,y,visited,board,gameBoard,tempColor,0,1);
				currentPlayer.sendResponse("SYNC");
				notCurrentPlayer.sendResponse("SYNC");
				sendBoardToPlayers(board);
				currentColor*=-1;		
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
	private int calculateScore(int[][] territoriesBoard,int currentScore,int[][] gameBoard){
		System.out.println("Calculating score");
		int score=currentScore;
		for(int[] i : territoriesBoard){
			for(int j : i){
				score+=j;
			}
		}
		for(int[] i : gameBoard){
			for(int j : i){
				score+=j;
			}
		}
		score+=6;		//KUMI RULE
		return score;
	}
	private void sendBoardToPlayers(int[][] board) {
		String boardRaw = "";
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				if(board[i][j] == -1) {
					boardRaw += '2';
				} else {
					boardRaw += board[i][j];
				}
			}
		}
		
		whitePlayer.sendBoard(boardRaw, boardSize);
		blackPlayer.sendBoard(boardRaw, boardSize);
	}
	
	public GameInstance(Player player, String keyCode, int boardSize,GoServer goServer) {
		blackPlayer = player;
		this.keyCode = keyCode;
		this.boardSize = boardSize;
		this.goServer=goServer;
	}

}
