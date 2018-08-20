package com.yulin.ivan.putsker;

import android.annotation.SuppressLint;
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

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        classes = (Map<String, Object>)getIntent().getSerializableExtra("classes");
        listView = (ListView) findViewById(R.id.classesList);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        initializeGroupsList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(ClassActivity.this, CoursesActivity.class);
//                startActivity(intent);

            }
        });

    }

    private void initializeGroupsList() {
        for (Map.Entry<String, Object> entry : classes.entrySet()) {
            list.add(entry.getKey());
        }
    }
}
