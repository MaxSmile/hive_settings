package org.evasoft.settings.utils;

import org.evasoft.settings.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Notif {
	
	public static void showToast(Activity context, Drawable icon, String pText) {
		try {
			LayoutInflater inflater = context.getLayoutInflater();
			View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) context.findViewById(R.id.toast_layout_root));

			ImageView image = (ImageView) layout.findViewById(R.id.toast_app_icon);
			image.setImageDrawable(icon);
			
			TextView text = (TextView) layout.findViewById(R.id.toast_text);
			text.setText(pText);
			
			Toast toast = new Toast(context.getApplicationContext());
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			toast.show();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public static void showToast(Activity context, Drawable icon, String pText, int toastTime) {
		try {
			LayoutInflater inflater = context.getLayoutInflater();
			View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) context.findViewById(R.id.toast_layout_root));

			ImageView image = (ImageView) layout.findViewById(R.id.toast_app_icon);
			image.setImageDrawable(icon);
			
			TextView text = (TextView) layout.findViewById(R.id.toast_text);
			text.setText(pText);
			
			Toast toast = new Toast(context.getApplicationContext());
			if (toastTime == Toast.LENGTH_LONG) {
				toast.setDuration(Toast.LENGTH_LONG);	
			} else {
				toast.setDuration(Toast.LENGTH_SHORT);
			}
			toast.setView(layout);
			toast.show();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}
