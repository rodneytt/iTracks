/**
 * Track.java 2011-8-18 下午9:50:22
 * AII Rights Reserved
 */

package zen.rodney.itracks;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Track extends Service {
	private static final String TAG = "Track";

	private LocationManager lm;
	private LocationListener locationListener;

	static LocateDbAdapter mlcDbHelper = null;
	private int track_id;

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		startDb();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			track_id = extras.getInt(LocateDbAdapter.TRACKID);
		}
		Log.e(TAG, "track_id:" + track_id);
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		// 查找到服务信息
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗

		String provider = lm.getBestProvider(criteria, true); // 获取GPS信息

		lm.requestLocationUpdates(provider, 100 * 1000, 500, locationListener);

	}

	public void onDestroy() {
		Log.d(TAG, "onDestroy.");
		super.onDestroy();
		stopDb();
	}

	private void startDb() {
		if (mlcDbHelper == null) {
			mlcDbHelper = new LocateDbAdapter(this);
			mlcDbHelper.open();
		}
	}

	private void stopDb() {
		if (mlcDbHelper != null) {
			mlcDbHelper.close();
		}
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			Log.d(TAG, "MyLocationListener::onLocationChanged..");
			if (loc != null) {
				if (mlcDbHelper == null) {
					mlcDbHelper.open();
				}
				mlcDbHelper.createLocate(track_id, loc.getLongitude(), loc.getLatitude(), loc.getAltitude());
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getBaseContext(), "ProviderDisabled.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getBaseContext(), "ProviderEnabled,provider:" + provider, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
