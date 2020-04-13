package jujube.android.widgets.phoenix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import jujube.android.widgets.R;

public class PhoenixActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample_phoenix);

		final int[] icons = {
				R.drawable.icon_1,
				R.drawable.icon_2,
				R.drawable.icon_3};

		final int[] colors = {
				R.color.saffron,
				R.color.eggplant,
				R.color.sienna};

		final PullToRefreshLayout pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.pull_refresh_layout);
		pullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				pullToRefreshLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						pullToRefreshLayout.setRefreshing(false);
					}
				}, 1200);
			}
		});

		RecyclerView recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(new RecyclerView.Adapter() {
			@Override
			public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.item_image, parent, false);
				return new ViewHolder(view);
			}

			@Override
			public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
				ImageView img = (ImageView) holder.itemView.findViewById(R.id.image_view_icon);
				holder.itemView.setBackgroundResource(colors[position]);
				img.setImageResource(icons[position]);
			}

			@Override
			public int getItemCount() {
				return colors.length;
			}
		});

	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View itemView) {
			super(itemView);
		}
	}
}
