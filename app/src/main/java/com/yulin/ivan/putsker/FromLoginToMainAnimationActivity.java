package com.yulin.ivan.putsker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FromLoginToMainAnimationActivity extends Activity {
    private Boolean isSenior = null;
    private final Object notifier = new Object();
    View busyindicator;

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.activity_from_login_to_main_animation);
        busyindicator = findViewById(R.id.busyindicator);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DatabaseReference user_db = FirebaseDatabase.getInstance().getReference().child("Guides").child("pgxULqnRotc8pFO1pQhznp40ZjE3");
        DatabaseReference seniority = user_db.child("senior");

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView title = findViewById(R.id.gifTitle);
                            title.setText("Got it!\nComing up to surface");
                        }
                    });
                    sleep(1000);
                    synchronized (notifier) {
                        while (isSenior == null) {
                            notifier.wait();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(
                            FromLoginToMainAnimationActivity.this, MainActivity.class)
                            .putExtra("senior", isSenior.booleanValue()));
                }
            }
        };
        timer.start();

        seniority.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot seniority) {
                isSenior = (boolean) seniority.getValue();

                synchronized (notifier) {
                    notifier.notifyAll();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                /* nothing */
            }
        });


    }

    @Override
    public void onBackPressed() {
        /* nothing */
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
