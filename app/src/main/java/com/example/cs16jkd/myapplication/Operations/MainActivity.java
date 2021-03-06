package com.example.cs16jkd.myapplication.Operations;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cs16jkd.myapplication.R;
import com.example.cs16jkd.myapplication.Tutorial.Tutorial;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private Button importTimtable,removeTimetable,ViewTutorial;
    private Uri pickedTimteTableURI;
    private static final int REQUEST_GALLERY = 001;
    private SweetAlertDialog CaptureDialog;
    private GoogleHandler googleHandler = new GoogleHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initUIElements();
        //Init google
        googleHandler.GoogleHandler(this);
        //Request permission
        PermissionsHandler permissionHandler = new PermissionsHandler();
        permissionHandler.PermissionsHandling(this,MainActivity.this);

    }



    /**
     * Initialise the UI Elements
     */
    private void initUIElements() {
        importTimtable  = findViewById(R.id.importTimeTableBtn);
        removeTimetable = findViewById(R.id.removeTimeTableBtn);
        ViewTutorial = findViewById(R.id.tutorialBtn);

        ViewTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Tutorial.class));
            }
        });
        //Send to gallery for picking image
        importTimtable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Choose between select from gallery or take picture
                CaptureDialog = new SweetAlertDialog(MainActivity.this,SweetAlertDialog.NORMAL_TYPE);
                CaptureDialog.setContentText("Recieve photo ?");
                CaptureDialog.setConfirmText("Pick image from gallery");
                CaptureDialog.setCancelText("Capture image from camera");
                CaptureDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        PickFromGallery();
                    }
                });
                CaptureDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        CaptureImageFromCamera();
                    }
                });
                CaptureDialog.show();
            }
        });

    }

    /**
     * Allow the user to take a picture
     * Will send them to the camera intent
     * Send to bitmap manager class
     */
    private void CaptureImageFromCamera() {
    }

    /**
     * Picks the image from the gallery with a request code
     */
    private void PickFromGallery() {
        Intent sendToGallery = new Intent();
        sendToGallery.setAction(Intent.ACTION_GET_CONTENT);
        sendToGallery.setType("image/*");
        startActivityForResult(sendToGallery,REQUEST_GALLERY );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK){
          //Send the captured image to the next activity for completion
            SendToBitMapManager(data);
        } if(requestCode == googleHandler.REQUEST_GOOGLE_SIGN_IN ){
            //Toast.makeText(this, "Google sign in intent", Toast.LENGTH_SHORT).show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                    GoogleSignInAccount signed_in_user = task.getResult(ApiException.class);
                    Toast.makeText(this, "Welcome "+signed_in_user.getEmail(), Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends the image to the bitmap manager
     * @param data
     */
    private void SendToBitMapManager(Intent data) {
        pickedTimteTableURI  = data.getData();
        Intent sendDrawable = new Intent(MainActivity.this, Bitmap_Manager.class);
        sendDrawable.putExtra("timetable_uri",pickedTimteTableURI.toString());
        startActivity(sendDrawable);
    }
}
