package com.brandwatch.glassmobile.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

public class BluetoothConnectionTask extends AsyncTask<Void, Void, BluetoothSocket> {
	private static final String TAG = BluetoothConnectionTask.class.getSimpleName();

	private BluetoothServerSocket serverSocket;
	private BluetoothConnectionTaskCallbacks callbacks;

	public BluetoothConnectionTask(BluetoothServerSocket serverSocket,
			BluetoothConnectionTaskCallbacks callbacks) {
		this.serverSocket = serverSocket;
		this.callbacks = callbacks;
	}

	@Override
	protected BluetoothSocket doInBackground(Void... voids) {
		BluetoothSocket socket = null;

		try {
			// Block until the connection is accepted or fails.
			socket = serverSocket.accept();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return socket;
	}

	@Override
	protected void onPostExecute(BluetoothSocket socket) {
		if (socket != null && callbacks != null) {
			Log.i(TAG, "Connection succeeded");
			callbacks.onConnectionSuccessful(socket);
		} else {
			Log.i(TAG, "Connection failed");
		}
	}

	@Override
	protected void onCancelled(BluetoothSocket socket) {
		if (callbacks != null) {
			callbacks.onConnectionCancel();
		}
	}

	public interface BluetoothConnectionTaskCallbacks {
		public void onConnectionSuccessful(BluetoothSocket socket);

		public void onConnectionCancel();
	}
}
