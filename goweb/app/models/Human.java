package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import models.msgs.End;
import models.msgs.Move;
import models.msgs.Opponent;
import models.msgs.Prisoners;
import models.msgs.ReDo;
import models.msgs.Sync;
import models.msgs.Territories;
import play.Logger;
import play.libs.F.Callback;
import play.libs.Json;
import play.mvc.WebSocket;

public class Human extends UntypedActor{
    public final ActorRef             table;
    
    //TODO TODO
    private final int myColor = 1;

    protected WebSocket.In<JsonNode>  in;
    protected WebSocket.Out<JsonNode> out;
    
    
	@Override
	public void onReceive(Object message) throws Exception {
		System.out.println("[ACTOR]: received message: " + message.toString());
		if( message instanceof Move ){
			//wait for move
			if(getSender() != table) {				
				table.tell(message, getSelf());
			} else {
				Move m = (Move)message;
				ObjectNode obj = Json.newObject();
				obj.put("x", m.x);
				obj.put("y", m.y);
				obj.put("color", m.color);
				out.write(obj);
			}
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
	public Human(WebSocket.In<JsonNode> _in,
            WebSocket.Out<JsonNode> _out, ActorRef _table){
		
		table = _table;
		in = _in;
		out = _out;
		
        in.onMessage(new Callback<JsonNode>()
        {
            @Override
            public void invoke(JsonNode event)
            {
                try
                {
                	System.out.println(event.toString());
                	int x = event.get("x").asInt();
                	int y = event.get("y").asInt();
                	getSelf().tell(new Move(x, y, myColor), getSelf());                	
                	System.out.println(event.toString());
                }
                catch (Exception e)
                {
                	e.printStackTrace();
                    Logger.error("invokeError");
                }
                
            }
        });
		
		
	}
	public void setBlackPrisoners(int prisoners){
		
	}
	public void setWhitePrisoners(int prisoners){
		
	}
}
