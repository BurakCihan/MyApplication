package com.example.cs16jkd.myapplication.Operations;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.cs16jkd.myapplication.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ImportToCalendar extends AppCompatActivity {

   private String captured_string;
    private EditText captured_string_input;
    private SweetAlertDialog error_on_capture_dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_to_calendar);
        
        init();
    }

    private void init() {
        initUIElements();
    }

    /**
     *
     */
    private void initUIElements() {
        captured_string_input = findViewById(R.id.captured_ocr_text);
        captured_string = getIntent().getStringExtra("calender_values");
        System.out.println(captured_string);
        //Complete pattern matching for the text
        captured_string_input.setText(DataExtract(captured_string));

    }
    /**
     *
     */
    private void ConnectToGoogle(){

    }

    /**
     *
     */
    private void ConnectToGoogleCalendar(){

    }

    /**
     *
     */
    private void ParseData(){

    }

    /**
     * Look for certain keywords
     *
     * @param captured_string
     */
    private String DataExtract(String captured_string) {
        String keyword = "CS"+"([0-9])";
        Pattern pattern = Pattern.compile(keyword);
        Matcher matcher = pattern.matcher(captured_string);
        if(matcher.find()){
            return matcher.group().toString();
        }else{
            //Display dialog
            error_on_capture_dialog = new SweetAlertDialog(ImportToCalendar.this,SweetAlertDialog.ERROR_TYPE);
            error_on_capture_dialog.setTitleText("Error!");
            error_on_capture_dialog.setContentText("Error when processing the timetable.");
            error_on_capture_dialog.setConfirmText("Restart the process");
            error_on_capture_dialog.setCancelText("View brunel timetable");
            error_on_capture_dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    Intent sendBackToHome = new Intent(ImportToCalendar.this, MainActivity.class);
                    sendBackToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(sendBackToHome);
                    finish();
                }
            });
            return null;
        }
    }

}
