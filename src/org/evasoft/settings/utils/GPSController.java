package org.evasoft.settings.utils;

import org.evasoft.settings.constants.IPrefs;

import android.content.Context;
import android.location.LocationManager;

public class GPSController implements IPrefs {

	private Context mContext;

	final static String GPS_STRING = "gps";
	final static String NETWORK_STRING = "network";

	private LocationManager mLocationManager;

	public GPSController(Context context) {
		this.mContext = context;
		this.mLocationManager = (LocationManager) this.mContext
				.getSystemService("location");
	}

	public int getNetworks() throws IllegalArgumentException {
		if (this.mLocationManager.isProviderEnabled(GPS_STRING)	&& this.mLocationManager.isProviderEnabled(NETWORK_STRING)) {
			return GPS_AND_NETWORK;
		} else if (this.mLocationManager.isProviderEnabled(GPS_STRING)) {
			return GPS;
		} else if (this.mLocationManager.isProviderEnabled(NETWORK_STRING)) {
			return NETWORK;
		} else {
			return 0;
		}
	}

}
