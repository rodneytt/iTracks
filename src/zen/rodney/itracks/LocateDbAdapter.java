/**
 * LocateDbAdapter.java 2011-8-15 下午10:08:44
 * AII Rights Reserved
 */

package zen.rodney.itracks;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocateDbAdapter extends DbAdapter {
	private static final String TAG = "LocateDbAdapter";
	public static final String TABLE_NAME = "locates";

	public static final String ID = "_id";
	public static final String TRACKID = "track_id";
	public static final String LON = "longitude";
	public static final String LAT = "latitude";
	public static final String ALT = "altitude";
	public static final String CREATED = "created_at";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;

	public LocateDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public LocateDbAdapter open() {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public Cursor getLocate(long rowId) {
		Cursor tCur = mDb.query(true, TABLE_NAME, new String[] { ID, LON, LAT, ALT, CREATED }, ID + "=" + rowId, null,
				null, null, null, null);
		if (tCur != null) {
			tCur.moveToFirst();
		}
		return tCur;
	}

	public long createLocate(int track_id, double longitude, double latitude, double altitude) {
		Log.d(TAG, "create locate");
		ContentValues initValues = new ContentValues();
		initValues.put(TRACKID, track_id);
		initValues.put(LON, longitude);
		initValues.put(LAT, latitude);
		initValues.put(ALT, altitude);
		Calendar calendar = Calendar.getInstance();
		String created = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		initValues.put(CREATED, created);
		return mDb.insert(TABLE_NAME, null, initValues);
	}

	public boolean deleteLocate(long rowId) {
		return mDb.delete(TABLE_NAME, ID + "=" + rowId, null) > 0;
	}

	public Cursor getTrackAllLocates(int trackId) {
		return mDb.query(TABLE_NAME, new String[] { ID, TRACKID, LON, LAT, ALT, CREATED }, "track_id=?",
				new String[] { String.valueOf(trackId) }, null, null, "created_at asc");
	}
}
