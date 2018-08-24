package com.yulin.ivan.putsker;

import android.Manifest;
import android.app.Dialog;
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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {
    ListView studentsList;
    ArrayList<Student> students;
    Map<String, Object> m;
    ArrayList<Object> arr;
    Toolbar apptoolbar;
    String title;
    Dialog dialog;
    ProgressDialog pg;
    private StorageReference mStorageRef;
    private ImageView profilePicture;

    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    private static final int CAMERA_REQUEST_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        arr = (ArrayList<Object>) getIntent().getSerializableExtra("group");
        title = getIntent().getExtras().getString("title");
        students = new ArrayList<Student>();

        initToolbar();

        for (int i = 1; i < arr.size(); i++) {
            m = (Map<String, Object>) arr.get(i);
            Student item = new Student((String) m.get("name"), (String) m.get("phone"), (Boolean) m.get("hasGear"));
            students.add(item);
        }

        studentsList = (ListView) findViewById(R.id.StudentsList);
        CustomStudentAdapter adapter = new CustomStudentAdapter(this, students);

        studentsList.setAdapter(adapter);
        studentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object clickedStudent = studentsList.getItemAtPosition(position);
                showStudentModal(view);
//                Intent intent = new Intent(StudentActivity.this, ClassActivity.class);
//                Student s = (Student) studentsList.getItemAtPosition(position);
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

    public void showStudentModal(View v) {
        dialog = new Dialog(this);
        TextView name = (TextView)findViewById( R.id.studentNameModal);
        TextView _name = (TextView)findViewById( R.id.studentNameList);
        name.setText(_name.getText().toString());
        dialog.setContentView(R.layout.studentpopup);
        dialog.setTitle("Change Profile Photo");
        dialog.show();
    }

    public void changeStudentImage(View v) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.change_image_dialog);
        dialog.setTitle("Change Profile Photo");
        dialog.show();
    }


    public void getImageFromCamera(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);

            new AlertDialog.Builder(this).
                    setTitle("Please try again after granting permission")
                    .setNeutralButton("Got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    }).show();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
            if (dialog != null) dialog.dismiss();
        }

    }

    public void getImageFromGallery(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);

            new AlertDialog.Builder(this).
                    setTitle("Please try again after granting permission")
                    .setNeutralButton("Got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    }).show();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
            if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE || requestCode == CAMERA_REQUEST_CODE) {
                Uri imageUri = data.getData();

                if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
                    uploadProfileImageFromMemory(imageUri);

                }
                if (requestCode == CAMERA_REQUEST_CODE) {
                    uploadProfileImageFromCamera(data);
                }
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

        mStorageRef.child("students").child(/*phone number is the unique id*/ "0527777777")
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
                        setStudentImageFromFirebase();
                    }
                });
    }

    private void uploadProfileImageFromMemory(Uri imageUri) {
        mStorageRef.child("students").child(/*phone number is the unique id*/"0527777777")
                .putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                        setStudentImageFromFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public void setStudentImageFromFirebase() {

        final StorageReference profileImageRef = mStorageRef.child("students").child(/*phone number is the unique id*/"0527777777");
        final Task<Uri> profileImageUri = profileImageRef.getDownloadUrl();
        profileImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
//                saveImageToMemory(profileImageRef); //save to local for faster load on startup
                profilePicture = findViewById(R.id.student_image_modal);
                Glide.with(StudentActivity.this)
                        .load(uri)
                        .into(profilePicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(StudentActivity.this, "could not load profile image from firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void temp(View view) {
        System.out.println("lala");
    }
}
