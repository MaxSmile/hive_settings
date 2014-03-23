package org.evasoft.settings;

import java.io.File;

import android.graphics.drawable.Drawable;

public class AppItem {
	
	public File file = null;
	public Drawable icon = null;
	public long lsize = 65535L;
	public String size = "";
	public String size_flag = ""; // KB, MB, GB, TB
	public String name = "";
	public String path = "";
	public String package_name = "";
	public String version_name = "";
}
