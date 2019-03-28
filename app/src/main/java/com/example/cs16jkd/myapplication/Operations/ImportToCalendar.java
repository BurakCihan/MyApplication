package com.example.cs16jkd.myapplication.Operations;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cs16jkd.myapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.auth.oauth.AbstractOAuthGetToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ImportToCalendar extends AppCompatActivity {

   private String captured_string;

    private EditText module_code_input,startTime_input, endTime_input;
    private Spinner lecturer_input,location_input;
    private SweetAlertDialog error_on_capture_dialog, addedEventDialog;
    private Button create_event_button;
    private ArrayAdapter<String> locationAdapter, lecturerAdapter;
    private String[] locations_list, lecturer_list;
    private DateTime startDateTime,endDateTime;
    private Calendar service;


    String startTime ;
    String endTime ;
    String moduleCodeString;

    private static final String APPLICATION_NAME = "MyApplication";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = ImportToCalendar.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(900).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_to_calendar);
        try {
            init();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void init() throws GeneralSecurityException, IOException {
        initUIElements();
    }

    /**
     *
     */
    private void initUIElements() {
        //init success dialog
        addedEventDialog = new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE);
        addedEventDialog.setTitle("Successfully added to Calendar");
        addedEventDialog.setContentText("You can now view the activity within your calendar");
        addedEventDialog.setConfirmText("Close");
        addedEventDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                //Send to the calendar app in phone
                Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
                startActivity(calendarIntent);
            }
        });

        //init arrays
        locations_list = new String[] {"Eastern Gateway","Lecture Centre"};
        lecturer_list = new String[] {"Martin Shepard"};

        //Set adapters
        locationAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,locations_list);
        lecturerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,lecturer_list);

        //init UI Elements
        module_code_input = findViewById(R.id.module_code_event);
        startTime_input = findViewById(R.id.start_time_event);
        endTime_input = findViewById(R.id.end_time_event);

        //init spinners
        lecturer_input = findViewById(R.id.lecturer_event);
        location_input = findViewById(R.id.location_event);

        //Set spinners
        lecturer_input.setAdapter(lecturerAdapter);
        location_input.setAdapter(locationAdapter);

        //Set buttons
        create_event_button = findViewById(R.id.create_subject_event);
        captured_string = getIntent().getStringExtra("calender_values");

        //Complete pattern matching for the text
        SetFields(captured_string);
    }

    /**
     *
     * @param captured_string
     */
    private void SetFields(String captured_string) {
        module_code_input.setText(DataExtract(captured_string));
        //Get Current yaer

        String currentYear = "2019";

        //Create Date and Time
         startDateTime = new DateTime(currentYear+"-05-28T09:00:00-07:00");
         endDateTime = new DateTime(currentYear+"-05-28T09:00:00-09:00");

        startTime_input.setText(startDateTime.toString());
        endTime_input.setText(endDateTime.toString());

        create_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckInput() == true){
                    try {
                        CreateGoogleCalendarEvent();
                    } catch (IOException e) {
                        Toast.makeText(ImportToCalendar.this, e.getStackTrace().toString(),Toast.LENGTH_SHORT).show();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**
     * Check inputs then return values
     * @return
     */
    private boolean CheckInput() {
        //Get text of inputs
         startTime = startTime_input.getText().toString();
         endTime = endTime_input.getText().toString();
         moduleCodeString = module_code_input.getText().toString();

        if (startTime == null || endTime == null || moduleCodeString == null) {
            Toast.makeText(this, "Fill in all fields to continue", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     */
    private void CreateGoogleCalendarEvent() throws IOException, GeneralSecurityException {
        /*
        final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        */
        //Get text from UI Elements and create event
        Event subject_event = new Event();

        //Set start time
        EventDateTime subject_event_start_time = new EventDateTime();
        subject_event_start_time.setDateTime(startDateTime);
        subject_event.setStart(subject_event_start_time);

        //Set end time
        EventDateTime subject_event_end_time = new EventDateTime();
        subject_event_end_time.setDateTime(endDateTime);
        subject_event.setEnd(subject_event_end_time);


        String CURRENT_USER = GoogleSignIn.getLastSignedInAccount(this).getEmail();
        ContentResolver cr = this.getContentResolver();
        ContentValues cv = new ContentValues();


        cv.put(CalendarContract.Calendars.ACCOUNT_NAME, CURRENT_USER);
        cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CURRENT_USER);
        cv.put(CalendarContract.Calendars.NAME, "CalTime calendar");
        cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "Module Calendar");
        cv.put(CalendarContract.Calendars.CALENDAR_COLOR, "232323");
        cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, CURRENT_USER);
        cv.put(CalendarContract.Calendars.ALLOWED_REMINDERS, "METHOD_ALERT, METHOD_EMAIL, METHOD_ALARM");
        cv.put(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES, "TYPE_OPTIONAL, TYPE_REQUIRED, TYPE_RESOURCE");
        cv.put(CalendarContract.Calendars.ALLOWED_AVAILABILITY, "AVAILABILITY_BUSY, AVAILABILITY_FREE, AVAILABILITY_TENTATIVE");
        cv.put(CalendarContract.Events.TITLE,module_code_input.getText().toString());
        cv.put(CalendarContract.Events.EVENT_LOCATION, location_input.getSelectedItem().toString());
        cv.put(CalendarContract.Events.DESCRIPTION,"Lecturer: "+lecturer_input.getSelectedItem().toString());
        //Re-do the time
        cv.put(CalendarContract.Events.DTSTART, java.util.Calendar.getInstance().getTimeInMillis());
        cv.put(CalendarContract.Events.DTEND, java.util.Calendar.getInstance().getTimeInMillis()+60*60*1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 10);
        }

        Uri uri = CalendarContract.ExtendedProperties.CONTENT_URI;
         uri.buildUpon().appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CURRENT_USER)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CURRENT_USER).build();

        cr.insert(uri, cv);



        //On-success
        addedEventDialog.show();






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
