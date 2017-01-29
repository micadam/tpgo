package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.ActorRef;
import akka.actor.Kill;
import akka.actor.UntypedActor;
import models.msgs.DontGo;
import models.msgs.End;
import models.msgs.Go;
import models.msgs.Move;
import models.msgs.Prisoners;
import models.msgs.Start;
import models.msgs.Sync;
import models.msgs.Territories;
import play.Logger;
import play.libs.F.Callback;
import play.libs.Json;
import play.mvc.WebSocket;

public class Human extends UntypedActor{
    public final ActorRef             table;
    
    //TODO TODO
    private final int myColor;

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
				obj.put("type", "move");
				obj.put("x", m.x);
				obj.put("y", m.y);
				obj.put("color", m.color);
				out.write(obj);
			}
		} else if(message instanceof Sync ){
			Sync s = (Sync ) message;
			ObjectNode obj = Json.newObject();
			obj.put("type", "sync");
			obj.put("territories", s.isTerritories());
			ArrayNode arr = obj.putArray("board");
			
			int[][] board = s.getBoard();
			for(int[] row : board){
				for(int i : row){
					arr.add(i);
				}
			}
			out.write(obj);
			
		} else if (message instanceof End){
			End e = (End)message;
			ObjectNode obj = Json.newObject();
			obj.put("type", "end");
			obj.put("winner", e.getWinner());
			out.write(obj);
		} else if (message instanceof Territories ){
			ObjectNode obj = Json.newObject();
			obj.put("type", "territories");
			out.write(obj);
		} else if (message instanceof Prisoners){
			Prisoners p = (Prisoners)message;
			ObjectNode obj = Json.newObject();
			obj.put("type", "prisoners");
			obj.put("white", p.getWhitePrisoners());
			obj.put("black", p.getBlackPrisoners());
			out.write(obj);
		} else if (message instanceof String){
			String s = (String ) message;
			System.out.println(s);
		} else if (message instanceof Go) {
			ObjectNode obj = Json.newObject();
			obj.put("type", "go");
			out.write(obj);
		} else if (message instanceof DontGo) {
			ObjectNode obj = Json.newObject();
			obj.put("type", "dontGo");
			out.write(obj);
		} else if (message instanceof Start) {
			ObjectNode obj = Json.newObject();
			obj.put("type", "start");
			String myColorString = myColor == Move.BLACK ? "Black" : "White";
			obj.put("color", myColorString);
			out.write(obj	);
		} else {
			System.out.println("Unhandled message error");
		}
		
	}
	@Override
	public void postStop(){
		System.out.println("[Human] human out");
	}
	public Human(WebSocket.In<JsonNode> _in,
            WebSocket.Out<JsonNode> _out, ActorRef _table,
            final int _color){
		
		table = _table;
		in = _in;
		out = _out;
		myColor = _color;
		
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
}
