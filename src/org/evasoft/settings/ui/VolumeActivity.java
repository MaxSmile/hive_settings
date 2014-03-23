package org.evasoft.settings.ui;

import org.evasoft.settings.Param;
import org.evasoft.settings.R;
import org.evasoft.settings.SaveManager;
import org.evasoft.settings.constants.IPrefs;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;


public class VolumeActivity extends Activity implements SeekBar.OnSeekBarChangeListener, IPrefs {
	
	private SaveManager mSM;
	private AudioManager audioManager;
	
	private SeekBar seekRingerVolume;
	private SeekBar seekNotifyVolume;
	private SeekBar seekMediaVolume;
	private SeekBar seekAlarmVolume;
	private SeekBar seekVoiceVolume;
	private SeekBar seekSystemVolume;
	
	private Button btn_volume_ok;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.volume_activity);
		
		getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
		
		this.mSM = new SaveManager(this);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		this.seekRingerVolume = (SeekBar) findViewById(R.id.sb_1);
		this.seekRingerVolume.setId(1);
		this.seekRingerVolume.setMax(7);
		final int st_ring = audioManager.getStreamVolume(AudioManager.STREAM_RING);
		this.seekRingerVolume.setProgress(st_ring);
		this.mSM.saveInt(Param.VOLUME_RINGER, st_ring);
		this.seekRingerVolume.setOnSeekBarChangeListener(this);
		
		this.seekNotifyVolume = (SeekBar) findViewById(R.id.sb_2);
		this.seekNotifyVolume.setId(2);
		this.seekNotifyVolume.setMax(7);
		final int st_notif = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
		this.seekNotifyVolume.setProgress(st_notif);
		this.mSM.saveInt(Param.VOLUME_NOTIFY, st_notif);
		this.seekNotifyVolume.setOnSeekBarChangeListener(this);
		
		this.seekMediaVolume = (SeekBar) findViewById(R.id.sb_3);
		this.seekMediaVolume.setId(3);
		this.seekMediaVolume.setMax(15);
		final int st_music = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		this.seekMediaVolume.setProgress(st_music);
		this.mSM.saveInt(Param.VOLUME_MEDIA, st_music);
		this.seekMediaVolume.setOnSeekBarChangeListener(this);
		
		this.seekAlarmVolume = (SeekBar) findViewById(R.id.sb_4);
		this.seekAlarmVolume.setId(4);
		this.seekAlarmVolume.setMax(7);
		final int st_alarm = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
		this.seekAlarmVolume.setProgress(st_alarm);
		this.mSM.saveInt(Param.VOLUME_ALARM, st_alarm);
		this.seekAlarmVolume.setOnSeekBarChangeListener(this);
		
		this.seekVoiceVolume = (SeekBar) findViewById(R.id.sb_5);
		this.seekVoiceVolume.setId(5);
		this.seekVoiceVolume.setMax(5);
		final int st_call = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
		this.seekVoiceVolume.setProgress(st_call);
		this.mSM.saveInt(Param.VOLUME_CALL, st_call);
		this.seekVoiceVolume.setOnSeekBarChangeListener(this);
		
		this.seekSystemVolume = (SeekBar) findViewById(R.id.sb_6);
		this.seekSystemVolume.setId(6);
		this.seekSystemVolume.setMax(7);
		final int st_system = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		this.seekSystemVolume.setProgress(st_system);
		this.mSM.saveInt(Param.VOLUME_SYSTEM, st_system);
		this.seekSystemVolume.setOnSeekBarChangeListener(this);
		
		
		this.btn_volume_ok = (Button) findViewById(R.id.btn_volume_ok);
		this.btn_volume_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		seekBar.setProgress(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		switch (seekBar.getId()) {
		case 1:
			audioManager.setStreamVolume(AudioManager.STREAM_RING, seekBar.getProgress(), AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
			break;
		case 2:
			audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, seekBar.getProgress(), AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
			break;
		case 3:
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
			break;
		case 4:
			audioManager.setStreamVolume(AudioManager.STREAM_ALARM, seekBar.getProgress(), AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
			break;
		case 5:
			audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, seekBar.getProgress(), AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
			break;
		case 6:
			audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, seekBar.getProgress(), AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
			break;
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
       	if (keyCode == KeyEvent.KEYCODE_BACK) {
       		audioManager.setStreamVolume(AudioManager.STREAM_RING, this.mSM.loadInt(Param.VOLUME_RINGER), AudioManager.FLAG_ALLOW_RINGER_MODES);
			audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, this.mSM.loadInt(Param.VOLUME_NOTIFY), AudioManager.FLAG_ALLOW_RINGER_MODES);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, this.mSM.loadInt(Param.VOLUME_MEDIA), AudioManager.FLAG_ALLOW_RINGER_MODES);
			audioManager.setStreamVolume(AudioManager.STREAM_ALARM, this.mSM.loadInt(Param.VOLUME_ALARM), AudioManager.FLAG_ALLOW_RINGER_MODES);
			audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, this.mSM.loadInt(Param.VOLUME_CALL), AudioManager.FLAG_ALLOW_RINGER_MODES);
			audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, this.mSM.loadInt(Param.VOLUME_SYSTEM), AudioManager.FLAG_ALLOW_RINGER_MODES);
			finish();
       		return true;
       	} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
       		return true;
       	} else {
       		return super.onKeyDown(keyCode, event);
       	}
    }
}
