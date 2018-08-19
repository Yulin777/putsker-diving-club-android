package com.yulin.ivan.putsker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tyizchak on 8/18/2018.
 */

public class CustomCoursesAdapter extends BaseAdapter {

    Context context;
    List<Course> rowItems;

    CustomCoursesAdapter(Context context, List<Course> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView course_pic;
        TextView course_name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.courses, null);
            holder = new ViewHolder();

            holder.course_name = (TextView) convertView
                    .findViewById(R.id.courseName);
            holder.course_pic = (ImageView) convertView
                    .findViewById(R.id.coursePic);

            Course row_pos = rowItems.get(position);

            holder.course_pic.setImageResource(row_pos.getImageSrc());
            holder.course_name.setText(row_pos.getCourseName());

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;    }
}
