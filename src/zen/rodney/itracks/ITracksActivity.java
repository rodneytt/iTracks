package zen.rodney.itracks;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class ITracksActivity extends ListActivity {
	private final String TAG = "Itracks";
	private TrackDbAdapter mDbHelper;
	private Cursor mTrackCursor;

	private static final int MENU_NEW = Menu.FIRST + 1;
	private static final int MENU_CON = MENU_NEW + 1;
	private static final int MENU_SETTING = MENU_CON + 1;
	private static final int MENU_HELPS = MENU_SETTING + 1;
	private static final int MENU_EXIT = MENU_HELPS + 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mDbHelper = new TrackDbAdapter(this);
		mDbHelper.open();
		renderListView();
	}

	private void renderListView() {
		Log.d(TAG, "renderListView.");
		mTrackCursor = mDbHelper.getAllTracks();
		startManagingCursor(mTrackCursor);
		String[] from = new String[] { TrackDbAdapter.NAME, TrackDbAdapter.CREATED, TrackDbAdapter.DESC };
		int[] to = new int[] { R.id.name, R.id.created, R.id.desc };
		SimpleCursorAdapter tracks = new SimpleCursorAdapter(this, R.layout.track_row, mTrackCursor, from, to);
		setListAdapter(tracks);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_NEW, 0, R.string.menu_new).setIcon(R.drawable.new_track).setAlphabeticShortcut('N');
		menu.add(0, MENU_CON, 0, R.string.menu_con).setIcon(R.drawable.con_track).setAlphabeticShortcut('C');
		menu.add(0, MENU_SETTING, 0, R.string.menu_setting).setIcon(R.drawable.setting).setAlphabeticShortcut('S');
		menu.add(0, MENU_HELPS, 0, R.string.menu_helps).setIcon(R.drawable.helps).setAlphabeticShortcut('H');
		menu.add(0, MENU_EXIT, 0, R.string.menu_exit).setIcon(R.drawable.exit).setAlphabeticShortcut('E');
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case MENU_NEW:
			intent.setClass(this, NewTrack.class);
			startActivity(intent);
			return true;
		case MENU_CON:
			conTrackService();
			return true;
		case MENU_SETTING:
			intent.setClass(this, Setting.class);
			startActivity(intent);
			return true;
		case MENU_HELPS:
			intent.setClass(this, Helps.class);
			startActivity(intent);
			return true;
		case MENU_EXIT:
			finish();
			break;
		}
		return true;
	}

	private void conTrackService() {
		Intent it = new Intent("zen.rodney.itracks.START_TRACK_SERVICE");
		Long track_id = getListView().getSelectedItemId();
		it.putExtra(LocateDbAdapter.TRACKID, track_id.intValue());
		startService(it);
	}
}