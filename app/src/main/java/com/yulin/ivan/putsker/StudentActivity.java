package com.yulin.ivan.putsker;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.List;
import java.util.Map;

public class StudentActivity extends ListActivity {
    ListView studentsList;
    ArrayList<Student> students;
    Map<String, Object> m;
    ArrayList<Object> arr;
    Toolbar apptoolbar;
    String title;
    Dialog dialog;
    ProgressDialog pg;
    View studentModal;
    Student currentStudent;
    ImageView studentImageModal;

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

        initToolbar();
        initStudentList();

    }

    private void initStudentList() {
        students = new ArrayList<Student>();

        for (int i = 1; i < arr.size(); i++) {
            m = (Map<String, Object>) arr.get(i);
            Student item = new Student((String) m.get("name"), (String) m.get("phone"), (Boolean) m.get("hasGear"));
            students.add(item);
        }

        studentsList = (ListView) findViewById(android.R.id.list);
        setListAdapter(new CustomStudentAdapter(this, students));
    }

    private void initToolbar() {
        apptoolbar = findViewById(R.id.apptoolbar);
        apptoolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        apptoolbar.setTitle(title);
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

    public void showStudentModal(View v) {
        final int position = getListView().getPositionForView((View) v.getParent());
        currentStudent = (Student) this.getListAdapter().getItem(position);

        LayoutInflater inflater = getLayoutInflater();
        studentModal = inflater.inflate(R.layout.studentmodal, null);
        TextView name = studentModal.findViewById(R.id.studentNameModal);
        TextView phone = studentModal.findViewById(R.id.studentPhoneModal);
        TextView gear = studentModal.findViewById(R.id.hasGearModal);
        studentImageModal = studentModal.findViewById(R.id.studentImageModal);

        name.setText(currentStudent.getName());
        phone.setText(currentStudent.getPhone());
        gear.setText((currentStudent.getHasGear() ? "Has " : "Does not have ") + "Gear");
        setStudentImageModal();

        new AlertDialog.Builder(this)
                .setView(studentModal)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                })
                .create().show();
    }

    private void setStudentImageModal() {

        mStorageRef = FirebaseStorage.getInstance().getReference();

        Task<Uri> profileImageUri = mStorageRef.child("students").child(currentStudent.getPhone()).getDownloadUrl();
        profileImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(StudentActivity.this)
                        .load(uri)
                        .into(studentImageModal);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(StudentActivity.this, "could not load profile image from firebase.", Toast.LENGTH_SHORT).show();
            }
        });
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

        mStorageRef.child("students").child(currentStudent.getPhone())
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
        mStorageRef.child("students").child(currentStudent.getPhone())
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

        final StorageReference profileImageRef = mStorageRef.child("students").child(currentStudent.getPhone());
        final Task<Uri> profileImageUri = profileImageRef.getDownloadUrl();
        profileImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
//                saveImageToMemory(profileImageRef); //save to local for faster load on startup
                profilePicture = studentModal.findViewById(R.id.studentImageModal);
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


    public class CustomStudentAdapter extends BaseAdapter {

        Context context;
        List<Student> rowItems;

        CustomStudentAdapter(Context context, List<Student> rowItems) {
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
            TextView student_name;
            ImageView image;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomStudentAdapter.ViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.students, null);
                holder = new CustomStudentAdapter.ViewHolder();

                holder.student_name = (TextView) convertView.findViewById(R.id.studentNameList);

                Student row_pos = rowItems.get(position);

                holder.student_name.setText(row_pos.getName());

                convertView.setTag(holder);
            } else {
                holder = (CustomStudentAdapter.ViewHolder) convertView.getTag();
            }
            holder.image = (ImageView) convertView.findViewById(R.id.student_image_list);
            Student currentStudent = (Student) getItem(position);
            String currentPhone = currentStudent.getPhone();
            setStudentImage(holder.image, currentPhone);

            return convertView;
        }

        private void setStudentImage(final ImageView image, String phone) {

            Task<Uri> profileImageUri;
            profileImageUri = mStorageRef.child("students").child(phone).getDownloadUrl();
            profileImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(StudentActivity.this)
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

