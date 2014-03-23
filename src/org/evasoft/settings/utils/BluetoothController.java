package org.evasoft.settings.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothController {

	private Context mContext;
	private BluetoothAdapter mBluetoothAdapter;
	private BroadcastReceiver mBluetoothReceiver;
	protected IBluetoothChangeListener bluetoothChangeListener;
	private IntentFilter mIntentFilter;
	private int bluetooth_state = 0;
	private boolean bluetooth_exists;
	
	public BluetoothController(Context context) {
		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (this.mBluetoothAdapter == null) {
			bluetooth_exists = false;
		    return;
		}
		
		bluetooth_exists = true;
		this.mContext = context;
		this.bluetoothChangeListener = null;
		this.mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");

		this.mBluetoothReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				BluetoothController.this.bluetooth_state = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 0);
				BluetoothController.this.callOnBluetoothChangeListener(bluetooth_state);
			}
		};
	}
	
	/** bluetooth exists */
	public boolean bluetoothExists() {
		return bluetooth_exists;
	}
	
	public int getBluetoothState() {
		return this.bluetooth_state;
	}
	
	public void toggleBluetooth() {
		if (!this.mBluetoothAdapter.isEnabled()) {
			this.mBluetoothAdapter.enable();
		} else this.mBluetoothAdapter.disable(); 
	}
	
	public void setEnabledBluetooth(final boolean state) {
		if (state) {
			this.mBluetoothAdapter.enable();
		} else this.mBluetoothAdapter.disable(); 
	}
	
	public boolean isEnabled() {
		return this.mBluetoothAdapter.isEnabled();
	}
	
	public void onPause() {
		this.mContext.unregisterReceiver(this.mBluetoothReceiver);
	}

	public void onResume() {
		if (this.mBluetoothReceiver != null && this.mIntentFilter != null)
		this.mContext.registerReceiver(this.mBluetoothReceiver, this.mIntentFilter);
	}

	protected void callOnBluetoothChangeListener(final int state) {
		if (this.bluetoothChangeListener == null)
			return;
		this.bluetoothChangeListener.onChange(this, state);
	}

	public void setBluetoothChangeListener(IBluetoothChangeListener changeListener) {
		this.bluetoothChangeListener = changeListener;
	}

	public static abstract interface IBluetoothChangeListener {
		public abstract void onChange(BluetoothController bluetoothController, final int state);
	}
}
