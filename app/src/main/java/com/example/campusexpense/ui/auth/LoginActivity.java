package com.example.campusexpense.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpense.R;

public class LoginActivity extends AppCompatActivity {

    private TextView registerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        registerText = findViewById(R.id.loginText);


        registerText.setOnClickListener(v -> {
        startActivity(new Intent(this, RegisterActivity.class));
        });

    }
}
