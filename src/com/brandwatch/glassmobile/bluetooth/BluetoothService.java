package com.brandwatch.glassmobile.bluetooth;

import java.io.IOException;
import java.util.UUID;

import com.glass.brandwatch.utils.PropertiesManager;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BluetoothService extends Service implements
		BluetoothConnectionTask.BluetoothConnectionTaskCallbacks,
		IncomingRequestBluetoothTask.BluetoothSocketHandlerCallbacks {

	public static final String TAG = BluetoothService.class.getSimpleName();
	
	public static final String NAME = "BRANDWATCH_GLASSWARE";

	private BluetoothAdapter bluetoothAdapter;
	private BluetoothServerSocket serverSocket;
	private BluetoothConnectionTask bluetoothConnectionTask;
	private IncomingRequestBluetoothTask handleBrandwatchDataRequestTask;
	
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			
			// React to state changes of the bluetooth adapter
			if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
						BluetoothAdapter.ERROR);
				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					if (bluetoothConnectionTask != null) {
						bluetoothConnectionTask.cancel(true);
					}
					if (handleBrandwatchDataRequestTask != null) {
						handleBrandwatchDataRequestTask.cancel(true);
					}
					break;
				case BluetoothAdapter.STATE_ON:
					initialiseBluetooth();
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					break;
				}
			}
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Starting");
		initialiseBluetooth();

		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver, filter);

		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "Stopping");

		unregisterReceiver(receiver);

		if (bluetoothConnectionTask != null) {
			bluetoothConnectionTask.cancel(true);
		}
		if (handleBrandwatchDataRequestTask != null) {
			handleBrandwatchDataRequestTask.cancel(true);
		}
	}

	public void initialiseBluetooth() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter != null) {
			if (bluetoothAdapter.isEnabled()) {
				// Create a bluetooth server socket
				try {
					UUID uuid = UUID.fromString(PropertiesManager.getProperty("bluetooth_uuid"));
					serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, uuid);
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
				
				// Wait Glass to connect to the socket
				bluetoothConnectionTask = new BluetoothConnectionTask(serverSocket, this);
				bluetoothConnectionTask.execute();
			}
		} else {
			stopSelf();
		}
	}

	@Override
	public void onConnectionSuccessful(BluetoothSocket socket) {
		handleBrandwatchDataRequestTask = new IncomingRequestBluetoothTask(socket, this);
		handleBrandwatchDataRequestTask.execute();
	}

	@Override
	public void onConnectionCancel() {
		bluetoothConnectionTask = new BluetoothConnectionTask(serverSocket, this);
		bluetoothConnectionTask.execute();
	}

	@Override
	public void onTransferComplete(String result) {
		bluetoothConnectionTask = new BluetoothConnectionTask(serverSocket, this);
		bluetoothConnectionTask.execute();
	}
}
