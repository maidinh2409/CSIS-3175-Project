package com.example.dory.events;

import android.os.Bundle;
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
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class UserSelectionActivity extends AppCompatActivity {
    private UserAdapter userAdapter;
    private MaterialButton sendInvitationButton;
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


        db = new UserDBHandler(this);
        if (getIntent() == null) {
            Toast.makeText(this, "Error: Intent is null", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        event = (Event) getIntent().getSerializableExtra("eventObj");
        organizerId = getIntent().getIntExtra("organizerId", -1);
        MaterialToolbar appBar = findViewById(R.id.topAppBar);
        appBar.setNavigationOnClickListener(view -> finish());

        if (event == null || organizerId == -1) {
            Toast.makeText(this, "Error: Event or Organizer ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        RecyclerView userListView = findViewById(R.id.user_selection_list);
        TextView listEmpty = findViewById(R.id.list_empty);
        userListView.setLayoutManager(new LinearLayoutManager(this));

        List<UserHashed> userList = db.getAllUsers();

        sendInvitationButton = findViewById(R.id.send_invitations_button);

        if (userList.isEmpty()) {
            listEmpty.setVisibility(View.VISIBLE);
            userListView.setVisibility(View.GONE);
            sendInvitationButton.setVisibility(View.GONE);
        } else {
            userList.removeIf(user -> !user.getRole().equalsIgnoreCase("attendee"));
            userAdapter = new UserAdapter(userList);
            userListView.setAdapter(userAdapter);
        }

        sendInvitationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<UserHashed> selectedUsers = userAdapter.getSelectedUsers();
                if (selectedUsers.isEmpty()) {
                    Toast.makeText(UserSelectionActivity.this, "Please select at least one user", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedUsers.size() > event.getCapacity()) {
                    Toast.makeText(UserSelectionActivity.this, "Cannot invite more than the capacity of the event. Current capacity: " + event.getCapacity(), Toast.LENGTH_LONG).show();
                    return;
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String currentTime = dateFormat.format(Calendar.getInstance().getTime());
                for (UserHashed user : selectedUsers) {
                    db.addInvitation(generateRandomId(), event.getEventID(), user.getUser_id(), "PENDING", currentTime);
                }
                Toast.makeText(UserSelectionActivity.this, "Invitations sent successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        db.close();
    }

    /**
     * generates a random id for the invitation
     * @return a random id
     */
    private int generateRandomId() {
        return ((UUID.randomUUID().hashCode() & 0x7fffffff)
                + (int) (System.currentTimeMillis() % 100000)) & 0x7fffffff;
    }

}