package com.cisetech.swipemenudemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cisetech.swipemenudemo.pulltorefresh.library.PullToRefreshBase;
import com.cisetech.swipemenudemo.pulltorefresh.library.PullToRefreshBase.Mode;
import com.cisetech.swipemenudemo.pulltorefresh.library.PullToRefreshScrollView;
import com.cisetech.swipemenudemo.swipemenu.library.SwipeMenu;
import com.cisetech.swipemenudemo.swipemenu.library.SwipeMenuCreator;
import com.cisetech.swipemenudemo.swipemenu.library.SwipeMenuItem;
import com.cisetech.swipemenudemo.swipemenu.library.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

public class SimpleActivity extends Activity implements PullToRefreshBase.OnRefreshListener<ScrollView> {

	private List<ApplicationInfo> mAppList;
	private List<ApplicationInfo> mAppList2=new ArrayList<ApplicationInfo>();
	private AppAdapter mAdapter;
	private SwipeMenuListView mListView;
	private PullToRefreshScrollView id_pr;
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			mAppList2.addAll(mAppList.subList(0,10));
			mAdapter = new AppAdapter();
			mListView.setAdapter(mAdapter);
			dialog.dismiss();
		}

		;
	};
	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		dialog=ProgressDialog.show(SimpleActivity.this,"","加载中，请稍后...");
		new Thread(){
			public void run() {
				mAppList = getPackageManager().getInstalledApplications(0);
				handler.sendEmptyMessage(0);
			};
		}.start();
		mListView = (SwipeMenuListView) findViewById(R.id.listView);
		id_pr=(PullToRefreshScrollView) findViewById(R.id.id_pr);
		id_pr.setMode(Mode.BOTH);
		id_pr.setOnRefreshListener(this);
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				// set item width
				openItem.setWidth(dp2px(90));
				// set item title
				openItem.setTitle("Open");
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		mListView.setMenuCreator(creator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				ApplicationInfo item = mAppList.get(position);
				switch (index) {
				case 0:
					// open
					open(item);
					break;
				case 1:
					// delete
//					delete(item);
					mAppList.remove(position);
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
		});
		
		// set SwipeListener
		mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
			
			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}
			
			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		// other setting
//		listView.setCloseInterpolator(new BounceInterpolator());
		
		// test item long click
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(), position + " long click", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		
	}

	private void delete(ApplicationInfo item) {
		// delete app
		try {
			Intent intent = new Intent(Intent.ACTION_DELETE);
			intent.setData(Uri.fromParts("package", item.packageName, null));
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	private void open(ApplicationInfo item) {
		// open app
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(item.packageName);
		List<ResolveInfo> resolveInfoList = getPackageManager()
				.queryIntentActivities(resolveIntent, 0);
		if (resolveInfoList != null && resolveInfoList.size() > 0) {
			ResolveInfo resolveInfo = resolveInfoList.get(0);
			String activityPackageName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName componentName = new ComponentName(
					activityPackageName, className);

			intent.setComponent(componentName);
			startActivity(intent);
		}
	}

	class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAppList2.size();
		}

		@Override
		public ApplicationInfo getItem(int position) {
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.e("Simple", "getView: ");
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_list_app, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			ApplicationInfo item = getItem(position);
			holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
			holder.tv_name.setText(item.loadLabel(getPackageManager()));
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView tv_name;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				view.setTag(this);
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	@Override
	public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mAppList2.addAll(mAppList.subList(mAppList2.size(),mAppList2.size()+10));
				mAdapter.notifyDataSetChanged();
				id_pr.onRefreshComplete(true);
			}
		}, 1000);
	}
}
