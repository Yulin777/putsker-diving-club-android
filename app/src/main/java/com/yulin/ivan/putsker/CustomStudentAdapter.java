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
 * Created by tyizchak on 8/21/2018.
 */

public class CustomStudentAdapter extends BaseAdapter {


    Context context;
    List<Student> rowItems;

    CustomStudentAdapter(Context context, List<Student> rowItems) {
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
        TextView student_name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomStudentAdapter.ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.sudents, null);
            holder = new CustomStudentAdapter.ViewHolder();

            holder.student_name = (TextView) convertView
                    .findViewById(R.id.studentName);

            Student row_pos = rowItems.get(position);

            holder.student_name.setText(row_pos.getName());

            convertView.setTag(holder);
        } else {
            holder = (CustomStudentAdapter.ViewHolder) convertView.getTag();
        }

        return convertView;    }
}
