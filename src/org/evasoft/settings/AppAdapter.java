package org.evasoft.settings;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<AppItem> apps;

	public AppAdapter(Context context, List<AppItem> appItem) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(this.mContext);
		this.apps = appItem;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getCount() {
		return this.apps.size();
	}

	@Override
	public Object getItem(int pos) {
		return this.apps.get(pos);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		View rowView = view;
		if (rowView == null) {
			rowView = this.mInflater.inflate(R.layout.row_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) rowView.findViewById(R.id.item_icon);
			holder.name = (TextView) rowView.findViewById(R.id.item_text);
			holder.versionName = (TextView) rowView.findViewById(R.id.item_version);
			holder.size = (TextView) rowView.findViewById(R.id.item_size);
			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}

		holder.icon.setImageDrawable(apps.get(position).icon);
		holder.name.setText(apps.get(position).name);
		holder.versionName.setText("Version:" + apps.get(position).version_name);
		holder.size.setText((apps.get(position).size));

		return rowView;
	}

	private class ViewHolder {
		private ImageView icon;
		private TextView name;
		private TextView versionName;
		private TextView size;
	}
}
