package com.example.dory.events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity {
    private UserDBHandler db;
    private Event currEvent;
    private TextInputEditText eventNameInput;
    private TextInputEditText descriptionInput;
    private TextInputEditText locationInput;
    private TextInputEditText dateInput;
    private TextInputEditText capacityInput;
    private TextInputEditText durationInput;
    private MaterialButton updateBtn;

    private UserHashed currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new UserDBHandler(this);
        if (getIntent() == null) {
            Toast.makeText(this, "Error fetching event and user", Toast.LENGTH_SHORT).show();
        }
        currEvent = (Event) getIntent().getSerializableExtra("eventObj");
        currUser = (UserHashed) getIntent().getSerializableExtra("currUser");

        if (currEvent == null || currUser == null) {
            Toast.makeText(this, "Error fetching event and user", Toast.LENGTH_SHORT).show();
            finish();
        }

        eventNameInput = findViewById(R.id.update_event_name);
        descriptionInput = findViewById(R.id.update_event_desc);
        locationInput = findViewById(R.id.update_event_location);
        dateInput = findViewById(R.id.update_event_date);
        capacityInput = findViewById(R.id.update_event_capacity);
        durationInput = findViewById(R.id.update_event_duration);
        updateBtn = findViewById(R.id.update_btn);
        setInitialValues();

        MaterialToolbar appbar = findViewById(R.id.topAppBar);
        appbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // update button click listener to update event in database
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    String date = dateInput.getText().toString().trim();
                    int duration = Integer.parseInt(durationInput.getText().toString().trim());
                    String eventName = eventNameInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();
                    String location = locationInput.getText().toString().trim();
                    String eventCapacity = capacityInput.getText().toString().trim();


                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(format.parse(date));
                        String StartDateTime = format.format(calendar.getTime());
                        calendar.add(Calendar.HOUR_OF_DAY, duration);
                        String EndDateTime = format.format(calendar.getTime());

                        boolean result = db.updateEvent(currEvent.getEventID(), currUser.getUser_id(), eventName, description, StartDateTime, EndDateTime, location, Integer.parseInt(eventCapacity));
                        if (!result) {
                            Toast.makeText(EditEventActivity.this, "Error updating event after result", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditEventActivity.this, "Event updated!", Toast.LENGTH_LONG).show();
                            clearForm();
                            db.close();
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(EditEventActivity.this, "Error updating event", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });
    }

    /**
     * sets the initial values of the input fields.
     */
    private void setInitialValues() {
        eventNameInput.setText(currEvent.getTitle());
        descriptionInput.setText(currEvent.getDescription());
        locationInput.setText(currEvent.getLocation());
        dateInput.setText(currEvent.getStartDate());
        capacityInput.setText(String.format(Locale.getDefault(), "%d", currEvent.getCapacity()));
    }

    /**
     * clears all input fields in the form.
     */
    private void clearForm() {
        eventNameInput.setText("");
        descriptionInput.setText("");
        locationInput.setText("");
        dateInput.setText("");
        capacityInput.setText("");
        durationInput.setText("");
    }

    /**
     * validates all input fields in the form.
     *
     * @return boolean
     */
    private boolean validateForm() {
        boolean isValid = true;
        if (eventNameInput.getText() == null || eventNameInput.getText().toString().trim().isEmpty()) {
            eventNameInput.setError("Event name is required");
            isValid = false;
        } else if (eventNameInput.getText().toString().length() > 30) {
            eventNameInput.setError("Event name must be less than 30 characters");
            isValid = false;
        }
        if (descriptionInput.getText() == null || descriptionInput.getText().toString().trim().isEmpty()) {
            descriptionInput.setError("Event description is required");
            isValid = false;
        } else if (descriptionInput.getText().toString().length() > 200) {
            descriptionInput.setError("Event description must be less than 200 characters");
            isValid = false;
        }
        if (locationInput.getText() == null || locationInput.getText().toString().trim().isEmpty()) {
            locationInput.setError("Event location is required");
            isValid = false;
        } else if (locationInput.getText().toString().length() > 40) {
            locationInput.setError("Event location must be less than 40 characters");
            isValid = false;
        }
        if (dateInput.getText() == null || dateInput.getText().toString().trim().isEmpty()) {
            dateInput.setError("Event date is required");
            isValid = false;
        }
        if (capacityInput.getText() == null || capacityInput.getText().toString().trim().isEmpty()) {
            capacityInput.setError("Event capacity is required");
            isValid = false;
        } else {
            try {
                int capacity = Integer.parseInt(capacityInput.getText().toString());
                if (capacity <= 0) {
                    capacityInput.setError("Event capacity must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                capacityInput.setError("Invalid event capacity");
                isValid = false;
            }
        }
        if (durationInput.getText() == null || durationInput.getText().toString().trim().isEmpty()) {
            durationInput.setError("Event duration is required");
            isValid = false;
        } else {
            try {
                int duration = Integer.parseInt(durationInput.getText().toString());
                if (duration <= 0) {
                    durationInput.setError("Event duration must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                durationInput.setError("Invalid event duration");
                isValid = false;
            }
        }
        return isValid;
    }
}