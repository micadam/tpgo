package models;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.msgs.Join;
import play.libs.Akka;
import play.mvc.WebSocket;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
public class Table extends UntypedActor {
    
    //	Table map
    static Map<String,ActorRef> tables = new HashMap<String,ActorRef>();
    // Members of this table.
    private ActorRef whitePlayer;
    private ActorRef blackPlayer;
    
    public static void join(final String name, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out ) throws Exception{
        // Send the Join message to the table
    	ActorRef table = tables.get(name);
    	if(table == null){
    		table = Akka.system().actorOf(Props.create(Table.class));
    		tables.put(name, table);
    	}
        String result = (String)Await.result(ask(table,new Join(name,in,out), 1000), Duration.create(1, SECONDS)); 
        System.out.println(result);                     
    }

	@Override
	public void onReceive(Object message) throws Exception {
		
		if(message instanceof Join ){
			Join join = (Join) message;
			if(blackPlayer != null){		//maximum number of player
				getSender().tell("FULL",getSelf());
			}else {
            	ActorRef player =  Akka.system().actorOf(
                        Props.create(Human.class, join.getIn(), join.getOut(), getSelf() ));
            	String text =  "New player entered the game"; 
            	if(whitePlayer == null){
            		whitePlayer = player;
            	}
            	else{
            		blackPlayer = player;
            		blackPlayer.tell(text, getSelf());
            	}  
            	whitePlayer.tell(text, getSelf());
                getSender().tell("OK", getSelf());
			}
		}
	}
}
