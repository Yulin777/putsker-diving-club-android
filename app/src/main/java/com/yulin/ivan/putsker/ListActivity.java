package com.yulin.ivan.putsker;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import java.util.Map;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    //ListView coursesList;
    FirebaseDatabase db;
    DatabaseReference ref;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Guide guide;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        guide = new Guide();
        listView = (ListView) findViewById(R.id.listview);
        //coursesList = (ListView) findViewById(R.id.coursesList);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference().child("Guides");
        //todo set write permissions for Guides

        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.guides, R.id.guideName, list);
        listView.setAdapter(adapter);

        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectGuidesData((Map<String, Object>) dataSnapshot.getValue());
                        adapter.notifyDataSetChanged();
                        Object o = dataSnapshot.getValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, CoursesActivity.class);
                intent.putExtra("index", position);
                //need to send courses data
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
