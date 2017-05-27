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
import org.tagsys.tagbeat.cr.Signal;

import com.google.gson.Gson;

/**
 * Handler the messages needed to broadcast to browsers.
 * 
 * @author Young
 *
 */
@WebSocket
public class WebSocketServer {

	private static List<Session> sessions = new ArrayList<Session>();
		
	protected static WebSocketServer instance;
	
	public static WebSocketServer getInstance(){
		return instance;
	}
	
	public WebSocketServer(){
		
		instance = this;
		
	}
	
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
    	    	
    	System.out.println("on connect");
    	
    	sessions.add(session);
    		
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
    	    	
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
   	
    	//keep nothing, we don't need receive any message from browser.
  
    }
    
   
    public void broadcast(String epc, Signal signal){
    	
    	sessions.stream().filter(Session::isOpen).forEach(session->{
    		try {
				
    			if(session.isOpen()){
    				JsonResult result = new JsonResult();
        			result.put("type", "cr");
        			result.put("epc", epc);
        			
        			result.put("originalSignal", signal.getPhaseSeries().getColumn(0));
        			result.put("recoveredSignal", signal.getRecoveredSeries().getColumn(0));
        			
        			session.getRemote().sendString(result.toString());
    			}
    		
    			    			
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
    	});
    	
    }
    

   
 
}