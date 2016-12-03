package goserver;

public class GameInstance implements Runnable {
	
	Thread thread;
	public static final int BOARD_SIZE = 19;
	private int[][] gameBoard;
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
		boolean gameOver = false;
		while(gameOver == false) {
			Player currentPlayer = (currentColor == Move.BLACK_NUMBER ? blackPlayer : whitePlayer);
			Player notCurrentPlayer = (currentColor == Move.BLACK_NUMBER ? whitePlayer : blackPlayer);
			
			currentPlayer.sendAlert(currentColor);
			Move move = currentPlayer.getMove();
			int x = move.getX();
			int y = move.getY();
			if( x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE || gameBoard[x][y] != 0) {
				currentPlayer.sendResponse("NO");				
			}	
			else {
				currentPlayer.sendResponse("OK");
				notCurrentPlayer.sendOpponentsMove(new Move(x, y, currentColor));
				
			}
			
			currentColor *= -1;
		}
		
	}
	
	public GameInstance(Player player, String keyCode) {
		blackPlayer = player;
		this.keyCode = keyCode;
	}

}
