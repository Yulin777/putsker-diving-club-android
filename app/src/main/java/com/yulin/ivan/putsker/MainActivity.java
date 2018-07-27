package com.yulin.ivan.putsker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int GET_ACCOUNTS_REQUEST_CODE = 1;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 3;
    private static final int CAMERA_REQUEST_CODE = 4;
    private static final int PROFILE_REQUEST_CODE = 5;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageView profileImage;
    ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide(); //todo add functionality to button (add new student to waiting list)
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        profileImage = findViewById(R.id.profile_image);
//        int id = getResources().getIdentifier("com.yulin.ivan.putsker:drawable/" + "diver", null, null);
//        profileImage.setImageResource(id);
        profileImage.setImageDrawable(getResources().getDrawable(R.drawable.diver));
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri;

        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) {
            imageUri = data.getData();

            if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
//                try {

                //place image to firebase
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(imageUri)
                        .build();

                mUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    //get image from firebase
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUser.getPhotoUrl());

                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        ((ImageView) findViewById(R.id.profile_image)).setImageBitmap(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });

//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    ((ImageView) findViewById(R.id.profile_image)).setImageBitmap(bitmap);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        //todo - permissions is an async function. app crashes when asking for permission in the first time


    }


    public void showProfilePicture(View view) {
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.popo);


        Intent selectFromGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectFromGalleryIntent.setType("image/*");
        startActivityForResult(selectFromGalleryIntent, READ_EXTERNAL_STORAGE_REQUEST_CODE);
//
//        try {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mUser.getPhotoUrl());
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            ((ImageView) findViewById(R.id.profile_image)).setImageBitmap(bitmap);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        image.setImageResource();
//
//        new AlertDialog.Builder(this)
//                .setView(image)
//                .show();
    }
}
