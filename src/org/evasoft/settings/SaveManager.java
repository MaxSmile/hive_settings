package org.evasoft.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveManager {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================
		private SharedPreferences mDB;
		private SharedPreferences.Editor mDBEditor;
		
		// ===========================================================
		// Constructors
		// ===========================================================

		public SaveManager(Context context) {
			this.mDB = context.getSharedPreferences(Param.DB_NAME, Context.MODE_PRIVATE);
			this.mDBEditor = this.mDB.edit();
		}
		
		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================
		
		public boolean saveInt(final String param, final int value) {
	        this.mDBEditor.putInt(param, value);
	        return this.mDBEditor.commit();
		}
	 
		public int loadInt(final String param) {
	        return this.mDB.getInt(param, -1);
		}
}
