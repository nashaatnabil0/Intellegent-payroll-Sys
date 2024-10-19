package com.example.aps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraClass extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 15;
    String pathToFile;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    File photoFile = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);



        dispatchTakePictureIntent();

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
                Uri photoURI = FileProvider.getUriForFile(CameraClass.this,
                        "com.example.explicitintents.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            } else Log.d("Fill Null: ","3aaa");
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
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {

            Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
            try {
                mProgress.setMessage("Uploading...");
                mProgress.show();


                Uri uri = FileProvider.getUriForFile(CameraClass.this,
                        "com.example.explicitintents.fileprovider",
                        photoFile);
                StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CameraClass.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgress.dismiss();
                        Log.d("Fire Fail","ER "+e);
                        Toast.makeText(CameraClass.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.d("AA", "OFF: " + e);
                Toast.makeText(CameraClass.this, "Excrption AA", Toast.LENGTH_SHORT).show();
            }

        }

    }


}
