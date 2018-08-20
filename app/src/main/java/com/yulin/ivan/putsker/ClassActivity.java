package com.yulin.ivan.putsker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class ClassActivity extends AppCompatActivity implements Serializable{

    ListView listView;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Map<String, Object> classes;
    ArrayList<Object> groups;
    String courseName;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        listView = (ListView) findViewById(R.id.classesList);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        courseName = getIntent().getExtras().getString("courseName");
        if(getIntent().getExtras().getBoolean("isCourse")){
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

                //show star1 sub-courses
//                if(courseName.equals("star1")){
                    Intent intent = new Intent(ClassActivity.this, ClassActivity.class);
                    String name = listView.getItemAtPosition(position).toString();
                    Object subcourse = (Object) classes.get(name);
                    if(courseName.equals("star1"))
                        intent.putExtra("isCourse", true);
                    else
                        intent.putExtra("isCourse", false);
                    intent.putExtra("courseName", name);
                    intent.putExtra("classes", (Serializable) subcourse);
                    startActivity(intent);
//                }
//                else{ //open groups view
//
//                }
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

}
