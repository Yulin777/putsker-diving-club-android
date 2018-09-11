package com.yulin.ivan.putsker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassActivity extends ListActivity implements Serializable {

    ListView listView;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Map<String, Object> classes;
    ArrayList<Object> groups;
    String courseName;
    Boolean isCourse;
    Toolbar apptoolbar;
    String title;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        listView = (ListView) findViewById(android.R.id.list);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.groups, R.id.groupName, list);
        listView.setAdapter(adapter);

        isCourse = getIntent().getExtras().getBoolean("isCourse");
        courseName = getIntent().getExtras().getString("courseName");
        title = getIntent().getExtras().getString("title");


        initToolbar();
        if (isCourse) {
            classes = (Map<String, Object>) getIntent().getSerializableExtra("classes");
            initializeClassesList();
        } else {
            groups = (ArrayList<Object>) getIntent().getSerializableExtra("classes");
            setListAdapter(new CustomClassAdapter(this, groups));

            initializeGroupsList();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nextTitle;
                if (isCourse) { // class is clicked
                    Intent intent = new Intent(ClassActivity.this, ClassActivity.class);
                    String name = listView.getItemAtPosition(position).toString();
                    Object subcourse = (Object) classes.get(name);
                    nextTitle = title + " > " + name;
                    if (courseName.equals("star1"))
                        intent.putExtra("isCourse", true);
                    else
                        intent.putExtra("isCourse", false);
                    intent.putExtra("courseName", name);
                    intent.putExtra("classes", (Serializable) subcourse);
                    intent.putExtra("title", nextTitle);
                    if (nextTitle.length() - nextTitle.replaceAll(">", "").length() == 2) {
                        intent.putExtra("showMessageIcon", true);

                    }
                    startActivity(intent);
                } else { // group is clicked
                    Intent intent = new Intent(ClassActivity.this, StudentActivity.class);
                    Object group = (Object) groups.get(position + 1);
                    nextTitle = title + " > " + ((ArrayList<Object>) group).get(0);
                    intent.putExtra("title", nextTitle);
                    intent.putExtra("group", (Serializable) group);
                    startActivity(intent);
                }
            }
        });

    }

    private void initializeClassesList() {
        for (Map.Entry<String, Object> entry : classes.entrySet()) {
            list.add(entry.getKey());
        }
    }

    private void initializeGroupsList() {
        for (int i = 1; i < groups.size(); i++) {
            ArrayList<Object> arr = (ArrayList<Object>) groups.get(i);
            list.add((String) arr.get(0));
        }
    }

    private void initToolbar() {
        apptoolbar = findViewById(R.id.apptoolbar);
        apptoolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        apptoolbar.setTitle(title);
        apptoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_right_white));
        apptoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public class CustomClassAdapter extends BaseAdapter {

        Context context;
        List<Object> rowItems;

        CustomClassAdapter(Context context, List<Object> rowItems) {
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
            TextView group_name;
            ImageButton messageIcon;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomClassAdapter.ViewHolder holder = null;
            Object row_pos;
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.groups, null);
                holder = new CustomClassAdapter.ViewHolder();

                holder.group_name = (TextView) convertView.findViewById(R.id.groupName);
                holder.messageIcon = convertView.findViewById(R.id.messageinabottle);
                if (rowItems.get(position + 1) != null) {
                    holder.group_name.setText((String) ((ArrayList) rowItems.get(position + 1)).get(0));
                    holder.messageIcon.setVisibility(View.VISIBLE);
                }
                convertView.setTag(holder);
            } else {
                holder = (CustomClassAdapter.ViewHolder) convertView.getTag();
            }
            holder.messageIcon = convertView.findViewById(R.id.messageinabottle);

            return convertView;
        }

    }
}
