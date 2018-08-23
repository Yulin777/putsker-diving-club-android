package com.yulin.ivan.putsker;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> list;
    ArrayList<Student> students;
    Map<String, Object> m;
    ArrayList<Object> arr;
    Toolbar apptoolbar;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        arr = (ArrayList<Object>)getIntent().getSerializableExtra("group");
        title = getIntent().getExtras().getString("title");
        students = new ArrayList<Student>();
        initToolbar();

        for (int i = 1; i < arr.size(); i++) {
            m = (Map<String, Object>) arr.get(i);
            Student item = new Student((String) m.get("name"), (String) m.get("phone"), (Boolean)m.get("hasGear"));
            students.add(item);
        }

        listView = (ListView) findViewById(R.id.StudentsList);
        CustomStudentAdapter adapter = new CustomStudentAdapter(this, students);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(StudentActivity.this, ClassActivity.class);
//                Student s = (Student) listView.getItemAtPosition(position);
//                Object student = (Object) m.get(s.getCourseName());
//                intent.putExtra("isCourse", true);
//                intent.putExtra("courseName", s.getCourseName());
//                intent.putExtra("classes", (Serializable) course);
//                startActivity(intent);

            }
        });

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
