package jujube.android.widgets.indicator;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import jujube.android.widgets.R;

/**
 * Created by tb on 2017/10/16.
 */

public class IndicatorActivity extends AppCompatActivity {

	private PagerAdapter mAdapter;
	ViewPager mPager;
	InkPageIndicator mIndicator;
	XInkPageIndicator xIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample_indicator);

		mAdapter = new PagerAdapter(getSupportFragmentManager());

		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		xIndicator = findViewById(R.id.x_indicator);
		xIndicator.setViewPager(mPager);
	}

	private static class PagerAdapter extends FragmentPagerAdapter {
		protected static final String[] CONTENT = new String[]{"This", "Is", "A", "Test",};

		private int mCount = CONTENT.length;

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return PagerAdapter.CONTENT[position % CONTENT.length];
		}

		public void setCount(int count) {
			if (count > 0 && count <= 10) {
				mCount = count;
				notifyDataSetChanged();
			}
		}

		public static final class TestFragment extends Fragment {
			private static final String KEY_CONTENT = "TestFragment:Content";

			public static TestFragment newInstance(String content) {
				TestFragment fragment = new TestFragment();

				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < 20; i++) {
					builder.append(content).append(" ");
				}
				builder.deleteCharAt(builder.length() - 1);
				fragment.mContent = builder.toString();

				return fragment;
			}

			private String mContent = "???";

			@Override
			public void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);

				if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
					mContent = savedInstanceState.getString(KEY_CONTENT);
				}
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				TextView text = new TextView(getActivity());
				text.setGravity(Gravity.CENTER);
				text.setText(mContent);
				text.setTextSize(20 * getResources().getDisplayMetrics().density);
				text.setPadding(20, 20, 20, 20);

				LinearLayout layout = new LinearLayout(getActivity());
				layout.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
				layout.setGravity(Gravity.CENTER);
				layout.addView(text);

				return layout;
			}

			@Override
			public void onSaveInstanceState(Bundle outState) {
				super.onSaveInstanceState(outState);
				outState.putString(KEY_CONTENT, mContent);
			}
		}
	}

}
