package goserver;

import java.util.Random;

public class Bot implements Player {
	private Move opponentsMove;
	private Random random;
	private Move rejectedMove=new Move(-123,-123,0);
	private int wrongMoveCount;
	private Move myLastMove=new Move(-123,-123,0);
	private int[][] gameBoard;
	private final int BOARD_SIZE;
	private boolean territoriesMode=false;

	@Override
	public Move getMove() {
		Move move = null;
		if(wrongMoveCount>=BOARD_SIZE*BOARD_SIZE || territoriesMode){
			move=new Move(-1,-1,0);	//pass
		} else if (opponentsMove != null) {
			if (opponentsMove.getX() == -1) {
				// TODO, change sendOpponentsMove to access this code
			} else {
				int iCount = 0;
				int jCount = 0;
				int x = opponentsMove.getX();
				int y = opponentsMove.getY();
				// int range =2,3 .. , so he looks further
				for (int i = random.nextInt(3); iCount < 3; i = (i + 1) % 3) { // i,j
																				// =
																				// 0,1,2
					jCount = 0;
					for (int j = random.nextInt(3); jCount < 3; j = (j + 1) % 3) {
						int moveX = x + i - 1;
						int moveY = y + j - 1;
						if (moveX >= 0 && moveY >= 0 && moveX < BOARD_SIZE && moveY < BOARD_SIZE
								&& gameBoard[moveX][moveY] == 0 && moveX!=rejectedMove.getX()&& moveY!=rejectedMove.getY()){
								
							move = new Move(moveX, moveY, 0);
							break;
						}
						jCount++;
					}
					iCount++;
				}
				if (move == null) { // weak bot mode
					for (int i = 0; i < BOARD_SIZE; i++) {
						for (int j = 0; j < BOARD_SIZE; j++) {
							if (gameBoard[i][j] == 0 && i!=rejectedMove.getX()&& j!=rejectedMove.getY()) {
								move = new Move(i, j, 0);
								break;
							}
						}
					}
				}
			}
		} else {
			move = new Move(random.nextInt(BOARD_SIZE), random.nextInt(BOARD_SIZE), 0);
		}
		myLastMove=move;
		return move;
	}

	@Override
	public void sendResponse(String response) {
		if(response.equals("NO")){
			wrongMoveCount++;
			rejectedMove=myLastMove;
		} else if(response.equals("TERRITORIES END")){
			territoriesMode=false;
		} else if(response.equals("TERRITORIES START")){
			territoriesMode=true;
		} else { //ok 
			wrongMoveCount=0;
		}

	}

	@Override
	public void sendAlert(int color) {
		// TODO Auto-generated method stub

	}

	public void setRules() {
		// TODO, so it can do more advanced stuff
	}

	@Override
	public void sendOpponentsMove(Move move) {
		if(territoriesMode)
			return;
		if (move.getX() != -1) { // if pass then doesn't matter
			this.opponentsMove = move;
			gameBoard[move.getX()][move.getY()] = move.getColor();
		}
	}

	@Override
	public void sendBoard(String boardRaw, int boardSize) {
		if(territoriesMode)
			return;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				char curField = boardRaw.charAt(i * boardSize + j);
				int fieldValue;
				if (curField == '2') {
					fieldValue = -1;
				} else {
					fieldValue = curField - '0';
				}
				gameBoard[i][j] = fieldValue;
			}
		}
	}

	public Bot(int boardSize) {
		random = new Random();
		this.BOARD_SIZE = boardSize;
		gameBoard = new int[BOARD_SIZE][BOARD_SIZE];
	}

	@Override
	public void sendCancellingMove(Move move) {
		if(territoriesMode)
			return;
		rejectedMove=move;
		gameBoard[move.getX()][move.getY()]=move.getColor();
	}

	@Override
	public void endCommunication() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isActive() {
		return true;
	}

}
