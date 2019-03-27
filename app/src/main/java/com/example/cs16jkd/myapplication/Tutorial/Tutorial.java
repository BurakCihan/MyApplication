package com.example.cs16jkd.myapplication.Tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cs16jkd.myapplication.Operations.MainActivity;
import com.example.cs16jkd.myapplication.R;

public class Tutorial extends AppCompatActivity {
    private Button goHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        init();
    }

    private void init() {
        goHome = findViewById(R.id.goBackToMainBtn);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserBackHome();
            }
        });
    }

    private void SendUserBackHome() {
        startActivity(new Intent(Tutorial.this, MainActivity.class));
    }
}
