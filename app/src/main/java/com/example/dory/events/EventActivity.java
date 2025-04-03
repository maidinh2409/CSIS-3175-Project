package com.example.dory.events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dory.R;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {
    private MaterialTextView event_name, event_date, event_location, event_desc, event_attendees, event_capacity;
    private MaterialButton editBtn, deleteBtn, attendeeListBtn;
    private UserDBHandler db;
    private UserHashed currUser;
    private List<UserHashed> attendees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        event_name = findViewById(R.id.event_name);
        event_date = findViewById(R.id.event_date);
        event_location = findViewById(R.id.event_location);
        event_desc = findViewById(R.id.event_desc);
        event_attendees = findViewById(R.id.event_attendees);
        event_capacity = findViewById(R.id.event_capacity);
        editBtn = findViewById(R.id.edit_btn);
        deleteBtn = findViewById(R.id.deleteEventBtn);
        attendeeListBtn = findViewById(R.id.view_attendee_list_btn);

        MaterialToolbar appBar = findViewById(R.id.topAppBar);
        appBar.setNavigationOnClickListener(view -> finish());

        db = new UserDBHandler(this);

        Intent intent = getIntent();
        Event event = (Event) intent.getSerializableExtra("eventObj");
        currUser = (UserHashed) intent.getSerializableExtra("currUser");
        if (event == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (currUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (currUser.getRole().equalsIgnoreCase("attendee")) {
            editBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            attendeeListBtn.setVisibility(View.GONE);
        }

        attendees = db.getAttendeesForEventId(event.getEventID());
        event_attendees.setText(String.format(Locale.getDefault(), "Attendees: %d", attendees.size()));
        event_capacity.setText(String.format(Locale.getDefault(), "Capacity: %d", event.getCapacity()));
        event_name.setText(event.getTitle());
        event_date.setText(event.getStartDate());
        event_location.setText(event.getLocation());
        event_desc.setText(event.getDescription());

        editBtn.setOnClickListener(view -> {
            Intent newIntent = new Intent(EventActivity.this, EditEventActivity.class);
            newIntent.putExtra("eventObj", event);
            newIntent.putExtra("currUser", currUser);
            startActivity(newIntent);
            finish();
        });

        attendeeListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAttendeeListDialog();
            }
        });

        // delete event button. shows a confirmation dialog before deleting the event
        deleteBtn.setOnClickListener(view -> {
            MaterialAlertDialogBuilder deleteDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("Are you sure you want to delete this event?")
                    .setNeutralButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (db.delEvent(event.getEventID())) {
                            Toast.makeText(this, "Event deleted successfully: ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                        }
                    });
            deleteDialog.show();
        });
    }

    /**
     * shows a dialog with a list of attendees for the event
     */
    private void showAttendeeListDialog() {
        List<String> attendeeNames = new ArrayList<>();
        for (UserHashed attendee : attendees) {
            attendeeNames.add(attendee.getName());
        }
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.attendee_item_layout, R.id.attendee_list_item, attendeeNames);
            new MaterialAlertDialogBuilder(this).setTitle("Attendee List")
                    .setPositiveButton("OK", null)
                    .setAdapter(adapter, null)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "Error showing attendee list dialog", Toast.LENGTH_SHORT).show();

        }
    }

}