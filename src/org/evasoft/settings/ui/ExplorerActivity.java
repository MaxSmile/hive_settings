package org.evasoft.settings.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.evasoft.settings.AppItem;
import org.evasoft.settings.R;
import org.evasoft.settings.constants.IPrefs;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class ExplorerActivity extends ListActivity implements IPrefs {
	
	/**
	 * The file path
	 */
	public final static String EXTRA_FILE_PATH = "file_path";
	
	/**
	 * Sets whether hidden files should be visible in the list or not
	 */
	public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";

	/**
	 * The allowed file extensions in an ArrayList of Strings
	 */
	public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";
	
	/**
	 * The initial directory which will be used if no directory has been sent with the intent 
	 */
	private static String DEFAULT_INITIAL_DIRECTORY;
	
	private static final String APK_FILE_TYPE = ".apk";
	
	protected File mDirectory;
	protected ArrayList<File> mFiles;
	protected FilePickerListAdapter mAdapter;
	protected boolean mShowHiddenFiles = false;
	protected String[] acceptedFileExtensions;
	private HashMap<String, AppItem> mItemsInfo;
	private int mLastScrollPosition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DEFAULT_INITIAL_DIRECTORY = Environment.getExternalStorageDirectory().toString();
		setTitle(DEFAULT_INITIAL_DIRECTORY);
		
		LayoutInflater inflator = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View emptyView = inflator.inflate(R.layout.empty_file, null);
		((ViewGroup)getListView().getParent()).addView(emptyView);
		getListView().setEmptyView(emptyView);

		mDirectory = new File(DEFAULT_INITIAL_DIRECTORY);
		
		mFiles = new ArrayList<File>();
		
		mAdapter = new FilePickerListAdapter(this, mFiles);

		setListAdapter(mAdapter);
		
		// Initialize the extensions array to allow any file extensions
		acceptedFileExtensions = new String[] {"apk"};
		/* uncomment for show all extensions
		if(getIntent().hasExtra(EXTRA_FILE_PATH)) {
			mDirectory = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));
		}
		if(getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES)) {
			mShowHiddenFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);
		}
		if(getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {
			ArrayList<String> collection = getIntent().getStringArrayListExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);
			acceptedFileExtensions = (String[]) collection.toArray(new String[collection.size()]);
		}*/
	}
	
	/**
	 * Updates the list view to the current directory
	 */
	protected void refreshFilesList() {
		// Clear the files ArrayList
		mFiles.clear();
		mAdapter.infoIsOutdated();
		
		// Set the extension file filter
		ExtensionFilenameFilter filter = new ExtensionFilenameFilter(acceptedFileExtensions);
		
		// Get the files in the directory
		File[] files = mDirectory.listFiles(filter);
		if(files != null && files.length > 0) {
			for(File f : files) {
				if(f.isHidden() && !mShowHiddenFiles) {
					// Don't add the file
					continue;
				}
				// Add the file the ArrayAdapter
				mFiles.add(f);
			}
			
			Collections.sort(mFiles, new FileComparator());
		}
		mAdapter.notifyDataSetChanged();
		setTitle(mDirectory.getPath());
		
		new AsyncManager().execute();
	}
	
	@Override
	public void onBackPressed() {
		if(mDirectory.getParentFile() != null && !mDirectory.getPath().equals(DEFAULT_INITIAL_DIRECTORY)) {
			// Go to parent directory
			mDirectory = mDirectory.getParentFile();
			refreshFilesList();
			this.getListView().setSelection(mLastScrollPosition);
			return;
		}
		
		super.onBackPressed();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File newFile = (File)l.getItemAtPosition(position);
		
		mLastScrollPosition = getListView().getFirstVisiblePosition();
		
		if(newFile.isFile()) {
			runAppFromApkFile(newFile.getAbsolutePath());
		} else {
			mDirectory = newFile;
			refreshFilesList();
		}
		
		super.onListItemClick(l, v, position, id);
	}
	
	private class FilePickerListAdapter extends ArrayAdapter<File> {
		
		private List<File> mObjects;
		private LayoutInflater mInflater;
		private HashMap<String, AppItem> mObjectsInfo = null;
		
		public FilePickerListAdapter(Context context, List<File> objects) {
			super(context, R.layout.row_item, android.R.id.text1, objects);
			this.mInflater = LayoutInflater.from(context);
			mObjects = objects;
		}
		
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder holder;
			View rowView = view;
			if (rowView == null) {
				rowView = this.mInflater.inflate(R.layout.row_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) rowView.findViewById(R.id.item_icon);
				holder.name = (TextView) rowView.findViewById(R.id.item_text);
				holder.version = (TextView) rowView.findViewById(R.id.item_version);
				holder.size = (TextView) rowView.findViewById(R.id.item_size);
				rowView.setTag(holder);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}
			
			File object = mObjects.get(position);

			holder.name.setText(object.getName());
			if(object.isFile()) {
				
				// Show the file icon
				if (mObjectsInfo != null && mObjectsInfo.get(object.getName()) != null) {
					holder.icon.setImageDrawable(mObjectsInfo.get(object.getName()).icon);
					holder.version.setText(mObjectsInfo.get(object.getName()).version_name);
					holder.size.setText(mObjectsInfo.get(object.getName()).size);
				} else {
					//holder.icon.setImageDrawable(mObjectsInfo.get(object.getName()).icon);
					holder.icon.setImageResource(R.drawable.icon_empty);
					holder.version.setText("-");
					holder.size.setText("-");
				}
			} else {
				// Show the folder icon
				holder.icon.setImageResource(R.drawable.icon_explorer2);
				holder.version.setText("");
				holder.size.setText("");
			}
			
			return rowView;
		}
		
		public void setNewInfo(HashMap<String, AppItem> newObjectsInfo) {
			mObjectsInfo = newObjectsInfo;
			mAdapter.notifyDataSetChanged();
		}
		
		public void infoIsOutdated() {
			mObjectsInfo = null;
		}
	}
	
	private class ViewHolder {
		private ImageView icon;
		private TextView name;
		private TextView version;
		private TextView size;
	}
	
	private class FileComparator implements Comparator<File> {
	    @Override
	    public int compare(File f1, File f2) {
	    	if(f1 == f2) {
	    		return 0;
	    	}
	    	if(f1.isDirectory() && f2.isFile()) {
	        	// Show directories above files
	        	return 1;
	        }
	    	if(f1.isFile() && f2.isDirectory()) {
	        	// Show files below directories
	        	return -1;
	        }
	    	// Sort the directories alphabetically
	        return f1.getName().compareToIgnoreCase(f2.getName());
	    }
	}
	
	private class ExtensionFilenameFilter implements FilenameFilter {
		private String[] mExtensions;
		
		public ExtensionFilenameFilter(String[] extensions) {
			super();
			mExtensions = extensions;
		}
		
		@Override
		public boolean accept(File dir, String filename) {
			if(new File(dir, filename).isDirectory()) {
				// Accept all directory names
				return true;
			}
			if(mExtensions != null && mExtensions.length > 0) {
				for(int i = 0; i < mExtensions.length; i++) {
					if(filename.endsWith(mExtensions[i])) {
						// The filename ends with the extension
						return true;
					}
				}
				// The filename did not match any of the extensions
				return false;
			}
			// No extensions has been set. Accept all file extensions.
			return true;
		}
	}
	
	private class AsyncManager extends AsyncTask<Void, String, Boolean> {
		@Override
		protected Boolean doInBackground(Void... arg0) {
			mItemsInfo = new HashMap<String, AppItem>();
		    for(int i=0;i<mFiles.size();i++) {
		    	if (mFiles.get(i).getPath().endsWith(APK_FILE_TYPE)) {
		    		String filePath = mFiles.get(i).getPath();
		    		PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
		    		if(packageInfo != null) {
		    			ApplicationInfo appInfo = packageInfo.applicationInfo;
		                if (Build.VERSION.SDK_INT >= 8) {
		                    appInfo.sourceDir = filePath;
		                    appInfo.publicSourceDir = filePath;
		                }
		                AppItem newInfo = new AppItem();
		                Drawable icon = appInfo.loadIcon(getPackageManager());
		                newInfo.icon = icon;
		                newInfo.version_name = packageInfo.versionName;
		                
		                newInfo.lsize = mFiles.get(i).length();
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
		                
		                mItemsInfo.put(mFiles.get(i).getName(), newInfo);
		    		}
		    	}
		    }
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			mAdapter.setNewInfo(mItemsInfo);
		}
	}
	
	private void runAppFromApkFile(final String pFullpath) {
		if (pFullpath.endsWith(APK_FILE_TYPE)) {
			Intent promptInstall = new Intent(Intent.ACTION_VIEW);
	        promptInstall.setDataAndType(Uri.fromFile(new File(pFullpath)), "application/vnd.android.package-archive");
	        startActivity(promptInstall); 
		}
    }
	
	@Override
	protected void onResume() {
		refreshFilesList();
		super.onResume();
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