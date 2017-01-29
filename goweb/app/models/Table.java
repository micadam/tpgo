package models;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import akka.actor.Kill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.PawnGroupAlgorithm;
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
    private String tableName;
    private ActorRef currentPlayer = null;
    private boolean territoriesMode = false;
    private boolean botMode = false;
    private final int boardSize = 19;
    private boolean passFlag;
    private int[][] gameBoard = new int[boardSize][boardSize];
    private int[][] territoriesBoard = new int[boardSize][boardSize];
    private CaptureRule captureRule = new CaptureRule();
    boolean whiteReady = false;
    boolean blackReady = false;
    
    public static void join(final String name, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out, 
    		boolean botMode) throws Exception{
        // Send the Join message to the table
    	ActorRef table = tables.get(name);
    	if(table == null){
    		table = Akka.system().actorOf(Props.create(Table.class,name, botMode));
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
            	if(whitePlayer == null){
            		
            		whitePlayer = player;
            		if(botMode){
	            		blackPlayer = Akka.system().actorOf(Props.create(Bot.class, getSelf(),Move.BLACK));
	            		currentPlayer = blackPlayer;
	            		notifyCurrentPlayer();
	            		System.out.println("[Table " + tableName + " ] game started");
            		}
            	}
            	else{
            		blackPlayer = player;
            		notifyBoth(new Start());
            		currentPlayer = blackPlayer;
            		notifyCurrentPlayer();
            		System.out.println("[Table " + tableName + " ] game started");
            	}  
            	
                getSender().tell("OK", getSelf());
			}
		} else if(message instanceof Move){
			if(territoriesMode) {
				tagTerritories((Move) message);
			} else if(getSender() == currentPlayer) {
				System.out.println("Got the message from the right player");
				checkMove((Move) message);
			} else {
				System.out.println("Got a message from the wrong player");
			}
			
		}  
	}
	
	
	private void checkMove(Move m){
		int x = m.x;
		int y = m.y;
		int color = m.color;
		if(x == -1 ){		//pass
			System.out.println("Pass");
			captureRule.dismissKo();
			if(passFlag){
				//Double pass handling in here
				territoriesMode = true;
				fillTerritories();
				notifyBoth(new Territories());
				notifyBoth(new Sync(territoriesBoard, true));
			} else {
				passFlag = true;				
				swapPlayers();
			}
		} else if ( x == -2 ){ 	//surrender
			String winner = (currentPlayer == whitePlayer ? "Black" : "White");
			notifyBoth(new End(winner));
			endGame();
		} else if (  x < 0 || y < 0 || x >= boardSize || y >= boardSize ){
			// Do nothing? XD 
		} else if ( gameBoard[x][y] != 0 ){
			//the spot was taken, tell the player that
			currentPlayer.tell(new Move(x,y,gameBoard[x][y]), getSelf());
		} else {
			passFlag = false;
			int boardChanged = captureRule.verifyMove(x, y, gameBoard, color);
			if(boardChanged > 0) {
				gameBoard[x][y] = color;
				Sync sync = new Sync(gameBoard, false);
				notifyBoth(sync);
				notifyBoth(new Prisoners(captureRule.getWhitePrisoners(), captureRule.getBlackPrisoners()));
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
	
	private void tagTerritories(Move move ){
		int x = move.x;
		int y = move.y;
		//int color = move.color;
		int color = ((territoriesBoard[x][y] + 2) % 3) - 1; //it should cycle between black white and empty
		System.out.println("Fill color: " + color);
		if(x == -1){		//ready
			//TODO
			if(getSender() == whitePlayer){
				whiteReady = true;
			} else {
				blackReady = true;
			}
			if(whiteReady && blackReady ){
				int whiteScore = 0;
				int blackScore = 0;
				for(int i = 0; i < boardSize; i++) {
					for(int j = 0; j < boardSize; j++) {
						if(territoriesBoard[i][j] == Move.WHITE) {
							whiteScore++;
						} else if (territoriesBoard[i][j] == Move.BLACK) {
							blackScore++;
						}
					}
				}
				whiteScore += captureRule.getBlackPrisoners();
				blackScore += captureRule.getWhitePrisoners();
				String winner;
				if(whiteScore > blackScore) {
					winner = "White";
				} else if (blackScore > whiteScore) {
					winner = "Black";
				} else {
					//draw?
					winner = "umm";
				}
				notifyBoth(new End(winner));
				endGame();
				
				
			}
		} else if (  x >= 0 && y >= 0 && x < boardSize && y < boardSize && gameBoard[x][y] == 0 ){
			boolean[][] visited = new boolean[boardSize][boardSize];
			PawnGroupAlgorithm.getBreathsOfThisGroup(x,y,visited,territoriesBoard,gameBoard,color,0,1);
			Sync sync = new Sync(territoriesBoard, true);
			notifyBoth(sync);
		}
	}
	
	private void fillTerritories(){
		territoriesBoard = new int[boardSize][boardSize];
		boolean[][] visitedTemp = new boolean[boardSize][boardSize];
		for(int x =0; x < boardSize; x ++ ){
			for( int y =0 ; y < boardSize ; y ++ ) {
				if(!visitedTemp[x][y]){
					PawnGroupAlgorithm.fillThisGroup(x, y, visitedTemp, territoriesBoard, gameBoard);
				}
			}
		}
		
	}
	
	private void swapPlayers(){
		currentPlayer = (currentPlayer == whitePlayer ? blackPlayer : whitePlayer);
		notifyCurrentPlayer();
	}
	private void notifyBoth(Object msg){
		whitePlayer.tell(msg, getSelf());
		blackPlayer.tell(msg, getSelf());
	}
	
	private void notifyCurrentPlayer() {
		currentPlayer.tell(new Go(), getSelf());
		ActorRef notCurrentPlayer = currentPlayer == whitePlayer ? blackPlayer : whitePlayer;
		notCurrentPlayer.tell(new DontGo(), getSelf());
	}
	
	public void endGame(){
		territoriesMode = false;
		currentPlayer = null;
		tables.remove(tableName,this);
		getSelf().tell(Kill.getInstance(),getSelf());
	}
	@Override 
	public void postStop(){
		System.out.println("[Table] table ( " + tableName + " ) out" );
	}
	public Table(String name, boolean botMode){
		tableName = name;
		this.botMode = botMode;
	}
}
