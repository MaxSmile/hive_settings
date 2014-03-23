package org.evasoft.settings.utils;

import android.app.Activity;
import android.provider.Settings;

public class RotationController {
	
	public static boolean getOrientationState(Activity activity) {
		String s = Settings.System.getString(activity.getContentResolver(), "accelerometer_rotation");
		if (!s.equals("1")) {
			return false;
		} else return true;
			
	}
	
	public static void lockOrientation(Activity activity) {
		if (!getOrientationState(activity)) {
			Settings.System.putString(activity.getContentResolver(), "accelerometer_rotation", "1");
		} else Settings.System.putString(activity.getContentResolver(), "accelerometer_rotation", "0");
	}
}
