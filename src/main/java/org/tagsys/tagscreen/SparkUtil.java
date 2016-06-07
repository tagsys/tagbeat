package org.tagsys.tagscreen;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import spark.Request;
import spark.utils.StringUtils;

public class SparkUtil {
	
	private static Gson gson = new Gson();

	public static Map<String, String> bodyParams(Request req){
		String body = req.body();
		System.out.println(body);
		if(!StringUtils.isEmpty(body)){
			HashMap<String, String> bodyParams = (HashMap<String, String>)gson.fromJson(body, new HashMap<String, String>().getClass());
			return bodyParams;
		}
		return new HashMap<String, String>();
	}
	
}
