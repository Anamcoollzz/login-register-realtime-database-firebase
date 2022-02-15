package com.example.logindong.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.logindong.Constants;
import com.example.logindong.R;

public class LoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        final TextView welcomeTextView = findViewById(R.id.welcome);

        Intent intent = getIntent();
        String name = intent.getStringExtra(Constants.NAME);
        welcomeTextView.setText("Selamat datang "+name);
    }
}