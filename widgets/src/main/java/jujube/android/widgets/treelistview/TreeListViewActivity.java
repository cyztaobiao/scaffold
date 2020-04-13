package jujube.android.widgets.treelistview;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import jujube.android.widgets.R;
import jujube.android.widgets.treelistview.bean.NodeBean;
import jujube.android.widgets.treelistview.tree.Node;
import jujube.android.widgets.treelistview.tree.TreeListViewAdapter;

/**
 * Created by tb on 2017/11/2.
 */

public class TreeListViewActivity extends AppCompatActivity {

	//标记是显示Checkbox还是隐藏
	private boolean isHide = true;
	private LocalTreeListViewAdapter<NodeBean> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= 23) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}
		setContentView(R.layout.sample_tree_list_view);

		final List<NodeBean> data = initData();
		final ListView treeLv = (ListView) findViewById(R.id.tree_list_view);
		final Button checkSwitchBtn = (Button)this.findViewById(R.id.check_switch_btn);


		checkSwitchBtn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				isHide = !isHide;
				adapter.updateView(isHide);
			}

		});
		try {
			adapter = new LocalTreeListViewAdapter<>(treeLv, this,
					data, 10, isHide);

			adapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
				@Override
				public void onClick(Node node, int position) {
//					if (node.isLeaf()) {
//						Toast.makeText(getApplicationContext(), node.getName(),
//								Toast.LENGTH_SHORT).show();
//					}
				}
				@Override
				public void onCheckChange(Node node, int position,
										  List<Node> checkedNodes) {
					// TODO Auto-generated method stub

					StringBuilder sb = new StringBuilder();
					for (Node n : checkedNodes) {
						int pos = n.getId() - 1;
						sb.append(data.get(pos).getName()).append("---")
								.append(pos + 1).append(";");

					}
					Toast.makeText(getApplicationContext(), sb.toString(),
							Toast.LENGTH_SHORT).show();
				}

			});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		treeLv.setAdapter(adapter);
	}

	private List<NodeBean> initData() {
		List<NodeBean> data = new ArrayList<>();
		data.add(new NodeBean(1, 0, "中国古代"));
		data.add(new NodeBean(2, 1, "唐朝"));
		data.add(new NodeBean(3, 1, "宋朝"));
		data.add(new NodeBean(4, 1, "明朝"));
		data.add(new NodeBean(5, 2, "李世民"));
		data.add(new NodeBean(6, 2, "李白"));

		data.add(new NodeBean(7, 3, "赵匡胤"));
		data.add(new NodeBean(8, 3, "苏轼"));

		data.add(new NodeBean(9, 4, "朱元璋"));
		data.add(new NodeBean(10, 4, "唐伯虎"));
		data.add(new NodeBean(11, 4, "文征明"));
		data.add(new NodeBean(12, 7, "赵建立"));
		data.add(new NodeBean(13, 8, "苏东东"));
		data.add(new NodeBean(14, 10, "秋香"));

		return data;
	}
}
