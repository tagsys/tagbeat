package org.tagsys.tagbeat.commands;

import org.tagsys.tagbeat.cr.CompressiveReading;

public class ChangeFrameSize implements Command{

	int Q;
	
	public ChangeFrameSize(int Q){
		this.Q = Q;
	}
	
	@Override
	public void execute() {
		CompressiveReading.instance().changeFrameSize(Q);
	}
	
}
