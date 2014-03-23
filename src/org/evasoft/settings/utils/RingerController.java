package org.evasoft.settings.utils;

import org.evasoft.settings.R;
import org.evasoft.settings.constants.IPrefs;

import android.content.Context;
import android.media.AudioManager;

public class RingerController implements IPrefs {
	
	private Context mContext;
	
	AudioManager mAudioManager;
	
	public RingerController(Context context) {
		this.mContext = context;
		this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void setSoundState(final int state) {
		switch (state) {
		case STATE_SOUND_ON:
			this.mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			this.mAudioManager.setVibrateSetting(0, 0);
			break;
		case STATE_SOUND_AND_VIBRO:
			this.mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			this.mAudioManager.setVibrateSetting(0, 1);
			break;
		case STATE_VIBRO:
			this.mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		break;
		case STATE_SILENT:
			this.mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		break;
		}
	}
	
	public int getSoundState() {
		int soundMode = this.mAudioManager.getRingerMode();
		final int vibrateMode = this.mAudioManager.getVibrateSetting(0);
		
		switch (soundMode) {
		case AudioManager.RINGER_MODE_NORMAL:
			soundMode = STATE_SOUND_ON;
			if (vibrateMode == 1) {
				soundMode = STATE_SOUND_AND_VIBRO;
			}
			break;
		case AudioManager.RINGER_MODE_VIBRATE:
			soundMode = STATE_VIBRO;
			break;
		case AudioManager.RINGER_MODE_SILENT:
			soundMode = STATE_SILENT;
			break;

		}
		
		return soundMode;
	}
	
	public String getRingerName() {
		String name = "";
		switch (getSoundState()) {
		case STATE_SOUND_ON:
			name = this.mContext.getString(R.string.s_ringer_onlysound);
			break;
		case STATE_SOUND_AND_VIBRO:
			name = this.mContext.getString(R.string.s_ringer_soundvibra);
			break;
		case STATE_VIBRO:
			name = this.mContext.getString(R.string.s_ringer_vibra);
			break;
		case STATE_SILENT:
			name = this.mContext.getString(R.string.s_ringer_silent);
			break;
		}
		return name;
	}
}
