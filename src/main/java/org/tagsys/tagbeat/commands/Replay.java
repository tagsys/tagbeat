package org.tagsys.tagbeat.commands;

import java.io.IOException;
import java.util.List;

import org.tagsys.tagbeat.Benchmark;
import org.tagsys.tagbeat.Processor;
import org.tagsys.tagbeat.Tag;
import org.tagsys.tagbeat.WebSocketClient;

public class Replay implements Command{

	
	Processor processor;
	
	String filename;
	
	public Replay(Processor processor, String filename){
		
		this.processor = processor;
		this.filename = filename;
	}
	
	@Override
	public void execute() {

		WebSocketClient socketClient = processor.getSocketClient();
		
		socketClient.clear();
		
		try {
			List<Tag> tags = Benchmark.read(this.filename);
			socketClient.add(tags);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
