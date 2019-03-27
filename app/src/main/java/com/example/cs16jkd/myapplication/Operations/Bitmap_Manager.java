package com.example.cs16jkd.myapplication.Operations;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cs16jkd.myapplication.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Bitmap_Manager extends AppCompatActivity {
    private ImageView timetable_bitmapimage;
    private Uri timeTableURI;
    private EditText OCRText;
    private SweetAlertDialog  OCRCompleteDialog;
    private StringBuilder complete_string;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap__manager);
        init();
    }


    private void init() {
        initUIElements();
    }

    /**
     * Init the UI Elements
     */
    private void initUIElements() {
        //init progress dialog

        OCRText = findViewById(R.id.capturedText);
        timetable_bitmapimage = findViewById(R.id.bitmapImageViewer);
        //get intent and string convert to URI
        timeTableURI = Uri.parse(getIntent().getStringExtra("timetable_uri"));
        //Set the image view
        timetable_bitmapimage.setImageURI(timeTableURI);

        RunRecognitions(timetable_bitmapimage);
    }

    /**
     * Run the OCR on the image
     * @param timetable_bitmapimage
     */
    private void RunRecognitions(ImageView timetable_bitmapimage) {
        //init recognizer
        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        //Check for operation
        if(!recognizer.isOperational()){
            Toast.makeText(this, "Error when attempting to run OCR, please restart the app and try again.", Toast.LENGTH_SHORT).show();
        }else{
            //Get the bitmap of custom image
            Frame frame = new Frame.Builder().setBitmap(getDrawable(timetable_bitmapimage)).build();
            //Add bytes to array
            SparseArray<TextBlock> items = recognizer.detect(frame);
            complete_string = new StringBuilder();
            //Loop and add textblock to stringbuilder
            for( int i = 0; i<items.size(); i++) {
                TextBlock text = items.valueAt(i);
                complete_string.append(text.getValue());
                complete_string.append("\n");
            }
            //Dialog management
            OCRCompleteDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
            OCRCompleteDialog.setTitleText("Processing Complete!");
            OCRCompleteDialog.setContentText("Your timetable has finished syncing,would you like to add to calendar");
            OCRCompleteDialog.setConfirmText("Add to google calendar");
            OCRCompleteDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    //dismiss the listener
                    sweetAlertDialog.dismissWithAnimation();
                    //Send to calendar management
                    Intent SendToCalendarManager = new Intent(Bitmap_Manager.this, ImportToCalendar.class);
                    SendToCalendarManager.putExtra("calender_values",complete_string.toString());
                    startActivity(SendToCalendarManager);
                }
            });
            OCRCompleteDialog.show();

        }
    }



    /**
     * Getting the image from any image
     * @param imageView
     * @return
     */
    private Bitmap getDrawable(ImageView imageView){
        if(timetable_bitmapimage != null){
            BitmapDrawable imageBitMapDrawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap imageBitMap = imageBitMapDrawable.getBitmap();
            return imageBitMap;
        }
        return null;
    }
}
