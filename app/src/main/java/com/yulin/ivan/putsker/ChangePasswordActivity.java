package com.yulin.ivan.putsker;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText email;
    EditText pass1;
    EditText pass2;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        email = findViewById(R.id.emailPasswordReset);
        pass1 = findViewById(R.id.newPass1);
        pass2 = findViewById(R.id.newPass2);
        mAuth = FirebaseAuth.getInstance();
    }

    public void changePassword(View v) {
        final FirebaseUser user = mAuth.getCurrentUser();
        String oldPassword = pass1.getText().toString();
        String userEmail = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(userEmail, oldPassword);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String newPassword = pass2.getText().toString();
                            if (!newPassword.equals("") && newPassword.length() > 5) {
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePasswordActivity.this,
                                                    "Password Changed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChangePasswordActivity.this,
                                                    "Password Change Failed", Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                    }
                                });
                            } else {
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Password should be identical in both fields\n" +
                                                "password should be at least 6 characters\n" +
                                                "Email must match your current email.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Wrong Old Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
