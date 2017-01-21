package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result joinTable(String tableName) {
    	
    	System.out.println(tableName);
    	
    	return ok(views.html.gameBoard.render("testt"));
    	
    }
    
    public static Result gameBoard() {
    	return ok(views.html.gameBoard.render("test"));
    }

}
