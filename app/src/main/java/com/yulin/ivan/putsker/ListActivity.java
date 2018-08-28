package com.yulin.ivan.putsker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ListActivity extends android.app.ListActivity {

    ListView listView;
    FirebaseDatabase db;
    DatabaseReference ref;
    ArrayList<String> list;
    ArrayList<Guide> guideslist;
    Object o;
    Map<String, Object> m;
    ArrayAdapter<String> adapter;
    Guide guide;
    private FirebaseUser mUser;
    Toolbar apptoolbar;
    String title;

    Dialog dialog;
    ProgressDialog pg;
    View guideModal;
    ImageView guideImageModal;
    Guide currentGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        title = getIntent().getExtras().getString("title");
        initToolbar();

        guide = new Guide();
        listView = (ListView) findViewById(android.R.id.list);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference().child("Guides");
        //todo set write permissions for Guides

        list = new ArrayList<>();
        guideslist = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.guides, R.id.guideName, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, CoursesActivity.class);
                o = (Object) m.values().toArray()[position];
                Map<String, Object> guide = (Map<String, Object>) o;
                String nextTitle = title + " > " + ((String) guide.get("name"));
                intent.putExtra("data", (Serializable) o);
                intent.putExtra("title", nextTitle);

//                Toast.makeText(ListActivity.this, "clicked!",
//                        Toast.LENGTH_SHORT).show();

                startActivity(intent);

            }
        });
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        m = (Map<String, Object>) dataSnapshot.getValue();
                        collectGuidesData(m);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


    }

    private Guide getGuideAt(int position){
        return guideslist.get(position);
    }

    public void showGuideInfo(View v){
        final int position = getListView().getPositionForView((View) v.getParent());
        currentGuide = getGuideAt(position);

        LayoutInflater inflater = getLayoutInflater();
        guideModal = inflater.inflate(R.layout.guidepopup, null);
        TextView name = guideModal.findViewById(R.id.guideNameModal);
//        TextView phone = guideModal.findViewById(R.id.studentPhoneModal);
//        TextView gear = guideModal.findViewById(R.id.hasGearModal);
//        guideImageModal = guideModal.findViewById(R.id.studentImageModal);
//
        name.setText(currentGuide.getFirstName() + " " + currentGuide.getLastName());
//        phone.setText(currentGuide.getPhone());
//        gear.setText((currentGuide.getHasGear() ? "Has " : "Does not have ") + "Gear");
//        setStudentImageModal();

        new AlertDialog.Builder(this)
                .setView(guideModal)
//                .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//
//                    }
//                })
                .create().show();

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

    private void collectGuidesData(Map<String, Object> guides) {

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : guides.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();

            String s1 = (String) singleUser.get("licenseExpiration");
            String s2 = (String) singleUser.get("insuranceExpiration");
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");
            Date d1 = null, d2 = null;
            try {
                d1 = format.parse(s1);
                d2 = format.parse(s2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Guide g = new Guide(entry.getKey(), (String) singleUser.get("name"),(String) singleUser.get("lastName"),
                    (String) singleUser.get("email"), (Boolean) singleUser.get("senior"), d1, d2);
            guideslist.add(g);
            list.add((String) singleUser.get("name"));
        }
    }

}
