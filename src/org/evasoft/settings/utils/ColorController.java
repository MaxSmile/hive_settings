package org.evasoft.settings.utils;

import org.evasoft.settings.R;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

public class ColorController {
	
	private Context mContext;
	
	public ColorController(Context context) {
		this.mContext = context;
	}
	
	public Drawable swapColor(int drawable, boolean needpaint) {
		Drawable localDrawable = this.mContext.getResources().getDrawable(drawable).mutate();
		PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
		if (needpaint) {
			localDrawable.setColorFilter(this.mContext.getResources().getColor(R.color.lightblue), mode);
		} else localDrawable.setColorFilter(this.mContext.getResources().getColor(R.color.white), mode);

		return localDrawable;
	}
	
	public Drawable swapColor(int drawable, int color) {
		Drawable localDrawable = this.mContext.getResources().getDrawable(drawable).mutate();
		PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
		localDrawable.setColorFilter(this.mContext.getResources().getColor(color), mode);

		return localDrawable;
	}
}
