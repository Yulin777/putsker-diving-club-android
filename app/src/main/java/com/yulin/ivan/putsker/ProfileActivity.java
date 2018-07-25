package com.yulin.ivan.putsker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private static final int GET_ACCOUNTS_REQUEST_CODE = 1;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 3;
    private static final int CAMERA_REQUEST_CODE = 4;
    private static final int PROFILE_REQUEST_CODE = 5;
    private static final int CHANGE_PASSWORD_REQUEST_CODE = 6;
    private static final int CHANGE_EMAIL_REQUEST_CODE = 7;
    private StorageReference mStorageRef;
    ProgressDialog pg;
    TextView userEmail;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();
        userEmail = findViewById(R.id.profile_email);
        userEmail.setText(mUser.getEmail());
    }


    public void changePassword(View v) {
        startActivityForResult(new Intent(ProfileActivity.this, ChangePasswordActivity.class), CHANGE_PASSWORD_REQUEST_CODE);
    }


    public void changeProfileImage(View v) {
        //todo change to users image
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.popo);

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(ProfileActivity.this);
        builderSingle.setTitle("Change Profile Photo");
        builderSingle.setView(image);
        builderSingle.setNeutralButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getImageFromGallery();

            }
        });
        builderSingle.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getImageFromCamera();

            }
        });

        builderSingle.show();
    }

    private void getImageFromCamera() {
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
        }
    }

    public void getImageFromGallery() {
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
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri;
        Bitmap bitmap = null;

        if (requestCode == CHANGE_EMAIL_REQUEST_CODE) {
            String newEmail = data.getStringExtra("newEmail");
            userEmail.setText(newEmail);
        } else try {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                pg = new ProgressDialog(this);
                pg.setMessage("uploading image...");
                pg.show();
                if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    mStorageRef.child("images").child(imageUri.getLastPathSegment()).putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
                if (requestCode == CAMERA_REQUEST_CODE) {

                    bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dataBAOS = baos.toByteArray();

                    mStorageRef.child("images").child("filename" + new Date().getTime())
                            .putBytes(dataBAOS)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE || requestCode == CAMERA_REQUEST_CODE) {
                    ((ImageView) findViewById(R.id.profile_image)).setImageBitmap(bitmap);
                }


            } else {
                //do nothing
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (pg != null) pg.dismiss();

    }

    public void changeEmail(View view) {
        startActivityForResult(new Intent(ProfileActivity.this, EmailChangeActivity.class), CHANGE_EMAIL_REQUEST_CODE);

    }
}
