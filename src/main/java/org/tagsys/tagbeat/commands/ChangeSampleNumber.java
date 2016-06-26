package org.tagsys.tagbeat.commands;

import org.tagsys.tagbeat.cr.CompressiveReading;

public class ChangeSampleNumber implements Command{

	int N=5000;
	
	public ChangeSampleNumber(int N){
		this.N = N;
	}
	
	@Override
	public void execute() {

		CompressiveReading.instance().changeSampleNumber(N);
	
	
		
	}

}
