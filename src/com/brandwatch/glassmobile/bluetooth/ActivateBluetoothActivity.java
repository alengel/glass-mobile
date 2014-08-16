package com.brandwatch.glassmobile.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.glass.brandwatch.R;

public class ActivateBluetoothActivity extends Activity {
	static final private String TAG = ActivateBluetoothActivity.class.getSimpleName();
	static final private int REQUEST_ENABLE_BT = 1;

	private BluetoothAdapter bluetoothAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initialiseBluetooth();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode != RESULT_OK) {
				Log.w(TAG, "Failed to enable bluetooth");
			} else {
				Log.w(TAG, "Bluetooth enabled");
				startBluetoothService();
			}
		}
	}

	private void initialiseBluetooth() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (bluetoothAdapter == null) {
			Log.w(TAG, "Bluetooth not supported on this device");
		} else {
			if (bluetoothAdapter.isEnabled() == false) {
				enableBluetooth();
			} else {
				startBluetoothService();
			}
		}
	}

	private void enableBluetooth() {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}

	private void startBluetoothService() {
		Intent intent = new Intent(this, BluetoothService.class);
		startService(intent);
	}
}
