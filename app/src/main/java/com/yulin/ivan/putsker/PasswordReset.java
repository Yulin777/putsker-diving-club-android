package com.yulin.ivan.putsker;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {
    private EditText email;
    private Button btnReset;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        email = findViewById(R.id.emailPasswordReset);
        btnReset = findViewById(R.id.btnResetPass);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void resetPassword(View view) {
        String userEmail = email.getText().toString().trim();

        if (email.equals("")) {
            Toast.makeText(PasswordReset.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordReset.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            finish();
        }
    }
}
