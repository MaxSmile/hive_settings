/* $Author: alexander@bsrgin.ru $
 * $Id: AlarmApplication.java 8 2011-11-13 20:11:06Z alexander@bsrgin.ru $
 * 
 * Описание класса приложения
 */
package org.evasoft.settings;

import java.util.Locale;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.preference.PreferenceManager;


public class SettingsApplication extends Application
{
	private static SettingsApplication singleton;
	
	private SharedPreferences mPreferences;
	private Locale mLocale;
    private String mLang;
	
	@Override
	public void onCreate() {
        super.onCreate();
		singleton = this;
		
		try {
			mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			mLang = mPreferences.getString("lang", "default");
			if (mLang.equals("default")) {
	        	mLang = getResources().getConfiguration().locale.getCountry();
	        }
	        mLocale = new Locale(mLang);
	        Locale.setDefault(mLocale);
	        Configuration config = new Configuration();
	        config.locale = mLocale;
	        getBaseContext().getResources().updateConfiguration(config, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {
    	super.onConfigurationChanged(newConfig);
        mLocale = new Locale(mLang);
        Locale.setDefault(mLocale);
        Configuration config = new Configuration();
        config.locale = mLocale;
        getBaseContext().getResources().updateConfiguration(config, null);
    }
	
	public static SettingsApplication getInstance()
	{
		return singleton;
	}
	
	public static String getApplicationVersion()
	{
		try {
			return getInstance().getApplicationContext().getPackageManager().getPackageInfo("org.evasoft.settings", 0).versionName;
		} catch (NameNotFoundException e) {
			return "App not installed!";
		}
	}
}
