package com.yulin.ivan.putsker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private final String[] seniors = new String[]{"pgxULqnRotc8pFO1pQhznp40ZjE3"};
    private static final int GET_ACCOUNTS_REQUEST_CODE = 1;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 3;
    private static final int CAMERA_REQUEST_CODE = 4;
    private static final int PROFILE_REQUEST_CODE = 5;
    private static final int KITKAT_VALUE = 1002;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageView profileImage;
    NavigationView mNavigationView;
    ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        initFab();
        initDrawer();
        initProfileImage();
        initUsername();
        initSeniority();
    }

    private void initUsername() {
        String username = mUser.getDisplayName();
        String preEmail = mUser.getEmail().split("@")[0];

        if (username == null || username.equals("")) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(preEmail)
                    .build();

            mUser.updateProfile(profileUpdates);
        }
    }

    private void initProfileImage() {
        mNavigationView = findViewById(R.id.nav_view);
        View header = mNavigationView.getHeaderView(0);
        profileImage = header.findViewById(R.id.nav_header_profile_image);
        if (mUser.getPhotoUrl() != null) {
            setProfileImageFromFirebase(); //set profile image from firebase if the user has one
        }
    }

    private void initDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.hide(); //todo add functionality to button (add new student to waiting list)
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @SuppressLint("NewApi")
    private void initSeniority() {
        String userID = mUser.getUid();
        Map newPost = new HashMap();
        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        //todo string array of chosen uids
//        if (userID.equals("pgxULqnRotc8pFO1pQhznp40ZjE3")) //aaa user id
        if (Arrays.asList(seniors).contains(userID))
            newPost.put("seniority", "yes");
        else {
            newPost.put("seniority", "no");
            ImageButton groupsButton = findViewById(R.id.groupsButton);

            ImageButton allGuidesButton = findViewById(R.id.allGuidesButton);
            ImageButton waitingListButton = findViewById(R.id.waitingListButton);
            ImageButton InventoryButton = findViewById(R.id.InventoryButton);

            TextView allGuidesTitle = findViewById(R.id.allGuidesTitle);
            TextView waitingListTitle = findViewById(R.id.waitingListTitle);
            TextView InventoryTitle = findViewById(R.id.InventoryTitle);

//            allGuidesButton.setClickable(false);
//            allGuidesButton.setImageAlpha(51);
//            allGuidesTitle.setAlpha(0.3f);
//            waitingListButton.setClickable(false);
//            waitingListButton.setImageAlpha(51);
//            waitingListTitle.setAlpha(0.3f);
//            InventoryButton.setClickable(false);
//            InventoryButton.setImageAlpha(51);
//            InventoryTitle.setAlpha(0.3f);

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int DeviceTotalWidth = metrics.widthPixels;
            int DeviceTotalHeight = metrics.heightPixels;

//            RelativeLayout RelativeLayoutImageCenter=(RelativeLayout)findViewById(R.id.temp);
//            RelativeLayoutImageCenter.setPadding(0,0,0,DeviceTotalHeight/4);

            LinearLayout RelativeLayouttemp3 = findViewById(R.id.mainRightHalf);
            RelativeLayouttemp3.removeViewAt(1); //remove two right images

            RelativeLayout RelativeLayouttemp4 = findViewById(R.id.allGuidesContainer);
            RelativeLayouttemp4.removeAllViews(); //remove upper left image


//            RelativeLayout RelativeLayoutImageCenterContainer = (RelativeLayout) findViewById(R.id.temp2);
//            RelativeLayoutImageCenterContainer.setPadding(DeviceTotalWidth / 4, 0, 0, 0);

        }

        Task<Void> temp = current_user_db.setValue(newPost);
        temp.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });
        temp.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(getApplicationContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_profile) {
            profileSettings();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void profileSettings() {
        startActivityForResult(new Intent(this, ProfileActivity.class), PROFILE_REQUEST_CODE);
    }

    public void signOut() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            Toast.makeText(MainActivity.this, "No user logged in yet!",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void setProfileImageFromFirebase() {

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        Task<Uri> profileImageUri = mStorageRef.child("users").child(mUser.getUid()).getDownloadUrl();
        profileImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(MainActivity.this)
                        .load(uri)
                        .into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MainActivity.this, "could not load profile image from firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        //todo - permissions is an async function. app crashes when asking for permission in the first time


    }


    public void allGuidesClicked(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra("title", "מדריכים");
        startActivity(intent);
    }

    public void onMyGroupsClicked(View view) {
        Toast.makeText(MainActivity.this, "my groups clicked.",
                Toast.LENGTH_SHORT).show();
    }

    public void OnWaitingListClicked(View view) {
        Toast.makeText(MainActivity.this, "waiting list clicked.",
                Toast.LENGTH_SHORT).show();
    }

    public void onInventoryClicked(View view) {
        Toast.makeText(MainActivity.this, "inventory clicked.",
                Toast.LENGTH_SHORT).show();
    }
}
