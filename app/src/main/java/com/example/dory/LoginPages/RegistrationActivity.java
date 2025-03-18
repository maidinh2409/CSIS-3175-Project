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
import com.example.dory.userDatabase.User;
import com.example.dory.userDatabase.UserDBHandler;

public class RegistrationActivity extends AppCompatActivity {
    Button registerBtn;
    TextView loginNow;
    ImageView logo;
    EditText nameTxt, companyTxt, emailTxt, passwordTxt, confirmedPassword, roleTxt;
    EditText []  fields;

    UserDBHandler dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        logo = findViewById(R.id.imageView2);
        loginNow = findViewById(R.id.loginNow);
        registerBtn = findViewById(R.id.continueBtn2);
        nameTxt = findViewById(R.id.editEmailLogin);
        companyTxt = findViewById(R.id.editCompany);
        roleTxt = findViewById(R.id.editRole);
        emailTxt = findViewById(R.id.editEmail1);
        passwordTxt = findViewById(R.id.editPasswordUpdate);
        confirmedPassword = findViewById(R.id.editPassword2);



        fields = new EditText[] {nameTxt, companyTxt, roleTxt, emailTxt, passwordTxt, confirmedPassword};

        dbHelper = new  UserDBHandler(RegistrationActivity.this);

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });



        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationError();
                passwordError();
                checkEmail();
                checkRegistration();
            }
        });




        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void registrationError() {
        for (EditText field: fields) {
            if (field.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "All fields must be filled in!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void passwordError() {
        String passwordVal = passwordTxt.getText().toString().trim();
        String confirmedPwVal = confirmedPassword.getText().toString().trim();
        if (!passwordVal.equals(confirmedPwVal)) {
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkEmail() {
        String emailVal = emailTxt.getText().toString().trim();
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if (!emailVal.matches(emailPattern)) {
            Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkRegistration() {
        String name = nameTxt.getText().toString().trim();
        String email = emailTxt.getText().toString().trim();
        String password = passwordTxt.getText().toString().trim();
        String company = companyTxt.getText().toString().trim();
        String role = roleTxt.getText().toString().trim();

        User user = new User(name, email, password, role);
        boolean isRegistered = dbHelper.addNewUser(user);

        if (isRegistered) {
            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else {
            Toast.makeText(this, "Registration failed. Email might be taken.", Toast.LENGTH_SHORT).show();
        }
    }



}
