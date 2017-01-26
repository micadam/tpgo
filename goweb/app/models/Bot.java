package models;

import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Kill;
import akka.actor.UntypedActor;
import models.msgs.End;
import models.msgs.Go;
import models.msgs.Move;
import models.msgs.Sync;
import models.msgs.Territories;

public class Bot extends UntypedActor{
	ActorRef table;
	final int myColor;
	final int boardSize = 19;
	int[][] Board = new int[boardSize][boardSize];
	
	boolean territoriesMode = false;
	Move myLast = null;
	Move opponentsMove = null;
	Move rejectedMove = null;
	int wrongMoveCount = 0;
	Random random = new Random();
	@Override
	public void onReceive(Object message) throws Exception {
		System.out.println("[ACTOR]: received message: " + message.toString());
		if( message instanceof Move ){
			Move m = ( Move) message;
			Board[m.x][m.y]= m.color;
			if (myLast.x == m.x && myLast.y == m.y ){
				wrongMoveCount ++;
				rejectedMove = m;
			} else {
				wrongMoveCount = 0;
				opponentsMove = m;
			}
			makeMove();
			
		} else if(message instanceof Sync ){
			Sync s = (Sync ) message;
			Board = s.getBoard();
			makeMove();

		} else if (message instanceof Go){
			makeMove();
		}else if (message instanceof End){
			table = null;
			getSelf().tell(Kill.getInstance(), getSelf());
		} else if (message instanceof Territories ){
			//start and end territories
		} else {
			System.out.println("[BOT] Unhandled message");
		}
	}
	@Override
	public void postStop(){
		System.out.println("[Bot] bot out");
	}
	public Bot(ActorRef _table, final int _color){
		table = _table;
		myColor = _color;
	}
	private void makeMove(){
		Move move = null;
		if(wrongMoveCount>=boardSize*boardSize || territoriesMode){
			move=new Move(-1,-1,myColor);	//pass
		} else if (opponentsMove != null) {
			if (opponentsMove.x == -1) {
				move = new Move(-1,-1,myColor);
			} else {
				int iCount = 0;
				int jCount = 0;
				int x = opponentsMove.x;
				int y = opponentsMove.y;
				// int range =2,3 .. , so he looks further
				for (int i = random.nextInt(3); iCount < 3; i = (i + 1) % 3) { // i,j
																				// =
																				// 0,1,2
					jCount = 0;
					for (int j = random.nextInt(3); jCount < 3; j = (j + 1) % 3) {
						int moveX = x + i - 1;
						int moveY = y + j - 1;
						if (moveX >= 0 && moveY >= 0 && moveX < boardSize && moveY < boardSize
								&& Board[moveX][moveY] == 0 && moveX!=rejectedMove.x&& moveY!=rejectedMove.y){
								
							move = new Move(moveX, moveY, myColor);
							break;
						}
						jCount++;
					}
					iCount++;
				}
				if (move == null) { // weak bot mode
					for (int i = 0; i < boardSize; i++) {
						for (int j = 0; j < boardSize; j++) {
							if (Board[i][j] == 0 && i!=rejectedMove.x&& j!=rejectedMove.y) {
								move = new Move(i, j, myColor);
								break;
							}
						}
					}
				}
			}
		} else {
			move = new Move(random.nextInt(boardSize), random.nextInt(boardSize), myColor);
		}
		myLast=move;
		table.tell(move, getSelf());
		System.out.println("[Bot] move made " + move.x + " "+  move.y +" " + move.color);
	}
}
