package com.example.dory.LoginPages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dory.R;
import com.example.dory.userDatabase.User;
import com.example.dory.userDatabase.UserDBHandler;

public class UpdatePassword extends AppCompatActivity {
    private EditText emailEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;

    private UserDBHandler dbHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatelogin);

        emailEditText = findViewById(R.id.editEmailUpdate);
        newPasswordEditText = findViewById(R.id.editPasswordUpdate);
        confirmPasswordEditText = findViewById(R.id.editPasswordUpdate2);

        dbHandler = new UserDBHandler(this);

        findViewById(R.id.continueBtn2).setOnClickListener(v -> updatePassword());
    }

    private void updatePassword() {
        // Get input from EditText fields
        String email = emailEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the email exists in the database
        if (!dbHandler.userExists(email)) {
            Toast.makeText(this, "No user found with that email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a User object to update the password
        User user = new User(null, email, newPassword, null, null, null, null);

        // Update the user's password in the database
        boolean isUpdated = dbHandler.updateUserPassword(email, newPassword);

        if (isUpdated) {
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdatePassword.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
        }
    }
}
