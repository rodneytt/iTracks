/**
 * TrackDbAdapter.java 2011-8-15 下午9:52:19
 * AII Rights Reserved
 */

package zen.rodney.itracks;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TrackDbAdapter extends DbAdapter {
	private static final String TAG = "TrackDbAdapter";

	public static final String TABLE_NAME = "tracks";

	public static final String ID = "_id";
	public static final String KEY_ROWID = "_id";
	public static final String NAME = "name";
	public static final String DESC = "desc";
	public static final String DIST = "distance";
	public static final String TRACKEDTIME = "tracked_time";
	public static final String LOCATE_COUNT = "locats_count";
	public static final String CREATED = "created_at";
	public static final String UPDATED = "updated_at";
	public static final String AVGSPEED = "avg_speed";
	public static final String MAXSPEED = "max_speed";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;

	public TrackDbAdapter(Context context) {
		this.mCtx = context;
	}

	public TrackDbAdapter open() {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public Cursor getTrack(long rowId) {
		Cursor tCur = mDb.query(true, TABLE_NAME, new String[] { KEY_ROWID, NAME, DESC, CREATED }, KEY_ROWID + "="
				+ rowId, null, null, null, null, null);
		if (tCur != null) {
			tCur.moveToFirst();
		}
		return tCur;
	}

	public long createTrack(String name, String desc) {
		Log.d(TAG, "create track");
		ContentValues initValues = new ContentValues();
		initValues.put(NAME, name);
		initValues.put(DESC, desc);
		Calendar calendar = Calendar.getInstance();
		String created = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		initValues.put(CREATED, created);
		initValues.put(UPDATED, created);
		long track_id = mDb.insert(TABLE_NAME, null, initValues);
		return track_id;
	}

	public boolean deleteTrack(long rowId) {
		return mDb.delete(TABLE_NAME, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public Cursor getAllTracks() {
		return mDb.query(TABLE_NAME, new String[] { ID, NAME, DESC, CREATED }, null, null, null, null,
				"updated_at desc");
	}

	public boolean updateTrack(long rowId, String name, String desc) {
		ContentValues vals = new ContentValues();
		vals.put(NAME, name);
		vals.put(DESC, desc);
		Calendar calendar = Calendar.getInstance();
		String updated = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		vals.put(UPDATED, updated);
		return mDb.update(TABLE_NAME, vals, KEY_ROWID + "=" + rowId, null) > 0;
	}
}
