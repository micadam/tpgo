package models;

import akka.actor.UntypedActor;
import models.msgs.*;

public class Human extends UntypedActor{

	@Override
	public void onReceive(Object message) throws Exception {
		if( message instanceof Move ){
			//wait for move
			Move response = new Move();
			getSender().tell(response, getSelf());
		} else if(message instanceof Opponent) {
			Opponent o = (Opponent ) message;
			if(o.getX() != -1){ 	//pass handling
				setField(o.getX(),o.getY(),o.getColor());
			}
		} else if(message instanceof Sync ){
			Sync s = (Sync ) message;
			setBoard(s.getBoard());
		} else if( message instanceof ReDo){
			ReDo r = (ReDo ) message;
			setField(r.getX(),r.getY(),r.getColor());
		} else if (message instanceof End){
			//end game 
		} else if (message instanceof Territories ){
			//start and end territories
		} else if (message instanceof Prisoners){
			Prisoners p = (Prisoners ) message;
			setWhitePrisoners(p.getWhitePrisoners());
			setBlackPrisoners(p.getBlackPrisoners());
		} else if (message instanceof String){
			String s = (String ) message;
			System.out.println(s);
		} else {
			System.out.println("Unhandled message error");
		}
		
	}
	public void setField(int x, int y, int color){
		
	}
	public void setBoard(int[][] board){
		//
	}
	public Human(){
		
	}
	public void setBlackPrisoners(int prisoners){
		
	}
	public void setWhitePrisoners(int prisoners){
		
	}
}
