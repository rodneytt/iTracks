/**
 * ShowTrack.java 2011-8-16 下午9:32:28
 * AII Rights Reserved
 */

package zen.rodney.itracks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class ShowTrack extends Activity {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_track);
		findViews();
	}

	public void findViews() {
		mMapView = (MapView) findViewById(R.id.mv);
		mc = mMapView.getController();
		

		mZin = (Button) findViewById(R.id.zin);
		mZin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				zoomIn();
			}
		});
	}

	public void zoomIn() {
		mc.zoomIn();
	}
}
