package org.tagsys.tagbeat.commands;

import java.util.HashMap;
import java.util.Iterator;

import org.tagsys.tagbeat.Processor;

import spark.utils.StringUtils;

public class Filtering implements Command{

	Processor processor;
	HashMap<String, Boolean> filters;
	
	public Filtering(Processor processor, HashMap<String, Boolean> filters){
		
		this.processor = processor;
		
		this.filters = filters;
	}
	
	@Override
	public void execute() {
		
		Iterator<String> iterator = this.filters.keySet().iterator();
				
		while(iterator.hasNext()){
			String epc = iterator.next();
			this.processor.getFilters().put(epc, this.filters.get(epc));
		}
		
		System.out.println("execute filtering:"+ this.filters.toString());
		
	}

}
