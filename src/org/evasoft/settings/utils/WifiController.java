package org.evasoft.settings.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

public class WifiController {

	private Context mContext;
	private  BroadcastReceiver mWifiReceiver;
	protected IWifiChangeListener wifiChangeListener;
	private IntentFilter mIntentFilter;

	public WifiController(Context context) {
		this.mContext = context;
		this.wifiChangeListener = null;
		this.mIntentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
		this.mIntentFilter.addAction("android.net.wifi.STATE_CHANGE");
		
		this.mWifiReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String str = intent.getAction();
				if (str.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
					WifiController.this.callOnWifiChangeListener();	
				}
			}
		};
	}

	public int getWifiState() {
		WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getWifiState();
	}
	
	public boolean isEnabled() {
		WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
		boolean state = false;
		switch (wifiManager.getWifiState()) {
		case WifiManager.WIFI_STATE_DISABLED:
		case WifiManager.WIFI_STATE_DISABLING:
			state = false;
			break;
		case WifiManager.WIFI_STATE_ENABLED:
		case WifiManager.WIFI_STATE_ENABLING:
			state = true;
			break;
		}
		return state;
	}

	public void toggleWifi() {
		WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
		if (getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			wifiManager.setWifiEnabled(true);
		} else if (getWifiState() != WifiManager.WIFI_STATE_DISABLED) {
			wifiManager.setWifiEnabled(false);
		}
	}

	public void onPause() {
		this.mContext.unregisterReceiver(this.mWifiReceiver);

	}

	public void onResume() {
		this.mContext.registerReceiver(this.mWifiReceiver, this.mIntentFilter);
	}

	protected void callOnWifiChangeListener() {
		if (this.wifiChangeListener == null)
			return;
		this.wifiChangeListener.onChange(this);
	}

	public void setWifiChangeListener(IWifiChangeListener changeListener) {
		this.wifiChangeListener = changeListener;
	}

	public static interface IWifiChangeListener {
		public abstract void onChange(WifiController wifiController);
	}

}
