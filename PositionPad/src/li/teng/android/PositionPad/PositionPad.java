package li.teng.android.PositionPad;

import java.io.FileWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PositionPad extends Activity {
	FileWriter m_FileWriter;
	LocationManager m_LocationManager;
	String m_provider;
	ArrayList<String> m_Data;
	ArrayAdapter<String> m_DataAdapter;
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		log("onCreate");
		m_Data = new ArrayList<String>();
		m_DataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
				m_Data);
		((ListView)findViewById(R.id.lstPositions)).setAdapter(m_DataAdapter);

		try {
			m_FileWriter = new FileWriter("/sdcard/my_position.txt");
		} catch (Exception ex) {
			log("create file failed");//Log.w("PositionPad", ex.toString());
		}
		m_LocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if(m_LocationManager == null){
			log("create LocationManager failed");
		}
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		m_provider = m_LocationManager.getBestProvider(criteria, true);
		log(m_provider);
		Location location = m_LocationManager.getLastKnownLocation(m_provider);
		updateWithNewLocation(location);
		m_LocationManager.requestLocationUpdates(m_provider, 2000, 5,locationListener);
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider){
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider){ }
		public void onStatusChanged(String provider, int status, 
				Bundle extras){ }
	};

	private void updateWithNewLocation(Location location) {
		String latLongString;
		TextView myLocationText; 
		myLocationText = (TextView)findViewById(R.id.myLocationText);
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "Lat:" + lat + "\nLong:" + lng;
		} else {
			latLongString = "No location found"; 
		}
		myLocationText.setText("Your Current Position is:\n" + 
				latLongString);
	}
	
	static final private int ADD_POS = 1;
	static final private int RENAME_POS = 2;
	static final private int DELETE_POS = 3;
	static final private int LOAD_FILE = 4;
	static final private int NEW_FILE = 5;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, ADD_POS, Menu.NONE, "add");
		menu.add(0, RENAME_POS, Menu.NONE, "rename");
		menu.add(0, DELETE_POS, Menu.NONE, "delete");
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		switch (item.getItemId()) {
		case (ADD_POS): {
			addPos();
			return true;
		}
		case (RENAME_POS): {
			return true;
		}
		case (DELETE_POS): {
			return true;
		}
		}
		return false;
	}

	private boolean addPos() {
		try {
			Location location = m_LocationManager
			.getLastKnownLocation(m_provider);
			if (location != null) {
				String pos = location.getLongitude() + ":"
				+ location.getLatitude() + ":" + location.getAltitude();
				String name = ((EditText) findViewById(R.id.txtName)).getText()
				.toString();
				String line = name + "=" + pos + "\r\n";
				m_FileWriter.write(line);
				m_FileWriter.flush();
				log(line);
				m_Data.add(name);
				m_DataAdapter.notifyDataSetChanged();
			}else{
				log("getLastKnownLocation falied");
			}

		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	@Override
	protected void onPause() {
		log("onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		log("onResume");
		super.onResume();
	}

	@Override
	protected void onRestart() {
		log("onRestart");
		super.onRestart();
	}

	@Override
	protected void onStart() {
		log("onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		log("onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		log("onDestroy");
		super.onDestroy();
		if (m_FileWriter != null ) {
			try{
				m_FileWriter.close();
			}catch(Exception ex){
				;
			}
		}
		if(m_LocationManager != null){
			m_LocationManager.removeUpdates(locationListener);
		}
	}
	private void log(String msg){
		if(msg == null){
			Log.w("PositionPad", "null value");
		}else{
			Log.w("PositionPad", msg);
		}

	}
}