package org.evasoft.settings.ui;

import org.evasoft.settings.Param;
import org.evasoft.settings.R;
import org.evasoft.settings.SaveManager;
import org.evasoft.settings.constants.IPrefs;
import org.evasoft.settings.utils.Notif;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class BrightnessActivity extends Activity implements SeekBar.OnSeekBarChangeListener, IPrefs {
	
	private TextView mCurrentLevel;
	/** 0 - 255 */
	private int mCurrentBrightnessLevel;
	private SeekBar mBar;
	private SaveManager mSM;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brightness_activity);
		
		getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
		
		this.mSM = new SaveManager(this);
		
		try {
			this.mCurrentBrightnessLevel = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		} 
		// min
		TextView btn_low = (TextView) findViewById(R.id.btn_bright_low);
		btn_low.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setBrightness(0);
				android.provider.Settings.System.putInt(BrightnessActivity.this.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, BRIGHTNESS_LEVEL_MIN);
				finish();
			}
		});
		// custom
		final TextView btn_custom = (TextView) findViewById(R.id.btn_bright_custom);
		btn_custom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int level = mSM.loadInt(Param.BRIGHTNESS_CUSTOM);
				if (level != -1) {
					setBrightness(level);
					android.provider.Settings.System.putInt(BrightnessActivity.this.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, (int) converPercentToLevel(level));
					finish();
				} else {
					Notif.showToast(BrightnessActivity.this, getResources().getDrawable(R.drawable.icon_brightness_custom), getString(R.string.s_hold_me), Toast.LENGTH_SHORT);
				}
			}
		});
		// save custom
		btn_custom.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				final int progress = BrightnessActivity.this.mBar.getProgress();
				mSM.saveInt(Param.BRIGHTNESS_CUSTOM, progress);
				btn_custom.setText(getString(R.string.s_btn_brightness_custom) + " " + progress + "%");
				return true;
			}
		});
		// max
		TextView btn_high = (TextView) findViewById(R.id.btn_bright_high);
		btn_high.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setBrightness(100);
				android.provider.Settings.System.putInt(BrightnessActivity.this.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, BRIGHTNESS_LEVEL_MAX);
				finish();
			}
		});

		Button btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		this.mBar = (SeekBar) findViewById(R.id.seekBar1);
		this.mBar.setOnSeekBarChangeListener(this);
		
		this.mCurrentLevel = (TextView) findViewById(R.id.brightness_current_lvl);
		final int progress = Math.round(converLevelToPercent(this.mCurrentBrightnessLevel));
		this.mCurrentLevel.setText(getString(R.string.s_brightness_current_lvl) + ": " + progress + "%");
		this.mBar.setProgress(progress);

		final int custom = this.mSM.loadInt(Param.BRIGHTNESS_CUSTOM);
		if (custom != -1) {
			btn_custom.setText(getString(R.string.s_btn_brightness_custom) + " " + custom + "%");
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		this.mCurrentLevel.setText(getString(R.string.s_brightness_current_lvl) + ": " +  progress + "%");
		setBrightness(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		android.provider.Settings.System.putInt(this.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, (int) converPercentToLevel(seekBar.getProgress()));
	}
	
	/** set brightness 0 - 100 */
	private void setBrightness(int brightnessLevel) {
	    WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
	    localLayoutParams.screenBrightness = converPercentToLevel(brightnessLevel) / (float) BRIGHTNESS_LEVEL_MAX;
	    getWindow().setAttributes(localLayoutParams);
	}
	
	/** @return value 30 - 255 */
	public static float converPercentToLevel(int value) {
		return ((BRIGHTNESS_LEVEL_MAX - BRIGHTNESS_LEVEL_MIN) / 100f * value + BRIGHTNESS_LEVEL_MIN);
	}
	
	/** @return value 0 - 100 */
	public static float converLevelToPercent(int level) {
		return (level - BRIGHTNESS_LEVEL_MIN) / ((BRIGHTNESS_LEVEL_MAX - BRIGHTNESS_LEVEL_MIN) / 100f);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
       	if (keyCode == KeyEvent.KEYCODE_SEARCH) {
       		return true;
       	} else {
       		return super.onKeyDown(keyCode, event);
       	}
    }
}
