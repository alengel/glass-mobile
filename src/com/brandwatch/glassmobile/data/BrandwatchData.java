package com.brandwatch.glassmobile.data;

import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.glass.brandwatch.utils.DateHelper;
import com.glass.brandwatch.utils.HttpRequest;
import com.glass.brandwatch.utils.PropertiesManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BrandwatchData {
	private static final String TAG = BrandwatchData.class.getSimpleName();

	public static String getBrandwatchQueryId(String url, String query) {
		Log.i(TAG, "Measurement " + String.format("Requesting queryId for query '%s'", query));

		String queryUrl = buildQueryUrl(url, query);
		String data = getData(queryUrl);
		JsonObject results = getResults(data);
		String resultsString = results.get("id").toString();
		
		Log.i(TAG, "Measurement " + "Received queryId");
		
		return resultsString;
	}

	public static String getSentimentData(String url, String queryId) {
		Log.i(TAG, "Measurement " + String.format("Requesting sentiment data for queryId '%s'", queryId));

		String sentimentUrl = buildSentimentUrl(url, queryId);
		String data = getData(sentimentUrl);
		
		Log.i(TAG, "Measurement " + "Receiving sentiment");
		
		return data;
	}

	public static String getTopicsData(String url, String queryId) {
		Log.i(TAG, "Measurement " + String.format("Requesting topics data for queryId '%s'", queryId));

		String topicstUrl = buildTopicsUrl(url, queryId);
		String data = getData(topicstUrl);
		
		Log.i(TAG, "Measurement " + "Receiving topics");
		
		return data;
	}

	private static String getData(String url) {
		// Delegate the GET request to HttpRequest
		HttpResponse response = HttpRequest.doHttpGet(url, null);

		try {
			return EntityUtils.toString(response.getEntity());

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		return null;
	}

	private static String buildSentimentUrl(String url, String queryId) {
		String sentimentUrl = "data/volume/months/sentiment/?";
		return url + sentimentUrl + buildFilters() + queryId;
	}

	private static String buildTopicsUrl(String url, String queryId) {
		String topicsUrl = "data/volume/topics/queries/?";
		return url + topicsUrl + buildFilters() + queryId;
	}

	private static String buildFilters() {
		String brandwatchKey = PropertiesManager.getProperty("brandwatch_auth");
		String startDate = DateHelper.getDateSevenDaysAgo();
		String endDate = DateHelper.getDateFormat(new Date());
		String filters = "&endDate=" + endDate + "&startDate=" + startDate + "&queryId=";

		return brandwatchKey + filters;
	}

	private static String buildQueryUrl(String url, String query) {
		String brandwatchKey = PropertiesManager.getProperty("brandwatch_auth");
		return url + "queries/?" + brandwatchKey + "&nameContains=" + query.replace(' ', '+');
	}

	private static JsonObject getResults(String responseString) {
		JsonElement jElement = new JsonParser().parse(responseString);
		JsonObject jObject = jElement.getAsJsonObject();
		JsonArray jArray = jObject.getAsJsonArray("results");
		jObject = jArray.get(0).getAsJsonObject();

		return jObject;
	}
}