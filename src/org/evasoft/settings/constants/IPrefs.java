package org.evasoft.settings.constants;

public interface IPrefs {
	
	public static final String tag = "SETTINGS_TEST";
	public static final String FLURRY_KEY = "2WRV8TDJ4Z2PXTP7V73G";
	public static final int NOTIFICATION_ID = 199;
	public static final String msg = "# ";
	public static final String SP_NAME = "SP";
	public static final String WIFISTATE = "wifistate";
	public static final String ROTATIONSTATE = "rotationstate";
	public static final String AIRPLANESTATE = "airplanestate";
	
	public static final int STATE_SOUND_ON = 0;
	public static final int STATE_SOUND_AND_VIBRO = 1;
	public static final int STATE_VIBRO = 2;
	public static final int STATE_SILENT = 3;
	
	public static final int BLUETOOTH_ON = 12;
	public static final int BLUETOOTH_OFF = 10;
	public static final int BLUETOOTH_TURNING_ON = 11;
	public static final int BLUETOOTH_TURNING_OFF = 13;
	public static final int GPS = 1;
	public static final int NETWORK = 2;
	public static final int GPS_AND_NETWORK = 3;
	public static final int BRIGHTNESS_LEVEL_MAX = 255;
	public static final int BRIGHTNESS_LEVEL_MIN = 30;
	
	public static final int NET_2G = 1;
	public static final int NET_3G = 2;
	public static final int NET_4G = 3;
	
	public static final boolean GOOGLE_PLAY = true;
	
	public static final String RATE_LINK = "market://details?id=org.evasoft.settings";
	public static final String TRANSLATE_PROJECT_LINK = "http://crowdin.net/project/hive-settings/";
}
