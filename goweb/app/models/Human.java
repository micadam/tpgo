package models;

import akka.actor.UntypedActor;
import models.msgs.*;

public class Human extends UntypedActor{

	@Override
	public void onReceive(Object message) throws Exception {
		if( message instanceof Move ){
			Move m = (Move ) message;
			if(m.getX() != -1){
				setField(m.getX(),m.getY(),m.getCOlor());
			}
			//wait for move
			Move response;
			getSender().tell(response, getSelf());
		} /*else if(message instanceof Opponent) {
			//connect it to Move
		} */else if(message instanceof Sync ){
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
	public Human(){
		
	}
}
