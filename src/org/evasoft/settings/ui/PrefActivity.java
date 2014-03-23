package org.evasoft.settings.ui;

import org.evasoft.settings.R;
import org.evasoft.settings.constants.IPrefs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;


public class PrefActivity extends PreferenceActivity implements IPrefs {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Preference pref = (Preference) findPreference("pref_rate_app");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(RATE_LINK));
				startActivity(intent);
				return true;
			}
		});
		
		pref = (Preference) findPreference("pref_translation_help");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(TRANSLATE_PROJECT_LINK));
				startActivity(intent);
				return true;
			}
		});
	}	
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
       	if (keyCode == KeyEvent.KEYCODE_SEARCH) {
       		return true;
       	} else {
       		return super.onKeyDown(keyCode, event);
       	}
    }
	// ===========================================================
	// Methods
	// ===========================================================
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
