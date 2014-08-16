package com.brandwatch.glassmobile.data;

import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.brandwatch.glassmobile.utils.DateHelper;
import com.brandwatch.glassmobile.utils.HttpRequest;
import com.brandwatch.glassmobile.utils.PropertiesManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BrandwatchData {
	private static String TAG = "BrandwatchData";

	// Get the queryId for the requested query
	public static String getBrandwatchQueryId(String url, String query) {
		Log.i(TAG, String.format("Requesting queryId for query '%s'", query));
		String queryUrl = buildQueryUrl(url, query);
		String data = getData(queryUrl);
		JsonObject results = getResults(data);
		return results.get("id").toString();
	}

	// Get sentiment data for the passed in queryId
	public static String getSentimentData(String url, String queryId) {
		Log.i(TAG, String.format("Requesting sentiment data for queryId '%s'", queryId));
		String sentimentUrl = buildSentimentUrl(url, queryId);
		return getData(sentimentUrl);
	}

	// Get topics data for the passed in queryId
	public static String getTopicsData(String url, String queryId) {
		Log.i(TAG, String.format("Requesting topics data for queryId '%s'", queryId));
		String topicstUrl = buildTopicsUrl(url, queryId);
		return getData(topicstUrl);
	}

	// Make generic HTTP request to the passed in URL
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

	// Build up the Brandwatch queryId URL
	private static String buildQueryUrl(String url, String query) {
		String brandwatchKey = PropertiesManager.getProperty("brandwatch_auth");
		return url + "queries/?" + brandwatchKey + "&nameContains=" + query.replace(' ', '+');
	}

	// Build up the Brandwatch sentiment URL
	private static String buildSentimentUrl(String url, String queryId) {
		String sentimentUrl = "data/volume/months/sentiment/?";
		return url + sentimentUrl + buildFilters() + queryId;
	}

	// Build up the Brandwatch topics URL
	private static String buildTopicsUrl(String url, String queryId) {
		String topicsUrl = "data/volume/topics/queries/?";
		return url + topicsUrl + buildFilters() + queryId;
	}

	// Build the filters for Brandwatch queries to get a subset of the data
	// This subset requests a weeks worth of data
	private static String buildFilters() {
		String brandwatchKey = PropertiesManager.getProperty("brandwatch_auth");
		String startDate = DateHelper.getDateSevenDaysAgo();
		String endDate = DateHelper.getDateFormat(new Date());
		String filters = "&endDate=" + endDate + "&startDate=" + startDate + "&queryId=";

		return brandwatchKey + filters;
	}

	// Turn the JSON string response into a JSONObject
	private static JsonObject getResults(String responseString) {
		JsonElement jElement = new JsonParser().parse(responseString);
		JsonObject jObject = jElement.getAsJsonObject();
		JsonArray jArray = jObject.getAsJsonArray("results");
		jObject = jArray.get(0).getAsJsonObject();

		return jObject;
	}
}
