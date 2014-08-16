package com.brandwatch.glassmobile.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.SerializationUtils;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.brandwatch.glassmobile.data.BrandwatchData;
import com.brandwatch.glassmobile.data.Semantics3Data;
import com.brandwatch.glassmobile.utils.PropertiesManager;
import com.brandwatch.glassmobile.utils.StreamUtils;

public class HandleBrandwatchDataRequestBluetoothTask extends AsyncTask<Void, Void, String> {
	private static final String TAG = HandleBrandwatchDataRequestBluetoothTask.class.getSimpleName();
	private BluetoothSocket socket;
	private BluetoothSocketHandlerCallbacks callbacks;

	public HandleBrandwatchDataRequestBluetoothTask(BluetoothSocket socket,
			BluetoothSocketHandlerCallbacks callbacks) {
		this.socket = socket;
		this.callbacks = callbacks;
	}

	@Override
	protected String doInBackground(Void... voids) {
		OutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();

			ArrayList<String> resultsArray = getBrandData(StreamUtils.readFromSocket(inputStream));
			
			// Serialise and send data to Glass
			byte[] results = SerializationUtils.serialize(resultsArray);
			StreamUtils.writeToSocket(outputStream, results);
			
			// Wait for glass to acknowledge receiving the data
			if(StreamUtils.readFromSocket(inputStream) != "completed"){
				Log.w(TAG, "Connection with Glass didn't terminate correctly.");
			}
			
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		// clean up socket
		finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}

			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}

		return "";
	}

	@Override
	protected void onPostExecute(String result) {
		callbacks.onTransferComplete(result);
	}

	public interface BluetoothSocketHandlerCallbacks {
		public void onTransferComplete(String result);
	}

	private ArrayList<String> getBrandData(String query) {
		Log.i(TAG, String.format("Received %s", query));

		ArrayList<String> results = new ArrayList<String>();
		String url = PropertiesManager.getProperty("brandwatch_url");

		// Get the query id
		String queryId = BrandwatchData.getBrandwatchQueryId(url, query);

		// Add the results of the requests as strings to the array
		results.add(Semantics3Data.getData(query));
		results.add(BrandwatchData.getSentimentData(url, queryId));
		results.add(BrandwatchData.getTopicsData(url, queryId));

		return results;
	}
}
