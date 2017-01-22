package models;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.msgs.*;
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
    
    private ActorRef currentPlayer = null;
    private boolean territoriesMode = false;
    
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
				int color = whitePlayer == null? Move.WHITE : Move.BLACK;
            	ActorRef player =  Akka.system().actorOf(
                        Props.create(Human.class, join.getIn(), join.getOut(), getSelf(),color ));
            	String text =  "New player entered the game"; 
            	if(whitePlayer == null){
            		whitePlayer = player;
            		whitePlayer.tell(text, getSelf());
            	}
            	else{
            		blackPlayer = player;
            		whitePlayer.tell(text, getSelf());
            		blackPlayer.tell(text, getSelf());
            		Go go = new Go();
            		blackPlayer.tell(go, getSelf());			//starts the game 
            		currentPlayer = blackPlayer;
            	}  
            	
                getSender().tell("OK", getSelf());
			}
		} else if(message instanceof Move){
			if(getSender() == currentPlayer) {
				System.out.println("Got the message from the right player");
				//TODO move checks
				checkMove((Move) message);
				
				/*
				whitePlayer.tell(message, getSelf());
				//TODO this should be earlier, using for testing
				 //if black is null, currentPlayer is also null so we never enter this if
				if(blackPlayer != null) {					
					blackPlayer.tell(message, getSelf());
				}
				*/
			} else {
				System.out.println("Got a message from the wrong player");
			}
			
		} else if(message instanceof Territories){
			//change mode
		} 
	}
	
	
	private final int boardSize = 19;
	private boolean passFlag;
	private int[][] gameBoard = new int[boardSize][boardSize];
	private CaptureRule captureRule = new CaptureRule();
	private void checkMove(Move m){
		int x = m.x;
		int y = m.y;
		int color = m.color;
		if(x == -1 ){		//pass
			captureRule.dismissKo();
			if(passFlag){
				//Doubple pass handling in here
			} else 
				passFlag = true;
		} else if ( x == -2 ){ 	//surrender
			String winner = (currentPlayer == whitePlayer ? "white" : "black");
			winner = "Game ended, and the winner is " + winner;
			notifyBoth(winner);
			notifyBoth(new End());
		} else if (  x < 0 || y < 0 || x >= boardSize || y >= boardSize ){
			// Do nothing? XD 
		} else if ( gameBoard[x][y] != 0 ){
			currentPlayer.tell(new Move(x,y,gameBoard[x][y]), getSelf());
		} else {
			passFlag = false;
			int boardChanged = captureRule.verifyMove(x, y, gameBoard, color);
			if(boardChanged > 0) {
				gameBoard[x][y] = color;
				Sync sync = new Sync(gameBoard);
				notifyBoth(sync);
				swapPlayers();
			} else if(boardChanged == -1) {
				//currentPlayer.sendResponse("NO");
				currentPlayer.tell(new Move(x,y,gameBoard[x][y]),getSelf());	
			} else {
				gameBoard[x][y] = color;
				notifyBoth(new Move(x, y, color));
				swapPlayers();
			}
			
		}
	}
	
	private void swapPlayers(){
		currentPlayer = (currentPlayer == whitePlayer ? blackPlayer : whitePlayer);
	}
	private void notifyBoth(Object msg){
		whitePlayer.tell(msg, getSelf());
		blackPlayer.tell(msg, getSelf());
	}
}
