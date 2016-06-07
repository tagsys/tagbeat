package org.tagsys.tagbeat;


import com.google.gson.Gson;

import spark.Spark;

public class Server {
	
	private static Gson gson = new Gson();

	public static void main(String[] args) {


		Spark.port(9093);

		Spark.webSocket("/socket", WebSocketHandler.class);

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


		Spark.exception(Exception.class, (e, req, resp) -> {

			resp.status(200);
			resp.type("application/json");

			resp.body(new JsonResult(505, e.getMessage()).toString());

		});

	}

}
