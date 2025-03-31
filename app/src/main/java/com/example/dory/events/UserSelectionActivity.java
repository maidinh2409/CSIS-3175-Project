package com.example.dory.events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dory.R;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.User;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class UserSelectionActivity extends AppCompatActivity {
    public static final String EXTRA_SELECTED_USER_ID = "selected_user_id";
    public static final String EXTRA_SELECTED_USER_NAME = "selected_user_name";
    private UserAdapter userAdapter;

    private MaterialButton sendInivitationButton;
    private UserDBHandler db;
    private int organizerId;
    private Event event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //            db.addEvent(2123, currUser.getUser_id(), "Event Title", "Event Description", "2023-10-01", "2023-10-02", "Location", 0);

        db = new UserDBHandler(this);
        UserHashed tempUser = db.getUserFromEmail("esc@gmail.com");
        event = new Event(2223, tempUser.getUser_id(), "Event Title", "Event Description", "2023-10-01", "2023-10-02", "Location", 0);
//        event = (Event) getIntent().getSerializableExtra("eventObj");
//        organizerId = getIntent().getIntExtra("organizerId", -1);
        organizerId = tempUser.getUser_id();

        if (event == null || organizerId == -1) {
            Toast.makeText(this, "Error: Event or Organizer ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        RecyclerView userListView = findViewById(R.id.user_selection_list);
        TextView listEmpty = findViewById(R.id.list_empty);
        userListView.setLayoutManager(new LinearLayoutManager(this));

        List<UserHashed> userList = db.getAllUsers();
        Log.d("UserSelectionActivity", "User list size: " + userList.size());

        sendInivitationButton = findViewById(R.id.send_invitations_button);

        if (userList.isEmpty()) {
            listEmpty.setVisibility(View.VISIBLE);
            userListView.setVisibility(View.GONE);
            sendInivitationButton.setVisibility(View.GONE);
        } else {
            userList.removeIf(user -> !user.getRole().equalsIgnoreCase("attendee"));
            userAdapter = new UserAdapter(userList);
            userListView.setAdapter(userAdapter);
        }

        sendInivitationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<UserHashed> selectedUsers = userAdapter.getSelectedUsers();
                if (selectedUsers.isEmpty()) {
                    Toast.makeText(UserSelectionActivity.this, "Please select at least one user", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String currentTime = dateFormat.format(Calendar.getInstance().getTime());
                for (UserHashed user : selectedUsers) {
                    Log.d("UserSelectionActivity", "Selected user: " + user.getName());
                    db.addInvitation(generateRandomId(), event.eventID, user.getUser_id(), "pending", currentTime);
                }
            }
        });

        db.close();
    }
    private int generateRandomId() {
        return ((UUID.randomUUID().hashCode() & 0x7fffffff)
                + (int) (System.currentTimeMillis() % 100000)) & 0x7fffffff;
    }

}