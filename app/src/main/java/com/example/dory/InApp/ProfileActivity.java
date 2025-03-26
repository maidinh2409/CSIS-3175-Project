package com.example.dory.InApp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dory.R;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;

public class ProfileActivity extends AppCompatActivity {
    Button changeSetting;
    ImageView profileImage;
    TextView userName, userEmail, userPassword, orgName, userRole, contactInfo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ UI
        changeSetting = findViewById(R.id.change_setting_btn);
        profileImage = findViewById(R.id.profile_img);
        userName = findViewById(R.id.user_name_input);
        userEmail = findViewById(R.id.email_input);
        userPassword = findViewById(R.id.password_input);
        orgName = findViewById(R.id.organization_input);
        userRole = findViewById(R.id.role_input);
        contactInfo = findViewById(R.id.contact_input);

        // 🟢 Lấy email từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("user_email", null);

        if (email == null) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu không có email
            return;
        }

        // 🟢 Lấy thông tin từ database
        UserDBHandler dbHelper = new UserDBHandler(this);
        UserHashed user = dbHelper.getUserFromEmail(email);

        if (user == null) {
            Toast.makeText(this, "User data not available!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🟢 Hiển thị thông tin user
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        userPassword.setText("********"); // Không hiển thị password thật
        orgName.setText(user.getOrganizationName() != null ? user.getOrganizationName() : "N/A");
        userRole.setText(user.getRole());
        contactInfo.setText(user.getContactInfo() != null ? user.getContactInfo() : "N/A");

        // 🟢 Nếu có ảnh đại diện, cập nhật ImageView
        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
            int imageResource = getResources().getIdentifier(user.getProfilePhoto(), "drawable", getPackageName());
            if (imageResource != 0) {
                profileImage.setImageResource(imageResource);
            }
        }

        // 🟢 Xử lý khi nhấn nút chỉnh sửa profile
        changeSetting.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileSetting.class);
            startActivity(intent);
        });
    }
}
