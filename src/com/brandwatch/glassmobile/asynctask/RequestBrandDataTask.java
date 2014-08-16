package com.brandwatch.glassmobile.asynctask;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.brandwatch.glassmobile.data.BrandwatchData;
import com.brandwatch.glassmobile.data.Semantics3Data;

public class RequestBrandDataTask extends AsyncTask<String, Void, ArrayList<String>> {

	private static final String TAG = "RequestBrandDataTask";
	private String url;
	private String query;

	// private Context context;

	public RequestBrandDataTask() {
		// this.context = context.getApplicationContext();
	}

	// Called by execute() in previous class, extract parameters
	protected ArrayList<String> doInBackground(String... parameters) {
		ArrayList<String> results = new ArrayList<String>();
		url = parameters[0];
		query = parameters[1];

		// Get the query id
		String queryId = BrandwatchData.getBrandwatchQueryId(url, query);

		// Add the results of the requests as strings to the array
		results.add(Semantics3Data.getData(query));
		results.add(BrandwatchData.getSentimentData(url, queryId));
		results.add(BrandwatchData.getTopicsData(url, queryId));

		return results;
	}

	// Called automatically after each HTTP request
	protected void onPostExecute(ArrayList<String> data) {
		if (data != null) {
			Log.v(TAG, String.format("Request for query '%s' succedeed", query));
			showCardsActivity(data);
		} else {
			Log.v(TAG, String.format("Request for query '%s' failed", query));
		}
	}

	// Start the new activity and pass the data array as parameters
	private void showCardsActivity(ArrayList<String> data) {
		// Intent intent = new Intent(context, CardBundleActivity.class);
		// intent.putStringArrayListExtra("data", data);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(intent);
	}
}
