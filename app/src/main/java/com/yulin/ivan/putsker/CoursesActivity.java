package com.yulin.ivan.putsker;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class CoursesActivity extends AppCompatActivity {

    ListView listView;
    FirebaseDatabase db;
    DatabaseReference ref;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ArrayList<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

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

//        //coursesList = (ListView) findViewById(R.id.coursesList);
//        db = FirebaseDatabase.getInstance();
//        ref = db.getReference().child("Guides");

//        String[] courses = new String[] {
//                "One Star",
//                "Two Stars",
//                "Nitrox",
//        };
//        list = new ArrayList<String>(Arrays.asList(courses));
//        adapter = new ArrayAdapter<String>(this, R.layout.courses, R.id.courseName, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

    }

    private void collectGuidesData(Map<String, Object> guides) {

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : guides.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            list.add((String) singleUser.get("name"));
        }

    }

}
