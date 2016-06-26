package org.tagsys.tagbeat;


import java.net.URI;
import java.net.URISyntaxException;

import org.tagsys.tagbeat.commands.ChangeFrameSize;
import org.tagsys.tagbeat.commands.ChangeSampleNumber;
import org.tagsys.tagbeat.commands.ChangeSparsity;
import org.tagsys.tagbeat.cr.CompressiveReading;

import com.google.gson.Gson;

import spark.Spark;

public class TagbeatServer {
	
	private static Gson gson = new Gson();

	public static void main(String[] args) throws URISyntaxException {


		Spark.port(9001);
		
		//please ensure tagsee has been started firstly.
		WebSocketClient socketClient = new WebSocketClient(new URI("ws://localhost:9092/socket"));

		Spark.webSocket("/socket", WebSocketServer.class);
		
		Spark.externalStaticFileLocation("public");

		Spark.before((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Request-Method", "*");
			response.header("Access-Control-Allow-Headers", "X-Requested-With");
		});

		Spark.init();

		Spark.get("/", (req, resp) -> {
			resp.redirect("/index.html");
			return "";
		});
		
		Spark.get("/changeParam", (req,resp)->{
			
			String NString = req.queryParams("N");
			String QString = req.queryParams("Q");
			String KString = req.queryParams("K");
			
			if(NString!=null){
				socketClient.addCommmand(new ChangeSampleNumber(Integer.parseInt(NString)));
			}
			
			if(QString!=null){
				socketClient.addCommmand(new ChangeFrameSize(Integer.parseInt(QString)));
			}
			
			if(KString!=null){
				socketClient.addCommmand(new ChangeSparsity(Integer.parseInt(KString)));
			}
			
			resp.type("application/json");
			
			return new JsonResult(0).toString();
						
		});
		
		

		Spark.exception(Exception.class, (e, req, resp) -> {

			resp.status(200);
			resp.type("application/json");

			resp.body(new JsonResult(505, e.getMessage()).toString());

		});
		

	}

}
