package org.evasoft.settings.ui;

import org.evasoft.settings.Param;
import org.evasoft.settings.R;
import org.evasoft.settings.constants.IPrefs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class AppActionActivity extends Activity implements IPrefs {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apps_action);
		
		TextView app_name = (TextView) findViewById(R.id.app_name);
		app_name.setText(AppsActivity.sCurrentAppName);
		
		ImageView app_icon = (ImageView) findViewById(R.id.app_icon);
		app_icon.setImageDrawable(AppsActivity.sCurrentAppIcon);
		
		getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
		
		LinearLayout btn_open_app = (LinearLayout) findViewById(R.id.btn_open_app);
		btn_open_app.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppsActivity.cancelUpdate = true;
				Intent intent = new Intent();
			    intent.putExtra(Param.EXT_PACKAGE_NAME, Param.EXT_PACKAGE_OPEN);
			    setResult(RESULT_OK, intent);
			    finish();
			}
		});
		
		LinearLayout btn_info_app = (LinearLayout) findViewById(R.id.toast_layout_root);
		btn_info_app.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppsActivity.cancelUpdate = true;
				Intent intent = new Intent();
			    intent.putExtra(Param.EXT_PACKAGE_NAME, Param.EXT_PACKAGE_INFO);
			    setResult(RESULT_OK, intent);
			    finish();
			}
		});
		
		LinearLayout btn_export_app = (LinearLayout) findViewById(R.id.btn_export_app);
		btn_export_app.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppsActivity.cancelUpdate = true;
				Intent intent = new Intent();
			    intent.putExtra(Param.EXT_PACKAGE_NAME, Param.EXT_PACKAGE_EXPORT);
			    setResult(RESULT_OK, intent);
			    finish();
			}
		});
		
		LinearLayout btn_uninstall_app = (LinearLayout) findViewById(R.id.btn_uninstall_app);
		btn_uninstall_app.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppsActivity.cancelUpdate = true;
				Intent intent = new Intent();
			    intent.putExtra(Param.EXT_PACKAGE_NAME, Param.EXT_PACKAGE_UNINSTALL);
			    setResult(RESULT_OK, intent);
			    finish();
			}
		});
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
