package jujube.android.widgets.xfermode;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import jujube.android.widgets.R;

/**
 * Created by tb on 2017/11/2.
 */

public class XfermodeActivity extends AppCompatActivity {

	XfermodeView xfermodeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample_xfermode);
		xfermodeView = (XfermodeView) findViewById(R.id.xfermode_view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.xfermode, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = item.getTitle().toString();
		setTitle(title);
		xfermodeView.setXfermode(PorterDuff.Mode.valueOf(title));
		return super.onOptionsItemSelected(item);
	}
}
