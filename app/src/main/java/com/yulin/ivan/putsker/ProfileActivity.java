package com.yulin.ivan.putsker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final int CHANGE_PASSWORD_REQUEST_CODE = 1;
    MainActivity ma = new MainActivity();//for code reuse

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void changeProfileImage(View v) {
        //ma.changeProfileImage(v,this);
    }

    public void changePassword(View v) {
        startActivityForResult(new Intent(ProfileActivity.this, ChangePasswordActivity.class), CHANGE_PASSWORD_REQUEST_CODE);
    }
}
