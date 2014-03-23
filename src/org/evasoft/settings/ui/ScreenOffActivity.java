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

public class ScreenOffActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screenoff_activity);
		
		LinearLayout l1 = (LinearLayout) findViewById(R.id.l1);
		l1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(0);
			}
		});
		LinearLayout l2 = (LinearLayout) findViewById(R.id.l2);
		l2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(1);
			}
		});
		LinearLayout l3 = (LinearLayout) findViewById(R.id.l3);
		l3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(2);
			}
		});
		LinearLayout l4 = (LinearLayout) findViewById(R.id.l4);
		l4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(3);
			}
		});
		LinearLayout l5 = (LinearLayout) findViewById(R.id.l5);
		l5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(4);
			}
		});
		LinearLayout l6 = (LinearLayout) findViewById(R.id.l6);
		l6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(5);
			}
		});
		LinearLayout l7 = (LinearLayout) findViewById(R.id.l7);
		l7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(6);
			}
		});
		LinearLayout l8 = (LinearLayout) findViewById(R.id.l8);
		l8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onParam(7);
			}
		});
		
		final int key = getIntent().getExtras().getInt(Param.SCREEN_OFF_ID_NAME, 0);
		ImageView img;
		switch (key) {
		case 0:
			img = (ImageView) findViewById(R.id.iv1);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		case 1:
			img = (ImageView) findViewById(R.id.iv2);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		case 2:
			img = (ImageView) findViewById(R.id.iv3);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		case 3:
			img = (ImageView) findViewById(R.id.iv4);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		case 4:
			img = (ImageView) findViewById(R.id.iv5);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		case 5:
			img = (ImageView) findViewById(R.id.iv6);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		case 6:
			img = (ImageView) findViewById(R.id.iv7);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		case 7:
			img = (ImageView) findViewById(R.id.iv8);
			img.setBackgroundResource(R.drawable.check_box);
			break;
		default:
			break;
		}
	}
	
	private void onParam(final int item) {
		Intent intent = new Intent();
	    intent.putExtra(Param.SCREEN_OFF_ID_NAME, item);
	    setResult(RESULT_OK, intent);
	    finish();
	}
}
