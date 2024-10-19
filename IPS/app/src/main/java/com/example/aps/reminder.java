package com.example.aps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class reminder extends AppCompatActivity {
    Button btnProfile, btnReminder, btnHome;
    String email, UID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        btnProfile = findViewById(R.id.btnProfile);
        btnHome = findViewById(R.id.btnHome);
        btnReminder = findViewById(R.id.btnReminder);
        email = getIntent().getStringExtra("email");
        UID = getIntent().getStringExtra("UID");

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(reminder.this,
                        com.example.aps.myprofile.class);
                profile.putExtra("email", email);
                profile.putExtra("UID", UID);
                startActivity(profile);
                finish();
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(reminder.this,
                        com.example.aps.HomepageActivity.class);
                profile.putExtra("email", email);
                profile.putExtra("UID", UID);
                startActivity(profile);
                finish();
            }
        });
        btnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(reminder.this, "Already Here", Toast.LENGTH_SHORT).show();

            }
        });


    }
}