package com.yulin.ivan.putsker;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class CoursesActivity extends AppCompatActivity implements Serializable{

    ListView listView;
    ArrayList<String> list;
    ArrayList<Course> courses;
    Map<String, Object> selectedGuide;
    Toolbar apptoolbar;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        selectedGuide = (Map<String, Object>)getIntent().getSerializableExtra("data");
        courses = new ArrayList<Course>();
        title = getIntent().getExtras().getString("title");
        initToolbar();

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
                String courseName="";
                switch (c.getCourseName()){
                    case "כוכב 1":
                        courseName = "star1";
                        break;
                    case "כוכב 2":
                        courseName = "star2";
                        break;
                    case "נייטרוקס":
                        courseName = "nitrox";
                        break;
                }
                Object course = (Object) selectedGuide.get(courseName);
                String nextTitle = title + " > " + c.getCourseName();
                intent.putExtra("title", nextTitle);
                intent.putExtra("isCourse", true);
                intent.putExtra("courseName", courseName);
                intent.putExtra("classes", (Serializable) course);
                startActivity(intent);

            }
        });

    }

    private void initToolbar() {
        apptoolbar = findViewById(R.id.apptoolbar);
//        apptoolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
//        apptoolbar.setTitle(title);
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
}
