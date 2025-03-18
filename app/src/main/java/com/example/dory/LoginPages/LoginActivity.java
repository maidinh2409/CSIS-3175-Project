package com.example.dory.LoginPages;
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

import com.example.dory.MainActivity;
import com.example.dory.R;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    TextView registerHere;
    ImageView logo;
    UserDBHandler dbHelper;
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


        dbHelper = new UserDBHandler(LoginActivity.this);

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

    // Method to check user credentials
    private void checkUser() {
        String emailTxt = email.getText().toString().trim();
        String passwordTxt = password.getText().toString().trim();

        // Validate email and password using the database handler
        if (dbHelper.validateUser(emailTxt, passwordTxt)) {
            // Fetch user from the database
            UserHashed loggedInUser = dbHelper.getUserFromEmail(emailTxt);

            // Display a welcome message
            Toast.makeText(LoginActivity.this, "Welcome, " + loggedInUser.getName(), Toast.LENGTH_SHORT).show();

            // Redirect to Home Activity and pass user data
            Intent intent = new Intent(LoginActivity.this, Home.class);
            intent.putExtra("user_name", loggedInUser.getName());
            intent.putExtra("user_role", loggedInUser.getRole());
            startActivity(intent);
        } else {
            // Show error if user credentials are invalid
            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}

