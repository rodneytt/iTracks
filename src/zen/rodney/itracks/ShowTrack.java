/**
 * ShowTrack.java 2011-8-16 下午9:32:28
 * AII Rights Reserved
 */

package zen.rodney.itracks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class ShowTrack extends MapActivity {
	private static final int MENU_NEW = Menu.FIRST + 1;
	private static final int MENU_CON = MENU_NEW + 1;
	private static final int MENU_DEL = MENU_CON + 1;
	private static final int MENU_MAIN = MENU_DEL + 1;

	private TrackDbAdapter mDbHelper;
	private LocateDbAdapter mlcDbHelper;

	private static final String TAG = "ShowTrack";
	private static MapView mMapView;
	private MapController mc;

	protected MyLocationOverlay mOverlayController;
	private Button mZin;
	private Button mZout;
	private Button mPanN;
	private Button mPanE;
	private Button mPanW;
	private Button mPanS;
	private Button mGps;
	private Button mSat;
	private Button mTraffic;
	private Button mStreetview;
	private String mDefCaption = "";
	private GeoPoint mDefPoint;

	private LocationManager lm;
	private LocationListener locationListener;

	private int track_id;
	private Long rowId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_track);
		findViews();
		centerOnGPSPosition();
		revArgs();
		paintLocates();
		startTrackService();
	}

	private void paintLocates() {
		mlcDbHelper = new LocateDbAdapter(this);
		mlcDbHelper.open();
		Cursor mCur = mlcDbHelper.getTrackAllLocates(track_id);
		startManagingCursor(mCur);
		Resources res = getResources();
		Overlay overlays = new LocateOverLay(res.getDrawable(R.drawable.icon), mCur);
		mMapView.getOverlays().add(overlays);
		mlcDbHelper.close();
	}

	private void revArgs() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String name = extras.getString(TrackDbAdapter.NAME);
			rowId = extras.getLong(TrackDbAdapter.KEY_ROWID);
			track_id = rowId.intValue();
			if (name != null) {
				setTitle(name);
			}

		}
	}

	public void findViews() {
		mMapView = (MapView) findViewById(R.id.mv);
		mc = mMapView.getController();

		SharedPreferences settings = getSharedPreferences(Setting.SETTING_INFOS, 0);
		String setting_gps = settings.getString(Setting.SETTING_MAP, "10");
		mc.setZoom(Integer.parseInt(setting_gps));

		mZin = (Button) findViewById(R.id.zin);
		mZin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				zoomIn();
			}
		});

		mZout = (Button) findViewById(R.id.zout);
		mZout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				zoomOut();
			}
		});
		mPanE = (Button) findViewById(R.id.pane);
		mPanE.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				panEast();
			}
		});
		mPanS = (Button) findViewById(R.id.pans);
		mPanS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				panSouth();
			}
		});
		mPanW = (Button) findViewById(R.id.panw);
		mPanW.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				panWest();
			}
		});
		mPanN = (Button) findViewById(R.id.pann);
		mPanN.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				panNorth();
			}
		});
		mGps = (Button) findViewById(R.id.gps);
		mGps.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				centerOnGPSPosition();
			}
		});
		mSat = (Button) findViewById(R.id.sat);
		mSat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleSatellite();
			}
		});
		mTraffic = (Button) findViewById(R.id.traffic);
		mTraffic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleTraffic();
			}
		});
		mStreetview = (Button) findViewById(R.id.streetview);
		mStreetview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleStreetView();
			}
		});
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10 * 60 * 1000, 500, locationListener);
	}

	public void toggleStreetView() {
		mMapView.setSatellite(false);
		mMapView.setTraffic(false);
		mMapView.setStreetView(true);
	}

	public void toggleTraffic() {
		mMapView.setSatellite(false);
		mMapView.setTraffic(true);
		mMapView.setStreetView(false);
	}

	public void toggleSatellite() {
		mMapView.setSatellite(true);
		mMapView.setStreetView(false);
		mMapView.setTraffic(false);
	}

	public void panEast() {
		GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6(), mMapView.getMapCenter().getLongitudeE6()
				+ mMapView.getLongitudeSpan() / 4);
		mc.setCenter(pt);
	}

	public void panSouth() {
		GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6() - mMapView.getLatitudeSpan() / 4, mMapView
				.getMapCenter().getLongitudeE6());
		mc.setCenter(pt);
	}

	public void panWest() {
		GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6(), mMapView.getMapCenter().getLongitudeE6()
				- mMapView.getLongitudeSpan() / 4);
		mc.setCenter(pt);
	}

	public void panNorth() {
		GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6() + mMapView.getLatitudeSpan() / 4, mMapView
				.getMapCenter().getLongitudeE6());
		mc.setCenter(pt);
	}

	public void centerOnGPSPosition() {
		String provider = "gps";
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location loc = lm.getLastKnownLocation(provider);
		if (loc == null) {
			return;
		}
		mDefPoint = new GeoPoint((int) (loc.getLatitude() * 1000000), (int) (loc.getLongitude() * 1000000));
		mDefCaption = "I'm here.";
		mc.animateTo(mDefPoint);
		mc.setCenter(mDefPoint);
		MyOverlay mo = new MyOverlay();
		mo.onTap(mDefPoint, mMapView);
		mMapView.getOverlays().add(mo);
	}

	public void zoomIn() {
		mc.zoomIn();
	}

	public void zoomOut() {
		mc.zoomOut();
	}

	protected class MyOverlay extends Overlay {
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			if (mDefCaption.length() == 0) {
				return;
			}
			Paint p = new Paint();
			int[] scoords = new int[2];
			int sz = 5;

			Point myScreenCoords = new Point();
			mMapView.getProjection().toPixels(mDefPoint, myScreenCoords);
			scoords[0] = myScreenCoords.x;
			scoords[1] = myScreenCoords.y;
			p.setTextSize(14);
			p.setAntiAlias(true);

			int sw = (int) (p.measureText(mDefCaption) + 0.5f);
			int sh = 25;
			int sx = scoords[0] - sw / 2 - 5;
			int sy = scoords[1] - sh - sz - 2;

			RectF rec = new RectF(sx, sy, sx + sw + 10, sy + sh);
			p.setStyle(Style.FILL);
			p.setARGB(128, 255, 0, 0);
			canvas.drawRoundRect(rec, 5, 5, p);

			p.setStyle(Style.STROKE);
			p.setARGB(255, 255, 255, 255);
			canvas.drawRoundRect(rec, 5, 5, p);

			canvas.drawText(mDefCaption, sx + 5, sy + sh - 8, p);

			p.setStyle(Style.FILL);
			p.setARGB(88, 255, 0, 0);
			p.setStrokeWidth(1);

			RectF spot = new RectF(scoords[0] - sz, scoords[1] + sz, scoords[0] + sz, scoords[1] - sz);
			canvas.drawOval(spot, p);

			p.setARGB(255, 255, 0, 0);
			p.setStyle(Style.STROKE);
			canvas.drawCircle(scoords[0], scoords[1], sz, p);
		}
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				Toast.makeText(getBaseContext(), "位置发生变化!经度:" + loc.getLatitude() + ";纬度:" + loc.getLongitude(),
						Toast.LENGTH_SHORT).show();
				mDefPoint = new GeoPoint((int) (loc.getLatitude() * 1000000), (int) (loc.getLongitude() * 1000000));
				mc.animateTo(mDefPoint);
				mc.setCenter(mDefPoint);
				mDefCaption = "经度:" + loc.getLatitude() + ";纬度:" + loc.getLongitude();
				MyOverlay mo = new MyOverlay();
				mo.onTap(mDefPoint, mMapView);
				mMapView.getOverlays().add(mo);
			}
		}

		@Override
		public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getBaseContext(), "ProviderEnabled,provider:" + provider, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderDisabled(String paramString) {
			Toast.makeText(getBaseContext(), "gps不可用", Toast.LENGTH_SHORT).show();
		}

	}

	// 初始化菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_CON, 0, R.string.menu_con).setIcon(R.drawable.con_track).setAlphabeticShortcut('C');
		menu.add(0, MENU_DEL, 0, R.string.menu_del).setIcon(R.drawable.delete).setAlphabeticShortcut('D');
		menu.add(0, MENU_NEW, 0, R.string.menu_new).setIcon(R.drawable.new_track).setAlphabeticShortcut('N');
		menu.add(0, MENU_MAIN, 0, R.string.menu_main).setIcon(R.drawable.icon).setAlphabeticShortcut('M');
		return true;
	}

	// 当一个菜单被选中的时候调用
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case MENU_NEW:
			intent.setClass(ShowTrack.this, NewTrack.class);
			startActivity(intent);
			return true;
		case MENU_CON:
			// TODO: 继续跟踪选择的记录
			startTrackService();
			return true;
		case MENU_DEL:
			mDbHelper = new TrackDbAdapter(this);
			mDbHelper.open();
			if (mDbHelper.deleteTrack(rowId)) {
				mDbHelper.close();
				intent.setClass(ShowTrack.this, ITracksActivity.class);
				startActivity(intent);
			} else {
				mDbHelper.close();
			}
			return true;
		case MENU_MAIN:
			intent.setClass(ShowTrack.this, ITracksActivity.class);
			startActivity(intent);
			break;
		}
		return true;
	}

	private void startTrackService() {
		Intent i = new Intent("zen.rodney.itracks.START_TRACK_SERVICE");
		i.putExtra(LocateDbAdapter.TRACKID, track_id);
		startService(i);
	}

	private void stopTrackService() {
		stopService(new Intent("zen.rodney.itracks.START_TRACK_SERVICE"));
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
		// mDbHelper.close();
		// mlcDbHelper.close();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy.");
		super.onDestroy();
		stopTrackService();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
