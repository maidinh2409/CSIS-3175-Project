package com.example.dory.events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dory.LoginPages.LoginActivity;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.example.dory.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {
    private TextInputEditText dateInput;
    private TextInputEditText locationInput;
    private TextInputEditText descriptionInput;
    private TextInputEditText eventNameInput;
    private TextInputEditText capacityInput;
    private TextInputEditText durationInput;
    private UserDBHandler userDb;
    private UserHashed currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userDb = new UserDBHandler(this);
        locationInput = findViewById(R.id.event_location_input);
        descriptionInput = findViewById(R.id.event_desc_input);
        eventNameInput = findViewById(R.id.event_name_input);
        capacityInput = findViewById(R.id.event_capacity_input);
        durationInput = findViewById(R.id.event_duration_input);

        if (savedInstanceState == null) {
            Intent extra = getIntent();
            if (extra == null) {
                Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                currUser = (UserHashed) extra.getSerializableExtra("currUser");
                if (currUser == null) {
                    Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                }
            }
        }

        MaterialButton createBtn = findViewById(R.id.create_event_btn);
        createBtn.setOnClickListener(view -> {
            if (!validateForm()) {
                return;
            }
            String eventName = Objects.requireNonNull(eventNameInput.getText()).toString();
            String description = Objects.requireNonNull(descriptionInput.getText()).toString();
            String location = Objects.requireNonNull(locationInput.getText()).toString();
            String dateTime = Objects.requireNonNull(dateInput.getText()).toString();
            int eventCapacity = Integer.parseInt(Objects.requireNonNull(capacityInput.getText()).toString());
            int duration = Integer.parseInt(Objects.requireNonNull(durationInput.getText()).toString());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Objects.requireNonNull(format.parse(dateTime)));

                String StartDateTime = format.format(calendar.getTime());
                calendar.add(Calendar.HOUR_OF_DAY, duration);
                String EndDateTime = format.format(calendar.getTime());
                int eventID = generateRandomId();
                Event event = new Event(eventID, currUser.getUser_id(), eventName, description, StartDateTime, EndDateTime, location, eventCapacity);

                boolean result = userDb.addEvent(event.eventID, currUser.getUser_id(), eventName, description, StartDateTime, EndDateTime, location, eventCapacity);
                if (!result) {
                    Toast.makeText(CreateEventActivity.this, "Error creating event", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateEventActivity.this, "Event created!", Toast.LENGTH_LONG).show();
                    clearForm();
                    startActivity(new Intent(CreateEventActivity.this, UserSelectionActivity.class)
                            .putExtra("eventObj", event)
                            .putExtra("organizerId", currUser.getUser_id()));
                    userDb.close();
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(CreateEventActivity.this, "Error creating event", Toast.LENGTH_SHORT).show();
            }
        });

        MaterialToolbar appBar = findViewById(R.id.topAppBar);
        appBar.setNavigationOnClickListener(view -> finish());

        // Dialog to select date and time
        dateInput = findViewById(R.id.event_datetime_input);
        dateInput.setOnClickListener(view -> showDatePickerDialog());
    }

    /**
     * generates a random ID using UUID and current timestamp. Ensures uniqueness and is always positive
     *
     * @return random ID
     */
    private int generateRandomId() {
        return ((UUID.randomUUID().hashCode() & 0x7fffffff)
                + (int) (System.currentTimeMillis() % 100000)) & 0x7fffffff;
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


    /**
     * shows a date picker dialog to select a date and time.
     */
    private void showDatePickerDialog() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now());
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).setCalendarConstraints(constraintsBuilder.build()).build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            showMaterialTimePicker(calendar);
        });

    }

    /**
     * shows a time picker dialog to select a time.
     *
     * @param dateCalendar the calendar object to set the time on
     */
    private void showMaterialTimePicker(Calendar dateCalendar) {
        int hour = dateCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = dateCalendar.get(Calendar.MINUTE);

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTitleText("Select time").setTimeFormat(TimeFormat.CLOCK_24H).setHour(hour).setMinute(minute).build();

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");

        timePicker.addOnPositiveButtonClickListener(view -> {
            int selectedHour = timePicker.getHour();
            int selectedMinute = timePicker.getMinute();
            dateCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            dateCalendar.set(Calendar.MINUTE, selectedMinute);
            dateCalendar.set(Calendar.SECOND, 0);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String finalDateTime = format.format(dateCalendar.getTime());

            dateInput.setText(finalDateTime);
        });
    }

}