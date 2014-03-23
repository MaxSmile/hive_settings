package org.evasoft.settings.ui;

import org.evasoft.settings.Param;
import org.evasoft.settings.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class RingerModeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ringermode_activity);
		
		LinearLayout l1 = (LinearLayout) findViewById(R.id.rm_l1);
		l1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(0);
			}
		});
		LinearLayout l2 = (LinearLayout) findViewById(R.id.rm_l2);
		l2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(1);
			}
		});
		LinearLayout l3 = (LinearLayout) findViewById(R.id.rm_l3);
		l3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(2);
			}
		});
		LinearLayout l4 = (LinearLayout) findViewById(R.id.rm_l4);
		l4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(3);
			}
		});
		
		final int key = getIntent().getExtras().getInt(Param.RINGER_MODE_ID_NAME, 0);
		ImageView img;
		switch (key) {
		case 0:
			img = (ImageView) findViewById(R.id.rm_iv1);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		case 1:
			img = (ImageView) findViewById(R.id.rm_iv2);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		case 2:
			img = (ImageView) findViewById(R.id.rm_iv3);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		case 3:
			img = (ImageView) findViewById(R.id.rm_iv4);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		default:
			break;
		}
	}
	
	private void onParam(final int item) {
		Intent intent = new Intent();
	    intent.putExtra(Param.RINGER_MODE_ID_NAME, item);
	    setResult(RESULT_OK, intent);
	    finish();
	}
}
