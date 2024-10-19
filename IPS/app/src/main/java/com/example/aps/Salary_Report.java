package com.example.aps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Salary_Report extends AppCompatActivity {

    Button btnHome, btnLogout, btnProfile, btnReminder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_report);
        btnHome = findViewById(R.id.buHomeSalaryReport);
        btnReminder = findViewById(R.id.buReminderSalaryReport);
        btnProfile = findViewById(R.id.buMyProfileSalaryReport);
        String email = getIntent().getStringExtra("email");
        String UID = getIntent().getStringExtra("UID");


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(Salary_Report.this,
                        com.example.aps.HomepageActivity.class);
                profile.putExtra("email", email);
                profile.putExtra("UID", UID);
                startActivity(profile);
                finish();
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(Salary_Report.this,
                        com.example.aps.myprofile.class);
                profile.putExtra("email", email);
                profile.putExtra("UID", UID);
                startActivity(profile);
                finish();
            }
        });
        btnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reminder = new Intent(Salary_Report.this,
                        com.example.aps.reminder.class);
                reminder.putExtra("email", email);
                reminder.putExtra("UID", UID);
                startActivity(reminder);
                finish();

            }
        });
    }
}