package com.example.dory.InApp;

import static com.example.dory.InApp.ProfileSetting.PROFILE_UPDATE_REQUEST_CODE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dory.R;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;

public class ProfileActivity extends AppCompatActivity {
    Button changeSetting;
    ImageView profileImage;
    TextView userName, userEmail, userPassword, orgName, userRole, contactInfo;
    private static final int PICK_IMAGE_REQUEST = 1;

    public void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        changeSetting = findViewById(R.id.change_setting_btn);
        profileImage = findViewById(R.id.profile_img);
        userName = findViewById(R.id.user_name_input);
        userEmail = findViewById(R.id.email_input);
        userPassword = findViewById(R.id.password_input);
        orgName = findViewById(R.id.organization_input);
        userRole = findViewById(R.id.role_input);
        contactInfo = findViewById(R.id.contact_input);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("user_email", null);

        if (email == null) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu không có email
            return;
        }

        UserDBHandler dbHelper = new UserDBHandler(this);
        UserHashed user = dbHelper.getUserFromEmail(email);

        if (user == null) {
            Toast.makeText(this, "User data not available!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        userPassword.setText("********");
        orgName.setText(user.getOrganizationName() != null ? user.getOrganizationName() : "N/A");
        userRole.setText(user.getRole());
        contactInfo.setText(user.getContactInfo() != null ? user.getContactInfo() : "N/A");


        if (user.getProfilePhoto() != null) {
            String profilePhoto = String.valueOf(user.getProfilePhoto());

            if (profilePhoto.startsWith("content://") || profilePhoto.startsWith("file://")) {

                Uri imageUri = Uri.parse(profilePhoto);
                Glide.with(this).load(imageUri).into(profileImage);
            } else {

                try {
                    byte[] decodedBytes = Base64.decode(profilePhoto, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    profileImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading profile image!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {

            profileImage.setImageResource(R.drawable.default_profile);
        }


        changeSetting.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileSetting.class);
            startActivityForResult(intent, PROFILE_UPDATE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {

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

        // 🟢 Update ảnh đại diện
        String profilePhoto = String.valueOf(user.getProfilePhoto());
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            if (profilePhoto.startsWith("content://") || profilePhoto.startsWith("file://")) {
                Glide.with(this).load(Uri.parse(profilePhoto)).into(profileImage);
            } else {
                try {
                    byte[] decodedBytes = Base64.decode(profilePhoto, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    profileImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading profile image!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            profileImage.setImageResource(R.drawable.default_profile);
        }
    }



}
