package org.tagsys.tagbeat.commands;

import org.tagsys.tagbeat.cr.CompressiveReading;

public class ChangeSparsity implements Command{

	
	int K = 10;
	
	public ChangeSparsity(int K){
		this.K = K;
	}

	@Override
	public void execute() {
		
		CompressiveReading.instance().changeSparsity(K);		

	}
	
	
	
}
