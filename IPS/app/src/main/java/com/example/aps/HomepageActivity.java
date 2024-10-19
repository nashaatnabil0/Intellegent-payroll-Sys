package com.example.aps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomepageActivity extends AppCompatActivity implements LocationListener {
    TextView YouAreSentence, tvStatues;
    Button btnProfile, btnReminder, btnHome, btnStartwork;
    private int LocationRequestCode = 1001;
    String email, UID;
    boolean isActive;
    String docID;

    String pathToFile;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    File photoFile = null;
    private static final int CAMERA_REQUEST_CODE = 15;
    private LocationManager mLocationManager;
    Location l;
    ProgressDialog progressBar;
    int works_day;
    Date currentDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        YouAreSentence = findViewById(R.id.YouAreSentence);
        tvStatues = findViewById(R.id.tvStatues);
        btnProfile = findViewById(R.id.btnProfile);
        btnHome = findViewById(R.id.btnHome);
        btnStartwork = findViewById(R.id.btnStartwork);
        btnReminder = findViewById(R.id.btnReminder);
        email = getIntent().getStringExtra("email");
        UID = getIntent().getStringExtra("UID");
        progressBar = new ProgressDialog(this);

        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);


        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(HomepageActivity.this,
                        com.example.aps.myprofile.class);
                profile.putExtra("email", email);
                profile.putExtra("UID", UID);
                startActivity(profile);

            }
        });
        btnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reminder = new Intent(HomepageActivity.this,
                        com.example.aps.reminder.class);
                reminder.putExtra("email", email);
                reminder.putExtra("UID", UID);
                startActivity(reminder);


            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomepageActivity.this, "Already Here", Toast.LENGTH_SHORT).show();

            }
        });
        btnStartwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();

            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.d("my log", "Excep2 : " + e);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(HomepageActivity.this,
                        "com.example.aps.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            } else Log.d("Fill Null: ", "3aaa");
        }
    }

    private File createImageFile() throws IOException {
// Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {

            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Log.d("my log", "Excep1 : " + e);
        }
// Save a file: path for use with ACTION_VIEW intents

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {

            try {
                mProgress.setTitle("Uploading");
                mProgress.setCancelable(false);
                mProgress.show();

                Uri uri = FileProvider.getUriForFile(HomepageActivity.this,
                        "com.example.aps.fileprovider",
                        photoFile);
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                //here you can choose quality factor in third parameter(ex. i choosen 25)
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] fileInBytes = baos.toByteArray();

                StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());
                filepath.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(HomepageActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                        Intent map = new Intent(HomepageActivity.this,
                                MapsActivity.class);
                        map.putExtra("email", email);
                        map.putExtra("UID", UID);
                        map.putExtra("Statues", isActive);
                        map.putExtra("docID", docID);
                        map.putExtra("works_day", works_day);

                        startActivity(map);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgress.dismiss();
                        Log.d("Fire Fail", "ER :" + e);
                        Toast.makeText(HomepageActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        //calculating progress percentage
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                        mProgress.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });

            } catch (Exception e) {
                Log.d("AA", "OFF: " + e);
                Toast.makeText(HomepageActivity.this, "Excrption AA", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void getStatus() {
        progressBar.setMessage("Loading... ");
        progressBar.show();
        progressBar.setCancelable(false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference UserRef = db.collection("users");
        UserRef.whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.dismiss();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        isActive = (boolean) document.get("Status");
                        docID = document.getId();
                        works_day = Integer.parseInt(document.get("works_day").toString());
                        if (isActive) {
                            tvStatues.setText("Online");
                            btnStartwork.setText("End Work Session");
                            StartLocUpdate();

                        } else {
                            tvStatues.setText("OFFLINE");
                            btnStartwork.setText("Start Work Session");

                        }

                    }
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        getStatus();
        getLastKnownLocation();

    }

  /*  private void getWorkDays() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(UID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                         works_day = Integer.parseInt(document.get("works_day").toString());

                    } else {

                    }
                }
            }
        });
    }*/

    private void StartLocUpdate() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(HomepageActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 55);

        } else {

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 30, this);

        }

    }

    private void getLastKnownLocation() {

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomepageActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 55);
        } else {
            for (String provider : providers) {
                l = mLocationManager.getLastKnownLocation(provider);

                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }

        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getStatus();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, "RUN", Toast.LENGTH_SHORT).show();
        Map<String, Object> StartData = new HashMap<>();
        StartData.put("email", email);
        StartData.put("StartLocation", location);
        StartData.put("time", currentDate);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference StartRef = db.collection("LocationsLog");
        Task StartQuery = StartRef.add(StartData);
        StartQuery.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(HomepageActivity.this, "Tracked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}