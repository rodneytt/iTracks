/**
 * LocateOverLay.java 2011-8-17 下午10:21:34
 * AII Rights Reserved
 */

package zen.rodney.itracks;

import java.util.ArrayList;

import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class LocateOverLay extends ItemizedOverlay<OverlayItem> {
	public LocateOverLay(Drawable defaultMarker, Cursor mLocatesCursor) {
		super(defaultMarker);
		geoList = new ArrayList<GeoPoint>();
		Cursor c = mLocatesCursor;
		while (c.moveToNext()) {
			GeoPoint gpt = new GeoPoint((int) (c.getDouble(3) * 1000000), (int) (c.getDouble(2) * 1000000));
			geoList.add(gpt);
		}
		populate();
	}

	private static final String TAG = "ShowTrack";
	private ArrayList<GeoPoint> geoList = null;

	@Override
	protected OverlayItem createItem(int i) {
		OverlayItem item = new OverlayItem(geoList.get(i), "", "");

		return item;
	}

	@Override
	public int size() {
		return geoList.size();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();
		OverlayItem endOverlayItem = null;
		for (int i = size() - 1; i >= 0; i--) {
			OverlayItem startOverlayItem = getItem(i);
			Point starPoint = projection.toPixels(startOverlayItem.getPoint(), null);
			Paint paintCircle = new Paint();
			paintCircle.setColor(Color.YELLOW);
			canvas.drawCircle(starPoint.x, starPoint.y, 2, paintCircle);
			if (endOverlayItem != null) {
				Paint paintLine = new Paint();
				paintLine.setColor(Color.RED);
				paintLine.setStyle(Style.STROKE);
				Point endPoint = projection.toPixels(endOverlayItem.getPoint(), null);
				canvas.drawLine(starPoint.x, starPoint.y, endPoint.x, endPoint.y, paintLine);
			}
			endOverlayItem = startOverlayItem;
		}
		super.draw(canvas, mapView, shadow);

	}

}
