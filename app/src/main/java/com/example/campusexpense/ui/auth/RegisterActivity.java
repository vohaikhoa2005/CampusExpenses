package com.example.campusexpense.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpense.R;
import com.example.campusexpense.data.database.AppDatabase;
import com.example.campusexpense.data.database.UserDao;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private UserDao userDao;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private Button registerButton;
    private TextView loginText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);

        loginText = findViewById(R.id.loginText);
        loginText.setOnClickListener(v -> finish());

        AppDatabase database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        registerButton.setOnClickListener(v -> register());

    }

    private void register() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        usernameLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);

        if (username.isEmpty()) {
            usernameLayout.setError(getString(R.string.error_empty_username));
            return;
        }


        if (password.isEmpty()) {
            passwordLayout.setError(getString(R.string.error_empty_password));
            return;
        }

        if (!confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError(getString(R.string.error_password_mismatch));
            return;
        }
        registerButton.setEnabled(false);`
        registerButton.setText(R.string.registering);



    }
}