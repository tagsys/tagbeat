package org.tagsys.tagbeat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Benchmark {

	
	public static final String HISTORY_DIRECTORY = "./benchmark";


	public static List<Tag> read(String filename) throws IOException {

		List<Tag> tags = new LinkedList<Tag>();
		BufferedReader br = new BufferedReader(new FileReader(HISTORY_DIRECTORY+"/"+filename));
		
		int v = 0;//default version
		if(filename.contains(".v2.")){
			v = 2;// for tagsee default format
		}

		String line = br.readLine();
		line = br.readLine();// ignore the first row;

		while (line != null) {

			String[] fields = line.split(",");

			Tag tag = new Tag();
			if(v == 0){
				tag.setEpc(fields[0]);
				tag.setAntenna(Integer.parseInt(fields[1]));
				tag.setChannel(Integer.parseInt(fields[2]));
				tag.setDoppler(Integer.parseInt(fields[3]));
				tag.setRssi(Integer.parseInt(fields[4]));
				tag.setPeekRssi(Integer.parseInt(fields[5]));
				tag.setPhase(Integer.parseInt(fields[6]));
				tag.setFirstSeenTime(Long.parseLong(fields[7]));
				tag.setLastSeenTime(Long.parseLong(fields[8]));
				tag.setSeenCount(Integer.parseInt(fields[9]));
				tag.setTimestamp(Long.parseLong(fields[10]));
			}else if(v == 2){
				tag.setEpc(fields[0]);
				tag.setAntenna(Integer.parseInt(fields[1]));
				tag.setChannel(Integer.parseInt(fields[2]));
				tag.setPhase(Integer.parseInt(fields[3]));
				tag.setRssi(Integer.parseInt(fields[4]));
				tag.setFirstSeenTime(Long.parseLong(fields[5]));
				tag.setTimestamp(Long.parseLong(fields[6]));				
			}

			tags.add(tag);

			line = br.readLine();
		}

		br.close();

		return tags;

	}

	public static List<String> getHistory() {

		
		List<String> files = new ArrayList<String>();
		
		File folder = new File(HISTORY_DIRECTORY);

		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".csv")) {
				files.add(listOfFiles[i].getName());
			}
		}
		
		return files;

	}
	
	public static void main(String[] args) {
		
		System.out.println(getHistory());
		
	}

}
