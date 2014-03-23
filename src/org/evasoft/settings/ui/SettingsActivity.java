package org.evasoft.settings.ui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.evasoft.settings.Param;
import org.evasoft.settings.R;
import org.evasoft.settings.SaveManager;
import org.evasoft.settings.constants.IPrefs;
import org.evasoft.settings.utils.AirplaneController;
import org.evasoft.settings.utils.BluetoothController;
import org.evasoft.settings.utils.BluetoothController.IBluetoothChangeListener;
import org.evasoft.settings.utils.ColorController;
import org.evasoft.settings.utils.GPSController;
import org.evasoft.settings.utils.Notif;
import org.evasoft.settings.utils.RingerController;
import org.evasoft.settings.utils.RotationController;
import org.evasoft.settings.utils.WifiController;
import org.evasoft.settings.utils.WifiController.IWifiChangeListener;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends Activity implements IPrefs {
	
	private static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
	private static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
	private static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
	
	private AirplaneController airplaneController;
	private BluetoothController bluetoothController;
	private ColorController colorController;
	private GPSController mGpsController;
	private RingerController mRingerController;
	private WifiController wifiController;
	
	private SaveManager sm;
	
	private BroadcastReceiver mBatteryReceiver;
	private BroadcastReceiver mWifiChangeReceiver;
	
	private IntentFilter mBatteryIntentFilter;
	private IntentFilter mWifiChangeIntentFilter;
	
	ArrayAdapter<CharSequence> mScreenOffAdapter;
		
	private TextView btn_airplane;
	private TextView btn_apps;
	private TextView btn_autosync;
	private TextView btn_battery;
	private TextView btn_bluetooth;
	private TextView btn_brightness;
	private TextView btn_explorer;
	private TextView btn_g_networks;
	private TextView btn_gps;
	private RelativeLayout btn_menu; 
	private TextView btn_profiles;
	private TextView btn_ringer;
	private TextView btn_rotation;
	private TextView btn_security;
	private TextView btn_screenoff;
	private TextView btn_wifi;
	
	
	private int mWorldScreenOffPosition = 0;
	private boolean accessPointIsOn = false;
    private boolean lightSensorOk = false; 
    
    /** APN, 3G, 4G */
    private int mCurrentNetwork;
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.main_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
    	{
        case R.id.menu_settings:
        	try {
				Intent intent = new Intent(SettingsActivity.this, PrefActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
       			startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
            return true;
        case R.id.menu_quit:
        	disableNotify();
        	System.exit(0);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
		
		sm = new SaveManager(this);
		mCurrentNetwork = sm.loadInt(Param.G_NETWORKS);
		if (mCurrentNetwork == -1) {
			mCurrentNetwork = NET_2G;
		}
        
		this.mScreenOffAdapter = ArrayAdapter.createFromResource(this, R.array.screen_off_items, android.R.layout.simple_spinner_dropdown_item);
		
		this.createControllers();
		this.createButtons();
		
		final SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		final Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		if (lightSensor != null) {
			lightSensorOk = true;
		} else {
			if (lightSensorOk) {
				lightSensorOk = false;	
			}
		}
        
		this.mBatteryIntentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
		this.mWifiChangeIntentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		
		this.mBatteryReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String str = intent.getAction();
				if (str.equals("android.intent.action.BATTERY_CHANGED")) {
					int level = intent.getIntExtra("level", 0);  
					SettingsActivity.this.btn_battery.setText(String.valueOf(level) + "%");
					if (level >= 75) { // full 4
						SettingsActivity.this.btn_battery.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_battery_full, R.color.white), null, null);
					} else if (level < 75 && level > 50) { // high 3
						SettingsActivity.this.btn_battery.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_battery_high, R.color.white), null, null);
					} else if (level <= 50 && level > 30) { // mid 2
						SettingsActivity.this.btn_battery.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_battery_mid, R.color.white), null, null);
					} else if (level <= 30 && level > 10) { // low 1
						SettingsActivity.this.btn_battery.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_battery_low, R.color.white), null, null);
					} else if (level <= 10) { // empty 0
						SettingsActivity.this.btn_battery.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_battery_empty, R.color.red), null, null);
					}
				}
			}
		};
		
		this.btn_airplane.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						SettingsActivity.this.airplaneController.setAirplaneMode();
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								btn_airplane.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_airplane, SettingsActivity.this.airplaneController.isEnabled()), null, null);
							}
						}, 150);
					}
				});
			}
		});
		
		this.btn_airplane.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		
		this.btn_apps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					startActivity(new Intent(SettingsActivity.this, AppsActivity.class));
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		
		this.btn_apps.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		
		this.btn_autosync.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final boolean state = ContentResolver.getMasterSyncAutomatically();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (state) {
							ContentResolver.setMasterSyncAutomatically(false);
						} else ContentResolver.setMasterSyncAutomatically(true);
					}
				}).start();
				btn_autosync.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_sync, !state), null, null);
			}
		});
		
		this.btn_autosync.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		
		this.btn_battery.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					Intent intent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		
		if (bluetoothController.bluetoothExists()) {
			this.btn_bluetooth.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							SettingsActivity.this.bluetoothController.toggleBluetooth();
						}
					}).start();
				}
			});
			
		this.btn_bluetooth.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		}
		
		this.btn_brightness.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (!isBrightnessAuto()) {
						Intent start = new Intent(SettingsActivity.this, BrightnessActivity.class);
						startActivity(start);
					}
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		
		this.btn_brightness.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					toggleAutoBrightness();
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		
		this.btn_explorer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					startActivity(new Intent(SettingsActivity.this, ExplorerActivity.class));	
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		
		this.btn_g_networks.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				switch (mCurrentNetwork) {
				case NET_2G:
					try {
						intent.setClassName("com.android.phone", "com.android.phone.MobileNetworkSettings");
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case NET_3G:
					try {
						intent.setClassName("com.android.phone", "com.android.phone.Settings");
						startActivity(intent);							
					} catch (SecurityException e) {
						Notif.showToast(SettingsActivity.this, getResources().getDrawable(R.drawable.icon_g_networks), getString(R.string.s_security_ex), Toast.LENGTH_SHORT);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case NET_4G:
					try {
						intent.setClassName("com.android.settings","com.android.settings.wimax.WimaxSettings");
			            startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		});
		
		this.btn_g_networks.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				mCurrentNetwork++;
				if (mCurrentNetwork > NET_4G) {
					mCurrentNetwork = NET_2G;
				}
				sm.saveInt(Param.G_NETWORKS, mCurrentNetwork);
				updateNetworksModeAndBtnText();
				return true;
			}
		});
		
		this.btn_gps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		
		this.btn_gps.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							final Intent intent = new Intent();
			                intent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			                intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
			                intent.setData(Uri.parse("custom:3"));
			                SettingsActivity.this.sendBroadcast(intent);
						} catch (Exception e) {
							Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
							startActivity(intent);
						}
					}
				}).start();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
							SettingsActivity.this.gpsButtonToggle();	
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						}
					}
				}, 150);
				return true;
			}
		});
		
		this.btn_menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					SettingsActivity.this.openOptionsMenu();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		this.btn_profiles.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(SettingsActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
			}
		});
		
		this.btn_ringer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					startActivity(new Intent(SettingsActivity.this, VolumeActivity.class));	
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		
		this.btn_ringer.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					Intent intent = new Intent(SettingsActivity.this, RingerModeActivity.class);
					intent.putExtra(Param.RINGER_MODE_ID_NAME, SettingsActivity.this.mRingerController.getSoundState());
					startActivityForResult(intent, Param.REQUEST_RINGER_MODE);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
				return true;
			}
		});
		
		this.btn_rotation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RotationController.lockOrientation(SettingsActivity.this);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								btn_rotation.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_rotation, RotationController.getOrientationState(SettingsActivity.this)), null, null);						
							}
						}, 150);								
					}
				});
			}
		});
		
		this.btn_security.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Intent start = new Intent();
							start.setClassName("com.android.settings", "com.android.settings.ChooseLockGeneric");
							start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
							SettingsActivity.this.startActivity(start);
						} catch (ActivityNotFoundException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
		
		this.btn_security.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		
		this.btn_screenoff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(SettingsActivity.this, ScreenOffActivity.class);
					intent.putExtra(Param.SCREEN_OFF_ID_NAME, SettingsActivity.this.mWorldScreenOffPosition);
					startActivityForResult(intent, Param.REQUEST_SCREEN_OFF);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		
		this.btn_wifi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						SettingsActivity.this.wifiController.toggleWifi();
					}
				}).start();
			}
		});
		
		this.btn_wifi.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		
		if (bluetoothController.bluetoothExists()) {
			IBluetoothChangeListener b_listener = new BluetoothController.IBluetoothChangeListener() {
				@Override
				public void onChange(BluetoothController bluetoothController, int state) {
					switch (SettingsActivity.this.bluetoothController.getBluetoothState()) {
					case BLUETOOTH_ON:
						SettingsActivity.this.btn_bluetooth.setEnabled(true);
						SettingsActivity.this.btn_bluetooth.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_bluetooth, R.color.lightblue), null, null);
						
						break;
					case BLUETOOTH_OFF:
						SettingsActivity.this.btn_bluetooth.setEnabled(true);
						SettingsActivity.this.btn_bluetooth.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_bluetooth, R.color.white), null, null);
						
						break;
					case BLUETOOTH_TURNING_ON:
						SettingsActivity.this.btn_bluetooth.setEnabled(false);
						SettingsActivity.this.btn_bluetooth.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_bluetooth, R.color.gray), null, null);
						break;
					case BLUETOOTH_TURNING_OFF:
						SettingsActivity.this.btn_bluetooth.setEnabled(false);
						SettingsActivity.this.btn_bluetooth.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_bluetooth, R.color.gray), null, null);
						break;
					}
				}
			};
			this.bluetoothController.setBluetoothChangeListener(b_listener);
		}
		
		IWifiChangeListener wifiChangeListener = new WifiController.IWifiChangeListener() {
			@Override
			public void onChange(WifiController wifiController) {
				switch (wifiController.getWifiState()) {
				case WifiManager.WIFI_STATE_DISABLING:
					SettingsActivity.this.btn_wifi.setEnabled(false);
					SettingsActivity.this.btn_wifi.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_wifi, R.color.gray), null, null);
					break;
				case WifiManager.WIFI_STATE_DISABLED:
					SettingsActivity.this.btn_wifi.setEnabled(true); 
					SettingsActivity.this.btn_wifi.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_wifi, R.color.white), null, null);
					
					break;
				case WifiManager.WIFI_STATE_ENABLING:
					SettingsActivity.this.btn_wifi.setEnabled(false); 
					SettingsActivity.this.btn_wifi.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_wifi, R.color.gray), null, null);
					break;
				case WifiManager.WIFI_STATE_ENABLED:
					SettingsActivity.this.btn_wifi.setEnabled(true); 
					SettingsActivity.this.btn_wifi.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_wifi, R.color.lightblue), null, null);
					break;
				}
				
				if (wifiController.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
					WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					final String wifiPointName = wifiManager.getConnectionInfo().getSSID();
					if (wifiPointName != null && accessPointIsOn) {
						btn_wifi.setText(getString(R.string.s_point) + " " + wifiPointName);
					}
				} else btn_wifi.setText(getString(R.string.s_wifi));
			}
		};
		this.wifiController.setWifiChangeListener(wifiChangeListener);
		
		this.mWifiChangeReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			    if(networkInfo.isConnected()) {
			    	accessPointIsOn = true;
			    	WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			    	final String wifiPointName = wifiManager.getConnectionInfo().getSSID();
					if (wifiPointName != null) {
						btn_wifi.setText(getString(R.string.s_point) + " " + wifiPointName);						
					} else {
						accessPointIsOn = false;
						btn_wifi.setText(getString(R.string.s_wifi));
					}
			    } else btn_wifi.setText(getString(R.string.s_wifi));
			}
		};
		if (GOOGLE_PLAY && sm.loadInt(Param.SHOW_RATE_AGAIN) != 2) {
			if (sm.loadInt(Param.LAUNCH_COUNTER) == -1 && sm.loadInt(Param.SHOW_RATE_AGAIN) == -1) {
				sm.saveInt(Param.LAUNCH_COUNTER, 1);
			} else {
				int result = sm.loadInt(Param.LAUNCH_COUNTER);
				if (result < 20) {
					result++;
					sm.saveInt(Param.LAUNCH_COUNTER, result);	
				} else if (result == 20) {
					startActivity(new Intent(SettingsActivity.this, RateActivity.class));			
				}
			}
		}
	}
	
	private void updateAccessPointName() {
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		final String wifiPointName = wifiManager.getConnectionInfo().getSSID();
		if (wifiPointName != null && accessPointIsOn) {
		btn_wifi.setText(getString(R.string.s_point) + " " + wifiPointName);
		} else {
			btn_wifi.setText(getString(R.string.s_wifi));
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		this.wifiController.onResume();
		if (this.bluetoothController.bluetoothExists()) {
			this.bluetoothController.onResume();
		}
		this.updateBtnColors();
		this.updateBtnText();
		if (this.mBatteryReceiver != null) {
			this.registerReceiver(this.mBatteryReceiver, this.mBatteryIntentFilter);
		}
		
		if (this.mWifiChangeReceiver != null) {
			this.registerReceiver(this.mWifiChangeReceiver, this.mWifiChangeIntentFilter);	
		}
		
		this.updateAccessPointName();

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean notifService= pref.getBoolean("notification_icon", true);
		if (notifService) {
			enableNotify();
		} else {
			disableNotify();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.wifiController.onPause();
		if (this.bluetoothController.bluetoothExists()) {
			this.bluetoothController.onPause();
		}
		
		if (this.mBatteryReceiver != null) {
			this.unregisterReceiver(this.mBatteryReceiver);
		}
		
		if (this.mWifiChangeReceiver != null) {
			this.unregisterReceiver(this.mWifiChangeReceiver);
		}
	}

	private void createControllers() {
		this.airplaneController = new AirplaneController(this);
		this.bluetoothController = new BluetoothController(this);
		this.colorController = new ColorController(this);
		this.mGpsController = new GPSController(this);
		this.mRingerController = new RingerController(this);
		this.wifiController = new WifiController(this);
	}

	private void createButtons() {
		this.btn_airplane = (TextView) findViewById(R.id.btn_airplane);
		this.btn_apps = (TextView) findViewById(R.id.btn_apps);
		this.btn_autosync = (TextView) findViewById(R.id.btn_sync);
		this.btn_battery = (TextView) findViewById(R.id.btn_battery);
		this.btn_bluetooth = (TextView) findViewById(R.id.btn_bluetooth);
		this.btn_brightness = (TextView) findViewById(R.id.btn_bright);
		this.btn_explorer = (TextView) findViewById(R.id.btn_explorer);
		this.btn_g_networks = (TextView) findViewById(R.id.btn_g_networks);
		this.btn_gps  = (TextView) findViewById(R.id.btn_gps);
		this.btn_menu  = (RelativeLayout) findViewById(R.id.btn_menu);
		this.btn_profiles = (TextView) findViewById(R.id.btn_profiles);
		this.btn_ringer = (TextView) findViewById(R.id.btn_ringermode);
		this.btn_rotation = (TextView) findViewById(R.id.btn_rotation);
		this.btn_screenoff = (TextView) findViewById(R.id.btn_scree_off);
		this.btn_security = (TextView) findViewById(R.id.btn_security);
		this.btn_wifi = (TextView) findViewById(R.id.btn_wifi);
	}
	/** Меняем цвета кнопок */
	private void updateBtnColors() {
		this.btn_airplane.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_airplane, SettingsActivity.this.airplaneController.isEnabled()), null, null);
		this.btn_autosync.setCompoundDrawablesWithIntrinsicBounds(null, SettingsActivity.this.colorController.swapColor(R.drawable.icon_sync, ContentResolver.getMasterSyncAutomatically()), null, null);
		if (isBrightnessAuto() == false) {
			this.btn_brightness.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_brightness_half, R.color.white), null, null);
		} else {
			this.btn_brightness.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_brightness_auto, R.color.lightblue), null, null);
		}
		try {
			this.gpsButtonToggle();	
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		if (bluetoothController.bluetoothExists()) {
			this.btn_bluetooth.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_bluetooth, SettingsActivity.this.bluetoothController.isEnabled()), null, null);
		} else {
			this.btn_bluetooth.setEnabled(false);
			this.btn_bluetooth.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_bluetooth, R.color.gray), null, null);
		}
		try {
			this.btn_rotation.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_rotation, RotationController.getOrientationState(this)), null, null);	
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		final boolean security_ok = android.provider.Settings.System.getInt(getContentResolver(), "lock_pattern_autolock", 0) == 1; // Settings.Secure.LOCK_PATTERN_ENABLED - API LVL 8
		if (security_ok) {
			this.btn_security.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_security, R.color.lightblue), null, null);
		} else this.btn_security.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_security, R.color.white), null, null);
	}
	
	private void updateBtnText() {
		updateNetworksModeAndBtnText();
		if (isBrightnessAuto() == false) {
			try {
				final int level = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
				this.btn_brightness.setText(getString(R.string.s_bright) + " " + Math.round(BrightnessActivity.converLevelToPercent(level)) + "%");
			} catch (SettingNotFoundException e) {
				e.printStackTrace();
			} 
		} else {
			this.btn_brightness.setText(getString(R.string.s_brightness_auto));
		}
		
		this.updateScreenOff();
		
		this.btn_ringer.setText(mRingerController.getRingerName());
	}
	
	private void updateNetworksModeAndBtnText() {
		if (mCurrentNetwork == NET_2G) {
			this.btn_g_networks.setText(getString(R.string.s_2g));
		} else if (mCurrentNetwork == NET_3G) {
			this.btn_g_networks.setText(getString(R.string.s_3g));
		} else if (mCurrentNetwork == NET_4G) {
			this.btn_g_networks.setText(getString(R.string.s_4g));
		}
	}
	
	private void gpsButtonToggle() throws IllegalArgumentException {
		switch (this.mGpsController.getNetworks()) {
		case 0:
			this.btn_gps.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_gps, R.color.white), null, null);
			this.btn_gps.setText(R.string.s_location);
			break;
		case GPS:
			this.btn_gps.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_gps, R.color.lightblue), null, null);
			this.btn_gps.setText(R.string.s_gps);
			break;
		case GPS_AND_NETWORK:
			this.btn_gps.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_gps, R.color.red), null, null);
			this.btn_gps.setText(R.string.s_gps_network);
			break;
		case NETWORK:
			this.btn_gps.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_gps, R.color.lightgreen), null, null);
			this.btn_gps.setText(R.string.s_network);
			break;
		}
	}
	
	private void toggleAutoBrightness() {
		if (!lightSensorOk) {
			Notif.showToast(this, getResources().getDrawable(R.drawable.icon_brightness_half), getString(R.string.s_no_light_sensor));
			return;
		}
		if (isBrightnessAuto() == false) {
	        Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
	        this.btn_brightness.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_brightness_auto, R.color.lightblue), null, null);
	        this.btn_brightness.setText(getString(R.string.s_brightness_auto));
	        refreshBrightness(getBrightnessLevel());
	    } else {
	        Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);
	        this.btn_brightness.setCompoundDrawablesWithIntrinsicBounds(null, this.colorController.swapColor(R.drawable.icon_brightness_half, R.color.white), null, null);
	        this.updateBtnText();
	        refreshBrightness(-1);
	    }
	}
	
	private void refreshBrightness(float brightness) {
	    WindowManager.LayoutParams lp = getWindow().getAttributes();
	    if (brightness < 0) {
	        lp.screenBrightness = -1; //WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE - API LVL 8
	    } else {
	        lp.screenBrightness = -1;//brightness;
	    }
	    getWindow().setAttributes(lp);
	}

	private int getBrightnessLevel() {
	    try {
	        int value = Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
	        // convert brightness level to range 0..1
	        value = value / 255;
	        return value;
	    } catch (SettingNotFoundException e) {
	        return 0;
	    }
	}
	
	private boolean isBrightnessAuto() {
		final int state = Settings.System.getInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);
		if (state == 0) {
			return false;
		} else return true;
	}
	
	private void updateScreenOff() {
		int time = 0;
		try {
			time = Settings.System.getInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		
		switch (time) {
		case 15000:
			this.btn_screenoff.setText(this.mScreenOffAdapter.getItem(0));
			SettingsActivity.this.mWorldScreenOffPosition = 0;
			break;
		case 30000:
			this.btn_screenoff.setText(this.mScreenOffAdapter.getItem(1));
			SettingsActivity.this.mWorldScreenOffPosition = 1;
			break;
		case 60000:
			this.btn_screenoff.setText(this.mScreenOffAdapter.getItem(2));
			SettingsActivity.this.mWorldScreenOffPosition = 2;
			break;
		case 120000:
			this.btn_screenoff.setText(this.mScreenOffAdapter.getItem(3));
			SettingsActivity.this.mWorldScreenOffPosition = 3;
			break;
		case 300000:
			this.btn_screenoff.setText(this.mScreenOffAdapter.getItem(4));
			SettingsActivity.this.mWorldScreenOffPosition = 4;
			break;			
		case 600000:
			this.btn_screenoff.setText(this.mScreenOffAdapter.getItem(5));
			SettingsActivity.this.mWorldScreenOffPosition = 5;
			break;
		case 1800000:
			this.btn_screenoff.setText(this.mScreenOffAdapter.getItem(6));
			SettingsActivity.this.mWorldScreenOffPosition = 6;
			break;			
		case -1:
			this.btn_screenoff.setText(this.mScreenOffAdapter.getItem(7));
			SettingsActivity.this.mWorldScreenOffPosition = 7;
			break;
		}
	}

	public void enableNotify() {
		// tray icon
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); 
	    int icon = R.drawable.ic_launcher;
	    Intent notificationIntent = new Intent(this, SettingsActivity.class);
	    Notification notification = new Notification(icon, getString(R.string.app_name_ex), 0);
	    notification.flags |= Notification.FLAG_ONGOING_EVENT;
	    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	    notification.setLatestEventInfo(this, getText(R.string.app_name), getString(R.string.notif_text) + " " + getString(R.string.app_name_ex), contentIntent);
	    notificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	public void disableNotify() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	mNotificationManager.cancel(NOTIFICATION_ID);
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
	    final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    final Class conmanClass = Class.forName(conman.getClass().getName());
	    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
	    iConnectivityManagerField.setAccessible(true);
	    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
	    final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
	    final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
	    setMobileDataEnabledMethod.setAccessible(true);

	    setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {return;}
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Param.REQUEST_SCREEN_OFF:
				final int pos = data.getIntExtra(Param.SCREEN_OFF_ID_NAME, 0);
				switch (pos) {
				case 0:
					Settings.System.putInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 15 * Param.MILLISECONDS);
					break;
				case 1:
					Settings.System.putInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 30 * Param.MILLISECONDS);
					break;
				case 2:
					Settings.System.putInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 1 * Param.SECONDS * Param.MILLISECONDS);
					break;
				case 3:
					Settings.System.putInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 2 * Param.SECONDS * Param.MILLISECONDS);
					break;
				case 4:
					Settings.System.putInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 5 * Param.SECONDS * Param.MILLISECONDS);
					break;
				case 5:
					Settings.System.putInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 10 * Param.SECONDS * Param.MILLISECONDS);
					break;
				case 6:
					Settings.System.putInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 30 * Param.SECONDS * Param.MILLISECONDS);
					break;
				case 7:
					Settings.System.putInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
					break;
				}
				SettingsActivity.this.updateScreenOff();
				break;
			case Param.REQUEST_RINGER_MODE:
				final int pos2 = data.getIntExtra(Param.RINGER_MODE_ID_NAME, 0);
				switch (pos2) {
				case 0:
					SettingsActivity.this.mRingerController.setSoundState(STATE_SOUND_ON);
					break;
				case 1:
					SettingsActivity.this.mRingerController.setSoundState(STATE_SOUND_AND_VIBRO);
					break;
				case 2:
					SettingsActivity.this.mRingerController.setSoundState(STATE_VIBRO);
					break;
				case 3:
					SettingsActivity.this.mRingerController.setSoundState(STATE_SILENT);
					break;
				}
            	SettingsActivity.this.btn_ringer.setText(mRingerController.getRingerName());
				break;
			default:
				break;
			}
		}
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