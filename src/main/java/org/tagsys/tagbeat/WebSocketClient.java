package org.tagsys.tagbeat;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.tagsys.tagbeat.commands.Command;
import org.tagsys.tagbeat.cr.CompressiveReading;
import org.tagsys.tagbeat.cr.Signal;

import com.google.gson.Gson;

/**
 * This client is to receive messages from TagSee
 * 
 * @author Young
 *
 */
@ClientEndpoint
public class WebSocketClient  {
	
//	Session userSession = null;

	Gson gson = new Gson();

	Map<String, LinkedBlockingDeque<Tag>> readingBuffer = new HashMap<String, LinkedBlockingDeque<Tag>>();

	private  Session session;
		
	public WebSocketClient(URI endpointURI) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			session = container.connectToServer(this, endpointURI);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void add(List<Tag> tags){
		
		for(Tag tag: tags){
			
			if(!readingBuffer.containsKey(tag.getEpc())){
				readingBuffer.put(tag.getEpc(), new LinkedBlockingDeque<Tag>());
			}
			
			readingBuffer.get(tag.getEpc()).add(tag);
			
		}
		
	}
	
	public void disconnect() throws IOException{
		
	
		
		this.session.close();
				
	}
	
	public void clear(){
		this.readingBuffer.clear();
	}

//	@OnOpen
//	public void onOpen(Session userSession) {
//		this.userSession = userSession;
//	}
//
//	@OnClose
//	public void onClose(Session userSession, CloseReason reason) {
//		this.userSession = null;
//	}

	@OnMessage
	@SuppressWarnings("unchecked")
	public void onMessage(String message) {

		try {
			
			ReadingModel reading = gson.fromJson(message, ReadingModel.class);
						
			if (reading.getType().equals("readings")) {
				
				for (Tag tag : reading.getTags()) {
					
					if (!readingBuffer.containsKey(tag.getEpc())) {
						readingBuffer.put(tag.getEpc(), new LinkedBlockingDeque<Tag>());
					}

					Queue<Tag> readingQueue = readingBuffer.get(tag.getEpc());

					// remove the oldest one to avoid overflow
					if (readingQueue.size() > 20000) {
						readingQueue.poll();
					}

					readingQueue.add(tag);
				}

			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}

	

}
