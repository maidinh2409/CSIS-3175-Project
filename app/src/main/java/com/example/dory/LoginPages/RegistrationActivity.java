package com.example.dory.LoginPages;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dory.R;
import com.example.dory.userDatabase.User;
import com.example.dory.userDatabase.UserDBHandler;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationActivity extends AppCompatActivity {
    Button registerBtn;
    TextView loginNow;
    TextInputEditText nameTxt, companyTxt, emailTxt, passwordTxt, confirmedPassword;
    MaterialAutoCompleteTextView roleTxt;

    TextInputEditText []  fields;

    UserDBHandler dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        loginNow = findViewById(R.id.loginNow);
        registerBtn = findViewById(R.id.register_btn);
        nameTxt = findViewById(R.id.register_username_text_input);
        companyTxt = findViewById(R.id.register_org_text_input);
        roleTxt = findViewById(R.id.register_role_text_input);
        emailTxt = findViewById(R.id.register_email_text_input);
        passwordTxt = findViewById(R.id.register_password_text_input);
        confirmedPassword = findViewById(R.id.register_cpassword_text_input);



        fields = new TextInputEditText[] {nameTxt, companyTxt, emailTxt, passwordTxt, confirmedPassword};

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
                if (!registrationError() || !passwordError() || !checkEmail()) {
                    return;
                }
                checkRegistration();
            }
        });


    }

    private boolean registrationError() {
        if (roleTxt.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Role must be selected!", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (TextInputEditText field : fields) {
            if (field.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "All fields must be filled in!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }


    private boolean passwordError() {
        String passwordVal = passwordTxt.getText().toString().trim();
        String confirmedPwVal = confirmedPassword.getText().toString().trim();
        if (!passwordVal.equals(confirmedPwVal)) {
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkEmail() {
        String emailVal = emailTxt.getText().toString().trim();
        String emailPattern = "^[^@]+@[^@]+\\.[a-zA-Z]{2,}$";

        if (!emailVal.matches(emailPattern)) {
            Toast.makeText(this, "Email is invalid (must be in format xxx@xxx.xxx)", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkRegistration() {
        String name = nameTxt.getText().toString().trim();
        String email = emailTxt.getText().toString().trim();
        String password = passwordTxt.getText().toString().trim();
        String company = companyTxt.getText().toString().trim();
        String role = roleTxt.getText().toString().trim();


        if (dbHelper.userExists(email)) {
            Toast.makeText(this, "Email is already registered!", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(name, email, password, role, company);
        boolean isRegistered = dbHelper.addNewUser(user);

        if (isRegistered) {
            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }



}
