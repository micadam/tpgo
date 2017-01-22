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
    
    public static Result joinTable(String tableName) {
    	
    	System.out.println(tableName);

    	return ok(views.html.gameBoard.render("testt"));
    	
    }
    
    public static WebSocket<JsonNode> join(final String tableName) {
        return new WebSocket<JsonNode>() {
            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
                // Join the table
                try {                	
                	Table.join(tableName, in, out);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }
    
    public static Result gameBoard() {
    	return ok(views.html.gameBoard.render("test"));
    }
    
    public static Result gameBoardJs (String userName) {
    	return ok(views.js.gameBoard.render("test3"));
    }

}
