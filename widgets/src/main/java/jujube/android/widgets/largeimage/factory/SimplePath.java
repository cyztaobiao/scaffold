package jujube.android.widgets.largeimage.factory;

import android.graphics.Paint;

import java.util.List;

import jujube.android.widgets.largeimage.Point;

/**
 * Created by tb on 2017/3/14.
 */

public class SimplePath {

	public List<Point> points;
	public Paint paint;

	public SimplePath(List<Point> points, Paint paint) {
		this.paint = paint;
		this.points = points;
	}
}
