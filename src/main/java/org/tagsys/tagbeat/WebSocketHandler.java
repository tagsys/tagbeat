package org.tagsys.tagbeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import com.google.gson.Gson;

@WebSocket
public class WebSocketHandler {

	static class SocketMessage{
		
		public String type;
		
		public String contentId;
		
		public String username;
				
		public String progress;
		
		public String text;

	
		@Override
		public String toString(){
			return type+":"+username+":"+contentId;
		}
		
	}
	
	private static List<Session> sessions = new ArrayList<Session>();

	private int numUsers;
	
	private Gson gson = new Gson();
	
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
    	
    	numUsers ++;
    	
    	System.out.println("on connect");
    	
    	sessions.add(session);
    		
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
    	
    	numUsers --;
    	System.out.println("on close");
    	
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
    	
    	System.out.println(message);
    		
		SocketMessage socketMsg = gson.fromJson(message, SocketMessage.class);
    	
    	
    
    }
    
    public void broadcast(Map<String,String> message){
    	
    	sessions.stream().filter(Session::isOpen).forEach(session->{
    		try {
				
    			System.out.println(gson.toJson(message));
    			
    			session.getRemote().sendString(gson.toJson(message));
    			
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
    	});
    	
    }
    

   
 
}