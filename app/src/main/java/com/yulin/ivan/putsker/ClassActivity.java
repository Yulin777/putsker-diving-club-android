package com.yulin.ivan.putsker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class ClassActivity extends AppCompatActivity implements Serializable{

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

        listView = (ListView) findViewById(R.id.classesList);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        isCourse = getIntent().getExtras().getBoolean("isCourse");
        courseName = getIntent().getExtras().getString("courseName");
        title = getIntent().getExtras().getString("title");
        initToolbar();
        if(isCourse){
            classes = (Map<String, Object>)getIntent().getSerializableExtra("classes");
            initializeClassesList();
        }
        else{
            groups = (ArrayList<Object>)getIntent().getSerializableExtra("classes");
            initializeGroupsList();
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nextTitle;
                if(isCourse){ // class is clicked
                    Intent intent = new Intent(ClassActivity.this, ClassActivity.class);
                    String name = listView.getItemAtPosition(position).toString();
                    Object subcourse = (Object) classes.get(name);
                    nextTitle = title + "/" + name;
                    if(courseName.equals("star1"))
                        intent.putExtra("isCourse", true);
                    else
                        intent.putExtra("isCourse", false);
                    intent.putExtra("courseName", name);
                    intent.putExtra("classes", (Serializable) subcourse);
                    intent.putExtra("title", nextTitle);
                    startActivity(intent);
                }
                else { // group is clicked
                    Intent intent = new Intent(ClassActivity.this, StudentActivity.class);
//                    ArrayList<Object> group = (ArrayList<Object>) groups.get(position+1);
                    Object group = (Object) groups.get(position+1);
                    nextTitle = title + "/" + ((ArrayList<Object>) group).get(0);
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
        for(int i=1; i<groups.size(); i++){
            ArrayList<Object> arr = (ArrayList<Object>) groups.get(i);
            list.add((String) arr.get(0));
        }
//        for (Object entry : groups) {
//            Object e = entry;
////            list.add(groups.);
//        }
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

}
