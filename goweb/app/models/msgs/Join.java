package models.msgs;


import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.WebSocket;

public class Join {

    final String username;
    final WebSocket.In<JsonNode> in;
    final WebSocket.Out<JsonNode> out;
    
    public String getUsername() {
		return username;
	}
    public WebSocket.In<JsonNode> getIn(){
    	return in;
    }
    public WebSocket.Out<JsonNode> getOut(){
    	return out;
    }
   
    public Join(String username,WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
        this.username = username;
        this.in = in;
        this.out = out;
    }
}

