package com.yulin.ivan.putsker;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class CoursesActivity extends AppCompatActivity implements Serializable{

    ListView listView;
    ArrayList<String> list;
    ArrayList<Course> courses;
    Map<String, Object> m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        m = (Map<String, Object>)getIntent().getSerializableExtra("data");
        courses = new ArrayList<Course>();

        String[] course_names = getResources().getStringArray(R.array.courses_names);
        TypedArray course_pics = getResources().obtainTypedArray(R.array.courses_pics);

        for (int i = 0; i < course_names.length; i++) {
            Course item = new Course(course_names[i],
                    course_pics.getResourceId(i, -1));
            courses.add(item);
        }

        listView = (ListView) findViewById(R.id.coursesList);
        CustomCoursesAdapter adapter = new CustomCoursesAdapter(this, courses);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CoursesActivity.this, ClassActivity.class);
                Course c = (Course) listView.getItemAtPosition(position);
                Object course = (Object) m.get(c.getCourseName());
                intent.putExtra("classes", (Serializable) course);
                startActivity(intent);

            }
        });

    }


}
