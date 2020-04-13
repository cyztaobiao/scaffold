package jujube.android.demos.recyclerview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jujube.starter.R;
import com.jujube.starter.recyclerview.ItemTouchHelperCallback;
import com.jujube.starter.recyclerview.onMoveAndSwipedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tb on 2017/9/26.
 */

public class MoveSwipeRecyclerViewActivity extends AppCompatActivity {

	final int[] icons = {
			R.drawable.icon_1,
			R.drawable.icon_2,
			R.drawable.icon_3};

	final int[] colors = {
			R.color.saffron,
			R.color.eggplant,
			R.color.sienna};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RecyclerView recyclerView = new RecyclerView(this);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setHasFixedSize(true);
		RecyclerViewAdapter adapter = new RecyclerViewAdapter();
		recyclerView.setAdapter(adapter);
		ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
		ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
		mItemTouchHelper.attachToRecyclerView(recyclerView);

		setContentView(recyclerView);
	}

	public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> implements onMoveAndSwipedListener {

		private List<Integer> mItems;

		public RecyclerViewAdapter() {
			mItems = new ArrayList<>();
			for (int resId : icons)
				mItems.add(resId);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			ImageView img = (ImageView) holder.itemView.findViewById(R.id.image_view_icon);
			holder.itemView.setBackgroundResource(colors[position]);
			img.setImageResource(mItems.get(position));
		}

		@Override
		public int getItemCount() {
			return mItems.size();
		}


		@Override
		public boolean onItemMove(int fromPosition, int toPosition) {
			Collections.swap(mItems, fromPosition, toPosition);
			notifyItemMoved(fromPosition, toPosition);
			return true;
		}

		@Override
		public void onItemDismiss(int position) {
			mItems.remove(position);
			notifyItemRemoved(position);
		}

	}

	class ViewHolder extends RecyclerView.ViewHolder {

		private ViewHolder(View itemView) {
			super(itemView);

		}
	}
}
