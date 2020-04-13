package jujube.android.widgets;

import android.content.Context;

/**
 * Created by tb on 2017/9/22.
 */

public class Utils {

	public static int convertDpToPixel(Context context, int dp) {
		float density = context.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}

}
