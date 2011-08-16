/**
 * NewTrack.java 2011-8-15 下午10:33:39
 * AII Rights Reserved
 */

package zen.rodney.itracks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewTrack extends Activity {
	private static final String TAG = "NewTrack";
	private Button button_new;
	private EditText field_new_name;
	private EditText field_new_desc;

	private TrackDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_track);
		setTitle(R.string.menu_new);

		findViews();
		setListeners();

		mDbHelper = new TrackDbAdapter(this);
		mDbHelper.open();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onstop");
		mDbHelper.close();
	}

	public void findViews() {
		Log.d(TAG, "find Views");
		field_new_name = (EditText) findViewById(R.id.new_name);
		field_new_desc = (EditText) findViewById(R.id.new_desc);
		button_new = (Button) findViewById(R.id.new_submit);
	}

	public void setListeners() {
		button_new.setOnClickListener(new_track);
	}

	private OnClickListener new_track = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String name = field_new_name.getText().toString();
			String desc = field_new_desc.getText().toString();
			if ("".equals(name)) {
				Toast.makeText(getApplicationContext(), getString(R.string.new_name_null), Toast.LENGTH_SHORT).show();
			} else {
				long row_id = mDbHelper.createTrack(name, desc);
				Log.d(TAG, "rowId:" + row_id);

				Intent it = new Intent();
				it.setClass(getApplicationContext(), ShowTrack.class);
				it.putExtra(TrackDbAdapter.KEY_ROWID, row_id);
				it.putExtra(TrackDbAdapter.NAME, name);
				it.putExtra(TrackDbAdapter.DESC, desc);
				startActivity(it);
			}
		}
	};
}
