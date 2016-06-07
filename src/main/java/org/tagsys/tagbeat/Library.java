package org.tagsys.tagscreen;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;

import com.google.gson.Gson;

public class Library {

	public static class Comment {

		
		public String username;
		
		public String progress;

		public String text;

	}

	public static class Content {

		public String id;

		public List<Comment> comments = new ArrayList<Comment>();

	}

	private static Gson gson = new Gson();

	private static Library instance = null;

	public String version;

	public List<Content> contents = new ArrayList<Content>();

	@Override
	public String toString() {
		return gson.toJson(this);
	}

	public void save() {

		try {
			File file = new File("./public/data/lib.json");
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(this.toString());
			bw.close();
			file.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Library getInstance() {

		if (instance == null) {
			String libtext = "";
			Charset charset = Charset.forName("UTF-8");
			Path file = Paths.get("./public/data/lib.json");
			try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					libtext = libtext + line;
				}
				instance = gson.fromJson(libtext, Library.class);
			} catch (IOException x) {
				System.err.format("IOException: %s%n", x);

			}

		}

		return instance;

	}
	
	public void addComment(String username, String contentId, String progress, String text){
		
		Comment comment = new Comment();
		comment.username = username;
		comment.progress = progress;
		comment.text = text;
		
		System.out.println(username+":"+contentId+":"+progress+":"+text);
		
		Content target = null;
		for(Content content:this.contents){
			if(content.id.equals(contentId)){
				target = content;
				break;
			}
		}
		
		if(target == null){
			target = new Content();
			target.id = contentId;
			this.contents.add(target);
		}
	
		target.comments.add(comment);
	
		this.save();
		
	}

}
