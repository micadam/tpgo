package goserver;

import java.util.Random;

public class Bot implements Player {
	private GameInstance gameInstance;
	private Move opponentsMove;
	private Random random;
	private static final int BOARD_SIZE=19;
	@Override
	public Move getMove() {
		// TODO Auto-generated method stub
		Move move=null;
		int[][] gameBoard = gameInstance.getBoard();
		if(opponentsMove!=null){
			if(opponentsMove.getX()==-1){
				//TODO, change sendOpponentsMove to access this code
			}else{
				int iCount=0;
				int jCount=0;
				int x=opponentsMove.getX();
				int y=opponentsMove.getY();
				//int range =2,3 .. , so he looks further 
				for(int i=random.nextInt(3);iCount<3;i=(i+1)%3){		//i,j = 0,1,2
					jCount=0;
					for(int j=random.nextInt(3);jCount<3;j=(j+1)%3){
						int moveX=x+i-1;
						int moveY=y+j-1;
						if(!(moveX < 0 || moveY < 0 || moveX >= BOARD_SIZE || moveY >= BOARD_SIZE || gameBoard[moveX][moveY] != 0)){
							move=new Move(moveX,moveY,0);
							break;
						}
						jCount++;
					}
					iCount++;
				}
				if(move==null){					//weak bot mode
					for(int i=0;i<BOARD_SIZE;i++){
						for(int j=0;j<BOARD_SIZE;j++){
							if(gameBoard[i][j]==0){
								move=new Move(i,j,0);
								break;
							}
						}
					}
				}
			}
		}else{
			move=new Move(random.nextInt(BOARD_SIZE),random.nextInt(BOARD_SIZE),0);
		}
		return move;
	}

	@Override
	public void sendResponse(String response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendAlert(int color) {
		// TODO Auto-generated method stub
		
	}
	public void setRules(){
		//TODO, so it can do more advanced stuff
	}

	@Override
	public void sendOpponentsMove(Move move) {
		if(move.getX()!=-1)			//if pass then doesn't matter
			this.opponentsMove=move;
	}

	@Override
	public void sendBoard(String gameBoardRaw, int gameBoardSize) {
		// TODO Auto-generated method stub
		
	}
	public Bot(GameInstance gameInstance){
		random=new Random();
		this.gameInstance=gameInstance;
		System.out.println("Bot for game "+ gameInstance.getKeyCode()+" created");

	}
	
}
