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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailChangeActivity extends AppCompatActivity {
    private static final int CHANGE_EMAIL_REQUEST_CODE = 7;

    TextView oldEmail;
    TextView newEmail;
    TextView password;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change);

        oldEmail = findViewById(R.id.old_email);
        newEmail = findViewById(R.id.new_email);
        password = findViewById(R.id.pass);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public void changeEmail(View view) {

        final String _oldEmail = oldEmail.getText().toString();
        final String _newEmail = newEmail.getText().toString();
        final String _password = password.getText().toString();

        final String userCurrentEmail = mUser.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(_oldEmail, _password);

        // Prompt the user to re-provide their sign-in credentials
        mUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (!_oldEmail.equals(userCurrentEmail)) {
                                Toast.makeText(EmailChangeActivity.this, "current email does not match.", Toast.LENGTH_SHORT).show();
                            } else {
                                mUser.updateEmail(_newEmail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Intent intent = new Intent();
                                                if (task.isSuccessful()) {
                                                    intent.putExtra("newEmail", _newEmail);
                                                    Toast.makeText(EmailChangeActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    intent.putExtra("newEmail", _oldEmail);
                                                    Toast.makeText(EmailChangeActivity.this, "Email failed to updated", Toast.LENGTH_SHORT).show();
                                                }
                                                setResult(CHANGE_EMAIL_REQUEST_CODE, intent);
                                                finish();
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}
