package com.example.doryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    TextView registerHere;
    ImageView logo;
    DatabaseHelper dbHelper;
    Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.editName);
        password = findViewById(R.id.editPassword1);

        registerHere = findViewById(R.id.loginNow);
        logo = findViewById(R.id.imageView2);

        continueBtn = findViewById(R.id.continueBtn);


        dbHelper = new DatabaseHelper(LoginActivity.this);

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });


    }

    private void checkUser() {
        String emailTxt = email.getText().toString().trim();
        String passwordTxt = password.getText().toString().trim();

        User loggedInUser = dbHelper.checkUser(emailTxt, passwordTxt);
        if (loggedInUser != null) {
            Toast.makeText(LoginActivity.this, "Welcome, " + loggedInUser.getName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, Home.class);
            intent.putExtra("user_name", loggedInUser.getName());
            intent.putExtra("user_role", loggedInUser.getRole());
            startActivity(intent);
        } else {
            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}

