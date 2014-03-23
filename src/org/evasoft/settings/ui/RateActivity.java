package org.evasoft.settings.ui;

import org.evasoft.settings.Param;
import org.evasoft.settings.R;
import org.evasoft.settings.SaveManager;
import org.evasoft.settings.constants.IPrefs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RateActivity extends Activity implements IPrefs {
	
	private SaveManager sm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate_activity);
		
		this.sm = new SaveManager(this);
		
		Button no_thanks = (Button) findViewById(R.id.btn_no_thanks);
		no_thanks.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sm.loadInt(Param.SHOW_RATE_AGAIN) < 0) {
					sm.saveInt(Param.SHOW_RATE_AGAIN, 1);
					sm.saveInt(Param.LAUNCH_COUNTER, 0);
				} else {
					if (sm.loadInt(Param.SHOW_RATE_AGAIN) == 1) {
						sm.saveInt(Param.SHOW_RATE_AGAIN, 2);	
					}
				}
				finish();
			}
		});
		Button rate_it = (Button) findViewById(R.id.btn_rate_it_ok);
		rate_it.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sm.saveInt(Param.SHOW_RATE_AGAIN, 2);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(RATE_LINK));
				startActivity(intent);
				finish();
			}
		});
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
       		return true;
       	} else if (keyCode == KeyEvent.KEYCODE_BACK) {
       		return true;
       	} else {
       		return super.onKeyDown(keyCode, event);
       	}
    }
}
