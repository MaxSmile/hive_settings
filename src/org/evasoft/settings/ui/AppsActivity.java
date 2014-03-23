package org.evasoft.settings.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.evasoft.settings.AppAdapter;
import org.evasoft.settings.AppItem;
import org.evasoft.settings.Param;
import org.evasoft.settings.R;
import org.evasoft.settings.SaveManager;
import org.evasoft.settings.constants.IPrefs;
import org.evasoft.settings.utils.NameComparator;
import org.evasoft.settings.utils.Notif;
import org.evasoft.settings.utils.SizeComparator;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class AppsActivity extends Activity implements IPrefs {
	
	public List<AppItem> installedApps;
	public AppAdapter adapter;
	private LinearLayout mProgressBar;
	
	private SaveManager mSM;
	
	private Handler mFinalizeHandler;
	
	private int mCall;
	private int mAppsCount;
	private boolean isCall;
	private boolean sort_by_size;
	public static Drawable sCurrentAppIcon;
	public static String sCurrentAppName;
	public static boolean cancelUpdate = false;

	private boolean tapTap = false;
	
	private boolean display_system_apps;
	
	private TextView mAppsCounter;
	private String mTextAppsCounter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apps_activity);
		
		this.mAppsCounter = (TextView) findViewById(R.id.apps_counter);
		this.mTextAppsCounter = getString(R.string.s_apps_count);
		
		this.mProgressBar = (LinearLayout) findViewById(R.id.progress_bar);
		this.mProgressBar.setVisibility(View.VISIBLE);
		
		this.mSM = new SaveManager(this);
		
		getSort();
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		this.display_system_apps = settings.getBoolean("pref_display_system_apps", false);
		
		this.mFinalizeHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mAppsCounter.setText(mTextAppsCounter + " " + mAppsCount);
				
				ListView list = (ListView) findViewById(R.id.app_list_left);
				
				if (sort_by_size) {
					try {
						Collections.sort(installedApps, new SizeComparator());						
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Collections.sort(installedApps, new NameComparator());						
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
				
				adapter = new AppAdapter(AppsActivity.this, installedApps);
				
				list.setAdapter(adapter);
				
				AppsActivity.this.mProgressBar.setVisibility(View.GONE);
				
				tapTap = false;
				
				list.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
						Intent intent = new Intent(AppsActivity.this, AppActionActivity.class);
						AppsActivity.sCurrentAppName = installedApps.get(position).name;
						AppsActivity.sCurrentAppIcon = installedApps.get(position).icon;
						
						startActivityForResult(intent, position);
					}
				});
			};
		};
	}
	
	private class AsyncManager extends AsyncTask<Void, String, Boolean> {

		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			mCall = 0;
			mAppsCount = 0;
			isCall = false;
			installedApps = new ArrayList<AppItem>();       
		    List<PackageInfo> packs = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		    for(int i = 0; i < packs.size(); i++) {
		        PackageInfo p = packs.get(i);
		        if (!display_system_apps && (p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 && (p.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
		       		// system
		       	} else {
		       		// user
			       	final AppItem newInfo = new AppItem();
			        newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
			        newInfo.name = p.applicationInfo.loadLabel(getPackageManager()).toString();
			        newInfo.package_name = p.packageName;
			        newInfo.version_name = p.versionName;
			        newInfo.path = p.applicationInfo.sourceDir;
			        
			        try {
						PackageManager pm = getPackageManager();
						Method getPackageSizeInfo;
						getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
						getPackageSizeInfo.invoke(pm, newInfo.package_name, new IPackageStatsObserver.Stub() {
							@Override
							public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
								newInfo.lsize = pStats.codeSize + pStats.dataSize + pStats.cacheSize;
								// KB
								float dsize = newInfo.lsize / 1024f;
								newInfo.size_flag = "KB";
								// MB
						        if (dsize >= 1024) {
						        	dsize = dsize / 1024f;
						        	newInfo.size_flag = "MB";
						        	// GB
						        	if (dsize >= 1024) {
						        		dsize = dsize / 1024f;
							        	newInfo.size_flag = "GB";
						        	}
						        }
						        
						        if (newInfo.size_flag.equals("KB")) {
						        	newInfo.size = String.format("%.0f", dsize) + newInfo.size_flag;
						        } else {
						        	newInfo.size = String.format("%.2f", dsize) + newInfo.size_flag;
						        }
						        mCall++;
						        if (mAppsCount == mCall && !isCall) {
						        	isCall = true;
						        	mFinalizeHandler.sendEmptyMessage(0);
								}
							}
						});
					} catch (SecurityException e) {
						mCall++;
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						mCall++;
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						mCall++;
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						mCall++;
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						mCall++;
						e.printStackTrace();
					}
					finally {
						installedApps.add(newInfo);						
					}
	        	}
		    }
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			mAppsCount = installedApps.size();
			if (mAppsCount == mCall && !isCall) {
				isCall = true;
				mFinalizeHandler.sendEmptyMessage(0);
			}
		}
	}
	
	private void exportApk(String path, String name, Drawable icon) {
		final String externalStoragePath = Environment.getExternalStorageDirectory().toString();
		try
	    {
			File importApk = new File(path);
			String exportTo = String.valueOf(externalStoragePath) + "/" + name + ".apk";
			File exportApk = new File(exportTo);
			FileInputStream inputStream = new FileInputStream(importApk);
			FileOutputStream outputStream = new FileOutputStream(exportApk);
			byte[] arrayOfByte = new byte[1024];
			while (true)
			{
				int i = inputStream.read(arrayOfByte);
				if (i <= 0)
				{
					inputStream.close();
					outputStream.close();
					Notif.showToast(this, icon, name + " " + getString(R.string.s_export_success));
					return;
				}
				outputStream.write(arrayOfByte, 0, i);
			}
	    }
		catch (FileNotFoundException localFileNotFoundException)
		{
			Notif.showToast(this, icon, getString(R.string.s_export_access_denied));
			return;
		}
		catch (IOException localIOException)
		{
			Notif.showToast(this, icon, getString(R.string.s_export_io_error));
		}
	}
	
	private void getSort() {
		if (this.mSM == null) {
			this.mSM = new SaveManager(this);
		}
		
		final int by_sort = this.mSM.loadInt(Param.SORT);
		if (by_sort == Param.DEFAULT_VALUE || by_sort == Param.SORT_BY_SIZE) {
			this.mSM.saveInt(Param.SORT, Param.SORT_BY_SIZE);
			sort_by_size = true;
		} else if (by_sort == Param.SORT_BY_NAME) {
			this.mSM.saveInt(Param.SORT, Param.SORT_BY_NAME);
			sort_by_size = false;
		}
	}
	
	private void swapSortMethod() {
		final int by_sort = this.mSM.loadInt(Param.SORT);
		if (by_sort == Param.SORT_BY_SIZE) {
			this.mSM.saveInt(Param.SORT, Param.SORT_BY_NAME);
			sort_by_size = false;
			Toast.makeText(this, getString(R.string.s_sort_by_name), Toast.LENGTH_SHORT).show();
		} else if (by_sort == Param.SORT_BY_NAME) {
			this.mSM.saveInt(Param.SORT, Param.SORT_BY_SIZE);
			sort_by_size = true;
			Toast.makeText(this, getString(R.string.s_sort_by_size), Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			cancelUpdate = true;
			return;
		}
		
		int action = data.getIntExtra(Param.EXT_PACKAGE_NAME, 0);
		
	    switch (action) {
		case 1:
			try {
				startActivity(new Intent(getPackageManager().getLaunchIntentForPackage(installedApps.get(requestCode).package_name)));
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				Notif.showToast(this, installedApps.get(requestCode).icon, getString(R.string.s_open_error));
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + installedApps.get(requestCode).package_name)));							
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case 3:
			exportApk(installedApps.get(requestCode).path, installedApps.get(requestCode).name, installedApps.get(requestCode).icon);
			break;
		case 4:
			try {
				Uri packageURI = Uri.parse("package:" + installedApps.get(requestCode).package_name);
		        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		        startActivity(uninstallIntent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	  }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
       	if (keyCode == KeyEvent.KEYCODE_MENU && !tapTap) {
       		this.tapTap = true;
       		swapSortMethod();
       		new AsyncManager().execute();
       		return true;
       	} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
       		return true;
       	} else {
       		return super.onKeyDown(keyCode, event);
       	}
    }
	
	@Override
	protected void onResume() {
		if (!cancelUpdate) {
			this.mProgressBar.setVisibility(View.VISIBLE);
			new AsyncManager().execute();
		} else {
			cancelUpdate = false;
		}
		super.onResume();
	}
}
