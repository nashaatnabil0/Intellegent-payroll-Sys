package com.example.aps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Attendace extends AppCompatActivity {
    EditText etDetail, etSubject;
    Button btnSubmit, btnProfile, btnReminder, btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendace);
        btnSubmit = findViewById(R.id.btnSubmit);
        etDetail = findViewById(R.id.etDetail);
        etSubject = findViewById(R.id.etSubject);
        btnHome = findViewById(R.id.btnHome);
        btnReminder = findViewById(R.id.buReminderMyProfile);
        btnProfile = findViewById(R.id.buMyProfileMyProfile);
        String email = getIntent().getStringExtra("email");
        String UID = getIntent().getStringExtra("UID");
        String name = getIntent().getStringExtra("name");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(Attendace.this,
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
                Intent profile = new Intent(Attendace.this,
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

                Intent reminder = new Intent(Attendace.this,
                        com.example.aps.reminder.class);
                reminder.putExtra("email", email);
                reminder.putExtra("UID", UID);
                startActivity(reminder);
                finish();

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etSubject.getText().toString().isEmpty() && !etDetail.getText().toString().isEmpty()) {
                    Toast.makeText(Attendace.this, "Sending", Toast.LENGTH_SHORT).show();
                    Map<String, Object> RequestData = new HashMap<>();
                    RequestData.put("email", email);
                    RequestData.put("subject", etSubject.getText().toString());
                    RequestData.put("detail", etDetail.getText().toString());
                    RequestData.put("name", name);

                    CollectionReference StartRef = db.collection("EmpRequest");
                    Task StartQuery = StartRef.add(RequestData);
                    StartQuery.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Attendace.this, "Successfully Saved! ", Toast.LENGTH_SHORT).show();
                                Intent profile = new Intent(Attendace.this,
                                        com.example.aps.myprofile.class);
                                profile.putExtra("email", email);
                                profile.putExtra("UID", UID);
                                startActivity(profile);
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Attendace.this, "Error " + e, Toast.LENGTH_SHORT).show();

                        }
                    });


                } else
                    Toast.makeText(Attendace.this, "please enter the request", Toast.LENGTH_SHORT).show();

            }
        });

    }
}