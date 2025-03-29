package com.example.dory.LoginPages;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dory.InApp.ProfileActivity;
import com.example.dory.MainActivity;
import com.example.dory.R;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    TextView registerHere;
    TextView logo;
    UserDBHandler dbHelper;
    Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email_input);
        password = findViewById(R.id.login_password_input);

        registerHere = findViewById(R.id.registerNow);
        logo = findViewById(R.id.app_heading);

        continueBtn = findViewById(R.id.login_continue_btn);


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

        if (dbHelper.validateUser(emailTxt, passwordTxt)) {
            UserHashed loggedInUser = dbHelper.getUserFromEmail(emailTxt);

            // Lưu email vào SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_email", loggedInUser.getEmail());
            editor.apply(); // Lưu lại dữ liệu

            Toast.makeText(LoginActivity.this, "Welcome, " + loggedInUser.getName(), Toast.LENGTH_SHORT).show();

            // Chuyển sang ProfileActivity
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish(); // Kết thúc LoginActivity
        } else {
            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}

