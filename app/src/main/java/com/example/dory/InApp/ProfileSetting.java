package com.example.dory.InApp;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.dory.R;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;
import com.example.dory.userDatabase.User;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileSetting extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private UserDBHandler userDBHelper;
    private String currentUserEmail;

    Button updateSetting, editImage;
    ImageView profileImage;
    Uri imageUri;
    TextView userName_s, email_s, password_s, c_password_s, orgName_s, role_s, contactInfo_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        userDBHelper = new UserDBHandler(this);

        // Lấy email từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("user_email", null);

        if (currentUserEmail == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy email người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ UI
        updateSetting = findViewById(R.id.change_setting_btn_s);
        editImage = findViewById(R.id.change_img_btn_s);
        profileImage = findViewById(R.id.profile_img_s);

        userName_s = findViewById(R.id.user_name_input_s);
        email_s = findViewById(R.id.email_input_s);
        password_s = findViewById(R.id.password_input_s);
        c_password_s = findViewById(R.id.c_password_input_s);
        orgName_s = findViewById(R.id.organization_input_s);
        role_s = findViewById(R.id.role_input_s);
        contactInfo_s = findViewById(R.id.contact_input_s);

        // Load thông tin user từ database
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi xử lý ảnh!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadUserProfile() {
        UserHashed user = userDBHelper.getUserFromEmail(currentUserEmail);
        if (user == null) {
            Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userName_s.setText(user.getName());
        email_s.setText(user.getEmail());
        password_s.setText("********"); // Không hiển thị password thật
        orgName_s.setText(user.getOrganizationName() != null ? user.getOrganizationName() : "N/A");
        role_s.setText(user.getRole());
        contactInfo_s.setText(user.getContactInfo() != null ? user.getContactInfo() : "N/A");

        // Load avatar nếu có
        String imageBase64 = user.getProfilePhoto();
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(decodedBitmap);
        }
    }

    private void updateUserProfile() {
        UserHashed hashedUser = userDBHelper.getUserFromEmail(currentUserEmail);
        if (hashedUser == null) {
            Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(
                userName_s.getText().toString()
        );

        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                user.setProfilePhoto(encodedImage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi xử lý ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String newPassword = password_s.getText().toString().trim();
        String confirmPassword = c_password_s.getText().toString().trim();
        boolean passwordUpdated = false;

        if (!newPassword.isEmpty() && newPassword.equals(confirmPassword)) {
            passwordUpdated = userDBHelper.updateUserPassword(currentUserEmail, newPassword);
        } else if (!newPassword.isEmpty()) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean updateSuccess = userDBHelper.updateUser(user);
        if (updateSuccess || passwordUpdated) {
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}