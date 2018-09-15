package com.yulin.ivan.putsker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListActivity extends android.app.ListActivity {

    ListView listView;
    FirebaseDatabase db;
    DatabaseReference ref;
    ArrayList<String> list;
    ArrayList<Guide> guideslist;
    Map<String, Object> m;
    ArrayList<Object> arr;
    Object selectedGuide;
    Map<String, Object> guidesMap;
    ArrayAdapter<String> adapter;
    Guide guide;
    Toolbar apptoolbar;
    String title;
    View guideModal;
    Guide currentGuide;
    private StorageReference mStorageRef;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        title = Objects.requireNonNull(getIntent().getExtras()).getString("title");
        initToolbar();

        guide = new Guide();
        listView = (ListView) findViewById(android.R.id.list);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference().child("Guides");
        list = new ArrayList<>();
        guideslist = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.guides, R.id.guide_name, list);
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

                        initGuidesList();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }


                });

    }


    private void initGuidesList() {
        ArrayList<Guide> guidesArrayList = setGuideArrayListFromGuidesMap();
        setListAdapter(new CustomGuideAdapter(ListActivity.this, guidesArrayList));

    }

    private ArrayList<Guide> setGuideArrayListFromGuidesMap() {
        ArrayList<Guide> temp = new ArrayList<Guide>();
        Object[] guidesArray = guidesMap.values().toArray();

        for (int i = 0; i < guidesMap.keySet().toArray().length; i++) {
            HashMap currentGuide = ((HashMap) (guidesMap.values().toArray()[i]));

            String uid = (String) guidesMap.keySet().toArray()[i];
            String name = (String) currentGuide.get("name");
            String email = (String) currentGuide.get("email");
            String insuranceExpiration = (String) currentGuide.get("insuranceExpiration");
            String licenseExpiration = (String) currentGuide.get("licenseExpiration");
            boolean isSenior = (Boolean) currentGuide.get("senior");
            temp.add(new Guide(uid, name, email, isSenior, insuranceExpiration, licenseExpiration));
        }
        return temp;
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
        setGuideImageModal((ImageView) guideModal.findViewById(R.id.guide_image_modal));

        new AlertDialog.Builder(this)
                .setView(guideModal)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                })
                .create().show();
    }

    private void setGuideImageModal(final ImageView guideImageModal) {

        mStorageRef = FirebaseStorage.getInstance().getReference();

        Task<Uri> profileImageUri = mStorageRef.child("users").child(currentGuide.uid).getDownloadUrl();
        profileImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ListActivity.this)
                        .load(uri)
                        .into(guideImageModal);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ListActivity.this, "could not load profile image.", Toast.LENGTH_SHORT).show();
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

    public class CustomGuideAdapter extends BaseAdapter {

        Context context;
        List<Guide> rowItems;

        CustomGuideAdapter(Context context, List<Guide> rowItems) {
            this.context = context;
            this.rowItems = rowItems;
        }

        @Override
        public int getCount() {
            return rowItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return rowItems.indexOf(getItem(position));
        }

        /* private view holder class */
        private class ViewHolder {
            TextView guide_name;
            ImageView image;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomGuideAdapter.ViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.guides, null);
                holder = new CustomGuideAdapter.ViewHolder();

                holder.guide_name = (TextView) convertView.findViewById(R.id.guide_name);

                Guide row_pos = rowItems.get(position);

                holder.guide_name.setText(row_pos.name);

                convertView.setTag(holder);
            } else {
                holder = (CustomGuideAdapter.ViewHolder) convertView.getTag();
            }
            holder.image = (ImageView) convertView.findViewById(R.id.guide_list_profile_image);
            Guide currentGuide = (Guide) getItem(position);
            setGuideImage(holder.image, currentGuide.uid);

            return convertView;
        }

        private void setGuideImage(final ImageView image, String uid) {

            Task<Uri> profileImageUri;
            profileImageUri = mStorageRef.child("users").child(uid).getDownloadUrl();
            profileImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(ListActivity.this)
                            .load(uri)
                            .into(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //Toast.makeText(StudentActivity.this, "could not load profile image from firebase.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
