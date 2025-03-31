package com.example.dory.events;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dory.R;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.UserDBHandler;
import com.google.android.material.textview.MaterialTextView;

public class EventActivity extends AppCompatActivity {
    private MaterialTextView event_name, event_date, event_location, event_desc, event_attendees, event_capacity;
    private UserDBHandler db;

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

        Intent intent = getIntent();
        Event event = (Event) intent.getSerializableExtra("eventObj");

        event_name = findViewById(R.id.event_name);
        event_date = findViewById(R.id.event_date);
        event_location = findViewById(R.id.event_location);
        event_desc = findViewById(R.id.event_desc);
        event_attendees = findViewById(R.id.event_attendees);
        event_capacity = findViewById(R.id.event_capacity);

        db = new UserDBHandler(this);
        if (event != null) {
            int attendees = db.viewInvitationsForAttendeeID(Integer.toString(event.getEventID())).size();
            event_attendees.setText("Attendees: " + attendees);
            event_capacity.setText("Capacity: " + event.getCapacity());

            event_name.setText(event.getTitle());
            event_date.setText(event.getStartDate());
            event_location.setText(event.getLocation());
            event_desc.setText(event.getDescription());
        }


    }
}