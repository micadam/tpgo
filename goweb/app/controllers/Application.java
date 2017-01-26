package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import models.Table;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result joinTable(String tableName, String bot) {
    	
    	System.out.println(tableName);

    	return ok(views.html.gameBoard.render(tableName, bot));
    	
    }
    
    public static WebSocket<JsonNode> join(final String tableName, String bot) {
    	boolean botMode = bot.equals("bot");
        return new WebSocket<JsonNode>() {
            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
                // Join the table
                try {                	
                	Table.join(tableName, in, out, botMode);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }
    
    public static Result gameBoardJs (String tableName, String bot) {
    	return ok(views.js.gameBoard.render(tableName, bot));
    }

}
