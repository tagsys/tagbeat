package org.tagsys.tagbeat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.tagsys.tagbeat.commands.Command;
import org.tagsys.tagbeat.cr.CompressiveReading;
import org.tagsys.tagbeat.cr.Signal;

public class Processor extends Thread{

	
	//The commands to be executed before compressive reading.
	Queue<Command> commands = new  LinkedBlockingQueue<Command>();

	//receiving readings from tagsee
	WebSocketClient socketClient;
	
	HashMap<String, Boolean> filters = new HashMap<String, Boolean>();
	
	protected boolean stopped = false;
	
	public static CompressiveReading cr = CompressiveReading.instance();


	public Processor(WebSocketClient socketClient){
			
		this.socketClient = socketClient;
		
		this.start();
	}
	
	public void addCommmand(Command command){
		this.commands.add(command);
	}
	
	public Map<String, Boolean> getFilters(){
		return this.filters;
	}
	
	public WebSocketClient getSocketClient(){
		
		return this.socketClient;
	}
	
	public void terminate(){
		
		this.stopped = true;
		
	}
	
	@Override
	public void run() {

		while (stopped==false) {
			try {
				Thread.sleep(100);
				
				Command command = commands.poll();
				while(command!=null){
					command.execute();
					command = commands.poll();
					System.out.println("execute commands");
				}
								
				for (String epc : this.socketClient.readingBuffer.keySet()) {
					
					if(!filters.containsKey(epc)){
						filters.put(epc, Boolean.TRUE);
					}
					
					if(filters.get(epc)==Boolean.FALSE){
						this.socketClient.readingBuffer.get(epc).clear();
						continue;
					}
										
					LinkedBlockingDeque<Tag> tags = this.socketClient.readingBuffer.get(epc);
					
					List<Long> timeList = new ArrayList<Long>();

					List<Double> phaseList = new ArrayList<Double>();
					
					if(tags.size()==0) continue;
					//not get sufficient sampls
					if((tags.getLast().getFirstSeenTime()-tags.getFirst().getFirstSeenTime()) < cr.getSampleNumber()*1000) {
						continue;
					}
				
					Tag tag = null;
					tag = tags.poll();
					while(tag!=null){
						
						//break when get sufficient samples
						if(timeList.size()>1 && (tag.getFirstSeenTime()-timeList.get(0))>cr.getSampleNumber()*1000){
							break;
						}
						
						timeList.add(tag.getFirstSeenTime());
						phaseList.add(tag.getPhase() / 4096.0 * 2 * Math.PI);
						
						tag = tags.poll();
						
					}
			
					Signal signal = cr.recover(timeList, phaseList);

					if(WebSocketServer.getInstance()!=null){
						WebSocketServer.getInstance().broadcast(epc, signal);
					}
					
					System.gc();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
