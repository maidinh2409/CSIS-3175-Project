package com.example.dory.InApp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dory.R;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;
import com.example.dory.userDatabase.User;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileSetting extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    static final int PROFILE_UPDATE_REQUEST_CODE = 1001;
    private UserDBHandler userDBHelper;
    private String currentUserEmail;

    Button updateSetting, editImage;
    ImageView profileImage;
    Uri imageUri;
    TextView userName_s, orgName_s, contactInfo_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        userDBHelper = new UserDBHandler(this);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("user_email", null);

        if (currentUserEmail == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialToolbar appBar = findViewById(R.id.profileSAppBar);
        appBar.setNavigationOnClickListener(view -> finish());

        updateSetting = findViewById(R.id.change_setting_btn_s);
        editImage = findViewById(R.id.change_img_btn_s);
        profileImage = findViewById(R.id.profile_img_s);

        userName_s = findViewById(R.id.user_name_input_s);
        orgName_s = findViewById(R.id.organization_input_s);
        contactInfo_s = findViewById(R.id.contact_input_s);

        loadUserProfile();

        editImage.setOnClickListener(v -> openGallery());
        updateSetting.setOnClickListener(v -> updateUserProfile());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE_UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            loadUserProfile(); // Reload user profile data after updating
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadUserProfile() {
        UserHashed user = userDBHelper.getUserFromEmail(currentUserEmail);
        if (user == null) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userName_s.setText(user.getName());
        orgName_s.setText(user.getOrganizationName() != null ? user.getOrganizationName() : "N/A");
        contactInfo_s.setText(user.getContactInfo() != null ? user.getContactInfo() : "N/A");

        // Load avatar nếu tồn tại
        if (user.getProfilePhoto() != null) {
            String photoString = String.valueOf(user.getProfilePhoto());

            if (photoString.startsWith("content://") || photoString.startsWith("file://")) {
                // Nếu là URI, dùng setImageURI()
                Uri imageUri = Uri.parse(photoString);
                profileImage.setImageURI(imageUri);
            } else {
                // Nếu là Base64, giải mã và hiển thị
                try {
                    byte[] decodedBytes = Base64.decode(photoString, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    profileImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading profile image!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateUserProfile() {
        UserHashed hashedUser = userDBHelper.getUserFromEmail(currentUserEmail);
        if (hashedUser == null) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(
                userName_s.getText().toString(),
                currentUserEmail,
                orgName_s.getText().toString(),
                contactInfo_s.getText().toString());

        if (imageUri != null) {
            String imagePath = imageUri.toString(); // Lưu URI dạng chuỗi vào database
            user.setProfilePhoto(imagePath);
        }

        boolean updateSuccess = userDBHelper.updateUser(user);
        if (updateSuccess) {
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Failed to update!", Toast.LENGTH_SHORT).show();
        }
    }
}
