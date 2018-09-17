package com.yulin.ivan.putsker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
        adapter = new ArrayAdapter<String>(this, R.layout.groups, R.id.group_name, list);
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
        (findViewById(R.id.login_progress)).setVisibility(View.INVISIBLE);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
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
//                    if (subcourseAreDates(subcourse))
//                        subcourse = sortByDate((ArrayList<ArrayList>) subcourse);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList sortByDate(ArrayList<ArrayList> subcourse) {
        Collections.sort(subcourse, new Comparator<ArrayList>() {
            int flag = 1;

            @Override
            public int compare(ArrayList o1, ArrayList o2) {
                System.err.println("\t\t\t\tIN COMPARE " + flag++);
                Date d1 = null;
                Date d2 = null;
                if (o1 == null || o2 == null) {
                    System.err.println("\t\t\t\tRETURNED 0: " + (o1 == null ? "o1" : "o2"));
                    return 0;
                }
                try {
                    d1 = (new SimpleDateFormat("dd.MM.yy")).parse(((String) o1.get(0)).trim());
                    d2 = (new SimpleDateFormat("dd.MM.yy")).parse(((String) o2.get(0)).trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return d1.after(d2) ? 1 : -1;
            }
        });
        return subcourse;
    }

    private boolean subcourseAreDates(Object subcourse) {
        try {
            String firstTitle = (String) ((ArrayList) ((ArrayList) subcourse).get(1)).get(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
            dateFormat.setLenient(false);

            dateFormat.parse(firstTitle.trim());
        } catch (Exception pe) {
            return false;
        }
        return true;
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
        TextView _title = findViewById(R.id.toolbar_title);
        _title.setText(title);
        _title.setSelected(true);
        apptoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_right_white));
        apptoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void sendGroupMessage(View v) {
        ArrayList<Object> clickedGroup = new ArrayList<>();
        clickedGroup = (ArrayList<Object>) groups.get((Integer) v.getTag());
        ArrayList<String> clickedGroupNames = getClickedGroupNames(clickedGroup);
        String namesToSingleString = joinNames(clickedGroupNames);
        ArrayList<String> clickedGroupPhones = getClickedGroupPhones(clickedGroup);
        final String phonesToSingleString = joinPhones(clickedGroupPhones);

        new AlertDialog.Builder(this)
                .setTitle("Send Message to this group")
                .setMessage("recipients:\n" + namesToSingleString)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phonesToSingleString));
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private String joinPhones(ArrayList<String> clickedGroupPhones) {
        StringBuilder listString = new StringBuilder();

        for (String s : clickedGroupPhones)
            listString.append(s).append(";");

        return listString.toString();
    }

    private String joinNames(ArrayList<String> clickedGroupNames) {
        StringBuilder listString = new StringBuilder();

        for (String s : clickedGroupNames)
            listString.append(s).append("\n");

        return listString.toString();
    }

    private ArrayList<String> getClickedGroupPhones(ArrayList<Object> clickedGroup) {
        boolean firstFlag = true;
        int oneOrZero = 1;
        ArrayList<String> temp = new ArrayList<String>();
        for (Object student : clickedGroup) {
            if (firstFlag) {
                firstFlag = false;
                continue;
            }
            String phoneOrName = ((HashMap) student).values().toArray()[1].toString().replaceAll("-", "");
            if (phoneOrName.matches("[0-9]+") && phoneOrName.length() > 2) {
                oneOrZero = 1;
            } else oneOrZero = 0;
            temp.add(((HashMap) student).values().toArray()[oneOrZero].toString().replaceAll("-", ""));
        }
        return temp;
    }

    private ArrayList<String> getClickedGroupNames(ArrayList<Object> clickedGroup) {
        boolean firstFlag = true;
        int oneOrZero = 0;
        ArrayList<String> temp = new ArrayList<String>();
        for (Object student : clickedGroup) {
            if (firstFlag) {
                firstFlag = false;
                continue;
            }
            String phoneOrName = ((HashMap) student).values().toArray()[1].toString().replaceAll("-", "");
            if (phoneOrName.matches("[0-9]+") && phoneOrName.length() > 2) {
                oneOrZero = 0;
            } else oneOrZero = 1;

            temp.add(((HashMap) student).values().toArray()[oneOrZero].toString());
        }
        return temp;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

                holder.group_name = (TextView) convertView.findViewById(R.id.group_name);
                holder.messageIcon = convertView.findViewById(R.id.message_in_a_bottle);
                try {
                    holder.group_name.setText((String) ((ArrayList) rowItems.get(position + 1)).get(0));
                    holder.messageIcon.setVisibility(View.VISIBLE);
                    holder.messageIcon.setTag(position + 1);
                } catch (Exception e) {
                    /* note: sometimes app crashes at last index. perhaps when list is smaller then the screen could not figure out why :( */
                }
                convertView.setTag(holder);
            } else {
                holder = (CustomClassAdapter.ViewHolder) convertView.getTag();
            }
            holder.messageIcon = convertView.findViewById(R.id.message_in_a_bottle);

            return convertView;
        }

    }
}
