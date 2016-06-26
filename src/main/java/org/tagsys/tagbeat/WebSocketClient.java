package org.tagsys.tagbeat;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
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
public class WebSocketClient extends Thread {
	

	Session userSession = null;

	Gson gson = new Gson();

	Map<String, LinkedList<Tag>> readingBuffer = new HashMap<String, LinkedList<Tag>>();
	
	//The commands to be executed before compressive reading.
	Queue<Command> commands = new  LinkedBlockingQueue<Command>();

	public static CompressiveReading cr = CompressiveReading.instance();
	
	public WebSocketClient(URI endpointURI) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.connectToServer(this, endpointURI);

			this.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnOpen
	public void onOpen(Session userSession) {
		this.userSession = userSession;
	}

	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		this.userSession = null;
	}

	@OnMessage
	@SuppressWarnings("unchecked")
	public void onMessage(String message) {

		try {
			
			ReadingModel reading = gson.fromJson(message, ReadingModel.class);
						
			if (reading.getType().equals("readings")) {
				
				for (Tag tag : reading.getTags()) {
					
					if (!readingBuffer.containsKey(tag.getEpc())) {
						readingBuffer.put(tag.getEpc(), new LinkedList<Tag>());
					}

					LinkedList<Tag> readingQueue = readingBuffer.get(tag.getEpc());

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

	
	public void addCommmand(Command command){
		this.commands.add(command);
	}
	
	@Override
	public void run() {

		while (true) {
			try {
				Thread.sleep(100);
				
				Command command = commands.poll();
				while(command!=null){
					command.execute();
					command = commands.poll();
				}

				
				for (String epc : readingBuffer.keySet()) {
					
					
					LinkedList<Tag> tags = readingBuffer.get(epc);

					int size = tags.size();

					List<Long> timeList = new ArrayList<Long>();

					List<Double> phaseList = new ArrayList<Double>();
				
					if (size > 1 && (tags.get(size - 1).getFirstSeenTime() - tags.get(0).getFirstSeenTime()) > cr.getSampleNumber()*1000) {

						Tag tag = null;
						for (int i = 0; i < size; i++) {
							tag = tags.poll();
							if(tag!=null){
								timeList.add(tag.getFirstSeenTime());
								phaseList.add(tag.getPhase() / 4096.0 * 2 * Math.PI);
							}
						}

						long[] time = new long[timeList.size()];
						double[] phase = new double[phaseList.size()];
						for (int i = 0; i < timeList.size(); i++) {
							time[i] = timeList.get(i).longValue();
							phase[i] = phaseList.get(i).doubleValue();
						}

						Signal signal = cr.recover(time, phase);

						WebSocketServer.getInstance().broadcast(epc, signal);
						
						System.gc();

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
