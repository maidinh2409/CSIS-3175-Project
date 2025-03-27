package com.example.dory.InApp;

import static com.example.dory.InApp.ProfileSetting.PROFILE_UPDATE_REQUEST_CODE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dory.R;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;

public class ProfileActivity extends AppCompatActivity {
    Button changeSetting;
    ImageView profileImage;
    TextView userName, userEmail, userPassword, orgName, userRole, contactInfo;


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
            startActivityForResult(intent, PROFILE_UPDATE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Gọi lại loadUserProfile() để cập nhật giao diện
            loadUserProfile();
        }
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("user_email", null);

        if (email == null) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        UserDBHandler dbHelper = new UserDBHandler(this);
        UserHashed user = dbHelper.getUserFromEmail(email);

        if (user == null) {
            Toast.makeText(this, "User data not available!", Toast.LENGTH_SHORT).show();
            return;
        }

        userName.setText(user.getName());
        orgName.setText(user.getOrganizationName() != null ? user.getOrganizationName() : "N/A");
        contactInfo.setText(user.getContactInfo() != null ? user.getContactInfo() : "N/A");

        // Load avatar nếu có
        String imageBase64 = user.getProfilePhoto();
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(decodedBitmap);
        }
    }

}
