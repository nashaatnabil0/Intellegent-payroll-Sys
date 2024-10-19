package com.example.aps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class myprofile extends AppCompatActivity {
    Button btnHome, btnLogout, btnProfile, btnReminder, butSalaryReport, buAttendenceReport;
    TextView tvName, tvID, tvPostion, tvPhone, tvEmail, tvSalary;
    private FirebaseAuth mAuth;
    String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        btnHome = findViewById(R.id.btnHome);
        btnReminder = findViewById(R.id.buReminderMyProfile);
        btnProfile = findViewById(R.id.buMyProfileMyProfile);
        buAttendenceReport = findViewById(R.id.buAttendenceReport);
        butSalaryReport = findViewById(R.id.buSalaryReport);
        btnLogout = findViewById(R.id.btnLogout);
        tvName = findViewById(R.id.tvName);
        tvID = findViewById(R.id.tvID);
        tvPostion = findViewById(R.id.tvPostion);
        tvPhone = findViewById(R.id.TvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvSalary = findViewById(R.id.tvSalary);
        mAuth = FirebaseAuth.getInstance();
        String email = getIntent().getStringExtra("email");
        String UID = getIntent().getStringExtra("UID");


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(myprofile.this,
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
                Toast.makeText(myprofile.this, "Already Here", Toast.LENGTH_SHORT).show();

            }
        });
        btnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reminder = new Intent(myprofile.this,
                        com.example.aps.reminder.class);
                reminder.putExtra("email", email);
                reminder.putExtra("UID", UID);
                startActivity(reminder);
                finish();

            }
        });
        butSalaryReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reminder = new Intent(myprofile.this,
                        com.example.aps.Salary_Report.class);
                reminder.putExtra("email", email);
                reminder.putExtra("UID", UID);
                startActivity(reminder);
                finish();

            }
        });
        buAttendenceReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reminder = new Intent(myprofile.this,
                        com.example.aps.Attendace.class);
                reminder.putExtra("email", email);
                reminder.putExtra("UID", UID);
                reminder.putExtra("name", name);
                startActivity(reminder);
                finish();

            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();
                Intent restart = new Intent(myprofile.this,
                        com.example.aps.MainActivity.class);
                startActivity(restart);
                finish();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("users");

        docRef.whereEqualTo("Email", email)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                tvName.setText(document.get("FName").toString() + " " + document.get("LName").toString());
                                tvID.setText(document.get("EmpID").toString());
                                tvPostion.setText(document.get("Position").toString());
                                tvPhone.setText(document.get("Phone").toString());
                                tvEmail.setText(document.get("Email").toString());
                                tvSalary.setText(document.get("SalaryPD").toString() + " per day");
                                name = document.get("FName").toString() + " " + document.get("LName").toString();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    public void signOut() {
        // [START auth_fui_signout]
        mAuth.signOut();

    }


}