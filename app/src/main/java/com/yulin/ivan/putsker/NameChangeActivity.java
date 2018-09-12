package com.yulin.ivan.putsker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class NameChangeActivity extends AppCompatActivity {

    private static final int CHANGE_NAME_REQUEST_CODE = 8;
    TextView newName;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_change);

        newName = findViewById(R.id.new_name);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public void changeName(View view) {
        final String _newName = newName.getText().toString();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(_newName)
                .build();

        mUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent();
                        if (task.isSuccessful()) {
                            intent.putExtra("newName", _newName);
                            Toast.makeText(NameChangeActivity.this, "Name updated", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(NameChangeActivity.this, "Name could not update", Toast.LENGTH_SHORT).show();
                            intent.putExtra("newName", "");
                        }
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
    }
}
