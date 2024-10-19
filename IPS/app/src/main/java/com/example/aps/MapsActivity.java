package com.example.aps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.aps.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    int LastLocationCode = 5005;
    private LocationManager mLocationManager;

    double lat, lng;
    Intent BackHome;
    Location l;
    Button ibSend;
    boolean isActive;

    String email, UID, docID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Variable
        email = getIntent().getStringExtra("email");
        UID = getIntent().getStringExtra("UID");
        isActive = getIntent().getBooleanExtra("Statues", false);
        docID = getIntent().getStringExtra("docID");

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat stf = new SimpleDateFormat("hh:mm");
        String currentDate = sdf.format(new Date());
        String currentTime = stf.format(new Date());

        GettingStart();
        //User Status

        //Items Initialization
        ibSend = findViewById(R.id.BtnLocation);

        //DataBase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // DocumentReference LocRef = db.collection("Start-EndLocation").document(UID);

        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "Sending", Toast.LENGTH_SHORT).show();
                if (!isActive) {

                    Location LastLocation = getLastKnownLocation();
                    if (LastLocation != null) {
                        lng = LastLocation.getLongitude();
                        lat = LastLocation.getLatitude();
                    }
                    GeoPoint Location = new GeoPoint(lat, lng);
                    Map<String, Object> StartData = new HashMap<>();
                    StartData.put("email", email);
                    StartData.put("StartLocation", Location);
                    StartData.put("StartTime", currentTime);
                    try {
                        StartData.put("StValue", timeHandler(currentTime, isActive));
                        //timeHandler(currentTime, false);
                    } catch (ParseException e) {
                        System.out.println("3aaa " + e);
                    }
                    DocumentReference StartRef = db.collection("Start-EndLocation").document(currentDate + "-" + email);
                    Task StartQuery = StartRef.set(StartData);
                    StartQuery.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MapsActivity.this, "Successfully Saved! " + lat + " & " + lng, Toast.LENGTH_SHORT).show();
                                //StartLocationUpdate();
                                UpdateStatus(true);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MapsActivity.this, "Error " + e, Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {

                    GeoPoint Location = new GeoPoint(lat, lng);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("EndLocation", Location);
                    updates.put("EndTime", currentTime);
                    updates.put("email", email);
                    try {
                        updates.put("EdValue", timeHandler(currentTime, true));
                    } catch (ParseException e) {
                        System.out.println("3aaa " + e);
                    }
                    DocumentReference EndRef = db.collection("Start-EndLocation").document(currentDate + "-" + email);

                    Task EndQuery = EndRef.update(updates);
                    EndQuery.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UpdateStatus(false);
                                //updateSalary(email);
                                Toast.makeText(MapsActivity.this, "Successfully Saved! " + lat + " & " + lng, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MapsActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private double timeHandler(String curentTime, boolean b) throws ParseException {
        double count = 0;
        SimpleDateFormat df = new SimpleDateFormat("hh:mm");
        Date stander;
        if (!b) {
            stander = df.parse("08:00");
            Date date2 = df.parse(curentTime);
            long diff =  date2.getTime()-stander.getTime() ;
            int timeInSeconds = (int) (diff / 1000);
            int minutes = timeInSeconds / 60;
            System.out.println(diff);
            if (minutes >= 60) {
                showDialog("Too Late Start.. " + minutes + " mins");
                count = 0;
            } else if (minutes >= 30 && minutes < 60) {
                showDialog("Slightly Late Start.. " + minutes + " mins");
                count = 0.5;
            } else if (minutes < 30 && minutes >= 0) {
                showDialog("In Time Start just.. " + minutes + " mins");
                count = 1;
            }
        } else {
            stander = df.parse("15:00");
            Date date2 = df.parse(curentTime);
            long diff = stander.getTime()-date2.getTime() ;
            int timeInSeconds = (int) (diff / 1000);
            int minutes;
            minutes = timeInSeconds / 60;

            if (minutes >= 45) {
                showDialog("Too Early Out.. " + minutes + " mins");
                count = 0;
            } else if (minutes > 15 && minutes < 45) {
                showDialog("Slightly Early Out.. " + minutes + " mins");
                count = 0.5;
            } else if (minutes < 15 && minutes >= 0) {
                showDialog("In Time Out just.. " + minutes + " mins");
                count = 1;
            } else if (minutes < 0) {
                showDialog("Over Time Out just.. " + minutes + " mins");
                count = 1;
            }
        }
        return count;
    }
/*
    int dayValue;
    private void updateSalary(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("Start-EndLocation");

        docRef.whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                         dayValue = Integer.parseInt(document.get("EdValue").toString())*
                        Integer.parseInt(document.get("StValue").toString());
                        getWorkDays(dayValue);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
    int Dday;
    private void getWorkDays(int dayValue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(UID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                  Dday= Integer.parseInt(document.get("works_day").toString());
                    UpdateprofileDays(dayValue,Dday);
                } else {

                }
                }
            }
        });
    }
    private void UpdateprofileDays(int dayValue, int oldDaus) {
        Map<String, Object> newDays = new HashMap<>();
        newDays.put("works_day", dayValue+oldDaus);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(UID);
        docRef.set(newDays).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("fol 3lik");
            }
        });

    }
*/
    private void showDialog(String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(BackHome);
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

  /*  private void getStatus() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference UserRef = db.collection("users");
        UserRef.whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        isActive = (boolean) document.get("Status");
                        docID = document.getId();
                    }
                }
            }
        });

    }

   */

    private void UpdateStatus(boolean state) {
        isActive = state;
        if (docID != null) {
            Toast.makeText(MapsActivity.this, "in Statues update", Toast.LENGTH_SHORT).show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference UserRef0 = db.collection("users").document(docID);
            UserRef0.update("Status", state)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MapsActivity.this, "Done Status", Toast.LENGTH_SHORT).show();
                            BackHome = new Intent(MapsActivity.this,
                                    com.example.aps.HomepageActivity.class);
                            BackHome.putExtra("email", email);
                            BackHome.putExtra("UID", UID);
                            BackHome.putExtra("Statues", isActive);
                            BackHome.putExtra("docID", docID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MapsActivity.this, "001" + e, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else Toast.makeText(MapsActivity.this, "hEre " + docID, Toast.LENGTH_SHORT).show();
    }

    private void StartLocationUpdate() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LastLocationCode);

        } else {

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 25, MapsActivity.this);

        }

    }

    private void GettingStart() {


        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, LastLocationCode);

        } else {
            locationEnabled();
            Location LastLocation = getLastKnownLocation();

            if (LastLocation != null) {
                lng = LastLocation.getLongitude();
                lat = LastLocation.getLatitude();
            } else {
                Toast.makeText(MapsActivity.this, "Your Location is " + LastLocation + " Something Error Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Location getLastKnownLocation() {

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LastLocationCode);
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

        return bestLocation;
    }

    private void UpdateToFireBase() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference LocRef = db.collection("LocationsLog").document(UID);

        Location LastLocation = getLastKnownLocation();
        lng = LastLocation.getLongitude();
        lat = LastLocation.getLatitude();

        GeoPoint Location = new GeoPoint(lat, lng);
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", email);
        updates.put("lastLocation", Location);
        updates.put("LastUpdate", Calendar.getInstance().getTime());

        LocRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MapsActivity.this, "done UU", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void locationEnabled() {
        LocationManager lm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("GPS Enable")
                    .setPositiveButton("Settings", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng position = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(MapsActivity.this, "ON Location Changed", Toast.LENGTH_SHORT).show();
        lat = location.getLatitude();
        lng = location.getLongitude();
        UpdateToFireBase();
        Toast.makeText(MapsActivity.this, "UpDated" + lng, Toast.LENGTH_SHORT).show();

    }

}