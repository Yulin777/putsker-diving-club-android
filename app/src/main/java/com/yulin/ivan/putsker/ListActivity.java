package com.yulin.ivan.putsker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ListActivity extends android.app.ListActivity {

    ListView listView;
    FirebaseDatabase db;
    DatabaseReference ref;
    ArrayList<String> list;
    ArrayList<Guide> guideslist;
    Object selectedGuide;
    Map<String, Object> guidesMap;
    ArrayAdapter<String> adapter;
    Guide guide;
    Toolbar apptoolbar;
    String title;
    View guideModal;
    Guide currentGuide;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        title = Objects.requireNonNull(getIntent().getExtras()).getString("title");
        initToolbar();

        guide = new Guide();
        listView = (ListView) findViewById(android.R.id.list);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference().child("Guides");
        list = new ArrayList<>();
        guideslist = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.guides, R.id.guideName, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, CoursesActivity.class);
                selectedGuide = (Object) guidesMap.values().toArray()[position];
                Map<String, Object> guide = (Map<String, Object>) selectedGuide;
                String nextTitle = title + " > " + ((String) guide.get("name"));
                intent.putExtra("data", (Serializable) selectedGuide);
                intent.putExtra("title", nextTitle);

                startActivity(intent);

            }
        });
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        guidesMap = (Map<String, Object>) dataSnapshot.getValue();
                        collectGuidesData(guidesMap);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private Guide getGuideAt(int position) {
        return guideslist.get(position);
    }

    @SuppressLint("NewApi")
    public void showGuideInfo(View v) {
        final int position = getListView().getPositionForView((View) v.getParent());
        currentGuide = getGuideAt(position);
        LayoutInflater inflater = getLayoutInflater();

        guideModal = inflater.inflate(R.layout.guidepopup, null);
        TextView name = guideModal.findViewById(R.id.guideNameModal);
        TextView insurance = guideModal.findViewById(R.id.insuranceExpirationModal);
        TextView license = guideModal.findViewById(R.id.licenseExpirationModal);

        name.setText(currentGuide.name);
        insurance.setText(String.format("insurance expiration\n%s", currentGuide.insuranceExpiration));
        license.setText(String.format("license expiraition\n%s", currentGuide.licenseExpiration));

        new AlertDialog.Builder(this)
                .setView(guideModal)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                })
                .create().show();
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

    private void collectGuidesData(Map<String, Object> guides) {

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : guides.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();

            String s1 = (String) singleUser.get("licenseExpiration");
            String s2 = (String) singleUser.get("insuranceExpiration");
            Guide g = new Guide(entry.getKey(), (String) singleUser.get("name"),
                    (String) singleUser.get("email"), (Boolean) singleUser.get("senior"), s2, s1);
            guideslist.add(g);
            list.add((String) singleUser.get("name"));
        }
    }

}
