package com.yulin.ivan.putsker;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private static final int GET_ACCOUNTS_REQUEST_CODE = 1;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 3;
    private static final int CAMERA_REQUEST_CODE = 4;
    private static final int PROFILE_REQUEST_CODE = 5;
    private static final int CHANGE_PASSWORD_REQUEST_CODE = 6;
    private static final int CHANGE_EMAIL_REQUEST_CODE = 7;
    private static final int CHANGE_NAME_REQUEST_CODE = 8;
    private StorageReference mStorageRef;
    ProgressDialog pg;
    TextView userEmail;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ImageView profilePicture;
    TextView profileName;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();
        userEmail = findViewById(R.id.profile_email);
        userEmail.setText(mUser.getEmail());

        initProfileName();
        initProfileImage();
    }

    private void initProfileName() {
        profileName = findViewById(R.id.profile_name);
        String userDisplayName = mUser.getDisplayName();
//        String preEmail = mUser.getEmail().split("@")[0];
//        profileName.setText(userDisplayName != "" ? userDisplayName : preEmail);
        profileName.setText(userDisplayName);
    }

    private void initProfileImage() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        profilePicture = findViewById(R.id.profile_image);

        if (mUser.getPhotoUrl() != null) {
            //set profile image from firebase if the user has one
            setProfileImageFromFirebase();
        }
    }

    public void setProfileImageFromFirebase() {

        final StorageReference profileImageRef = mStorageRef.child("users").child(mUser.getUid());
        final Task<Uri> profileImageUri = profileImageRef.getDownloadUrl();
        profileImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
//                saveImageToMemory(profileImageRef); //save to local for faster load on startup

                Glide.with(ProfileActivity.this)
                        .load(uri)
                        .into(profilePicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ProfileActivity.this, "could not load profile image from firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImageToMemory(StorageReference profileImageRef) {
        try {
            final File localFile = File.createTempFile("Images", mUser.getUid());
            profileImageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                  Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    Uri uri = Uri.fromFile(localFile);
//                    URL url = null;
//                    try {
//                        url = localFile.toURI().toURL();
                    profilePicture.setImageURI(uri);
//                    }
//                    catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "could not find profile image.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changePassword(View v) {
        startActivityForResult(new Intent(ProfileActivity.this, ChangePasswordActivity.class), CHANGE_PASSWORD_REQUEST_CODE);
    }


    public void changeProfileImage(View v) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.change_image_dialog);
        dialog.setTitle("Change Profile Photo");
        dialog.show();
    }

    public void getImageFromCamera(View v) {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);

            new AlertDialog.Builder(ProfileActivity.this).
                    setTitle("Please try again after granting permission")
                    .setNeutralButton("Got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    }).show();
        }
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
            if (dialog != null) dialog.dismiss();
        }

    }

    public void getImageFromGallery(View v) {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);

            new AlertDialog.Builder(ProfileActivity.this).
                    setTitle("Please try again after granting permission")
                    .setNeutralButton("Got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    }).show();
        }
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent selectFromGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            selectFromGalleryIntent.setType("image/*");
            startActivityForResult(selectFromGalleryIntent, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            if (dialog != null) dialog.dismiss();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == CHANGE_EMAIL_REQUEST_CODE) {
                String newEmail = data.getStringExtra("newEmail");
                userEmail.setText(newEmail);

            } else if (requestCode == CHANGE_NAME_REQUEST_CODE) {
                String newName = data.getStringExtra("newName");
                if (!newName.equals("")) {
                    profileName.setText(newName);
                }

            } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE || requestCode == CAMERA_REQUEST_CODE) {
                Uri imageUri = data.getData();

                if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
                    uploadProfileImageFromMemory(imageUri);

                }
                if (requestCode == CAMERA_REQUEST_CODE) {
                    uploadProfileImageFromCamera(data);
                }
//                ((ImageView) findViewById(R.id.profile_image)).setImageBitmap(bitmap);

            }


        } else /* if response not ok */ {
            //do nothing
            //Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();

        }
        if (pg != null) pg.dismiss();

    }

    private void uploadProfileImageFromCamera(Intent data) {

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] dataBAOS = baos.toByteArray();

        mStorageRef.child("users").child(mUser.getUid())
                .putBytes(dataBAOS)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                        setProfileImageFromFirebase();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadProfileImageFromMemory(Uri imageUri) {
        mStorageRef.child("users").child(mUser.getUid())
                .putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                        setProfileImageFromFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void changeEmail(View view) {
        startActivityForResult(new Intent(ProfileActivity.this, EmailChangeActivity.class), CHANGE_EMAIL_REQUEST_CODE);

    }

    public void changeName(View view) {
        startActivityForResult(new Intent(ProfileActivity.this, NameChangeActivity.class), CHANGE_NAME_REQUEST_CODE);

    }

}
