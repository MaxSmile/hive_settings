package org.evasoft.settings.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class AirplaneController {
	
	private Context mContext;
	
	public AirplaneController(Context context) {
		this.mContext = context;
	}
	
	public boolean isEnabled() {
        ContentResolver cr = this.mContext.getContentResolver();
        if (Settings.System.getInt(cr, Settings.System.AIRPLANE_MODE_ON, 1) != 1) {
        	return false;
        } else {
        	return true;
        }
    }

	public void setAirplaneMode() {
        Settings.System.putInt(this.mContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, isEnabled() ? 0 : 1);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", isEnabled());
        this.mContext.sendBroadcast(intent);
    }

}
