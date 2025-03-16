package com.example.dory;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateEventActivity extends AppCompatActivity {
    private TextInputEditText dateBtn;
    private TextInputEditText inviteesBtn;
    private TextInputEditText locationBtn;
    private TextInputEditText descriptionBtn;
    private TextInputEditText nameBtn;
    private final String[] invitees = {"John Doe", "Jane Smith", "Alice Johnson"};
    private final boolean[] checkedInvitees = new boolean[invitees.length];
    private ArrayList<String> selectedInvitees = new ArrayList<>();

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

        locationBtn = findViewById(R.id.event_location_input);
        descriptionBtn = findViewById(R.id.event_desc_input);
        nameBtn = findViewById(R.id.event_name_input);

        MaterialButton createBtn = findViewById(R.id.create_event_btn);
        createBtn.setOnClickListener(view -> {
            String s = String.format("name: %s\ndate: %s\nlocation: %s\ndescription: %s\ninvitees: %s",
                    Objects.requireNonNull(nameBtn.getText()),
                    Objects.requireNonNull(dateBtn.getText()),
                    Objects.requireNonNull(locationBtn.getText()),
                    Objects.requireNonNull(descriptionBtn.getText()),
                    String.join(", ", selectedInvitees));
            Log.d("CreateEventActivity", s);
            Toast.makeText(CreateEventActivity.this, s, Toast.LENGTH_LONG).show();
        });


        MaterialToolbar appBar = findViewById(R.id.topAppBar);
        appBar.setNavigationOnClickListener(view -> finish());

        inviteesBtn = findViewById(R.id.select_invitees_btn);
        inviteesBtn.setOnClickListener(view -> showInviteesDialog());

        dateBtn = findViewById(R.id.event_datetime_input);
        dateBtn.setOnClickListener(view -> showDatePickerDialog());


    }

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

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String finalDateTime = format.format(dateCalendar.getTime());

            dateBtn.setText(finalDateTime);

            Toast.makeText(CreateEventActivity.this, "Selected: " + finalDateTime, Toast.LENGTH_LONG).show();
        });
    }

    private void showInviteesDialog() {
        new MaterialAlertDialogBuilder(this).setTitle("Select Invitees").setMultiChoiceItems(invitees, checkedInvitees, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedInvitees[which] = isChecked;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int count = 0;
                for (int i = 0; i < invitees.length; i++) {
                    if (checkedInvitees[i]) {
                        count++;
                        selectedInvitees.add(invitees[i]);
                    }
                }
                if (count > 0) {
                    String inviteesText = count + " invitee" + (count > 1 ? "s" : "") + " selected";
                    inviteesBtn.setText(inviteesText);
                } else {
                    inviteesBtn.setText("");
                    Toast.makeText(CreateEventActivity.this, "No invitees selected", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", null).show();
    }

}