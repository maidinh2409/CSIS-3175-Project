package com.example.dory.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dory.R;
import com.example.dory.events.CreateEventActivity;
import com.example.dory.events.EventAdapter;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.User;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class OrganizerActivity extends AppCompatActivity {
    private TextInputEditText eventFilterEditText;
    private EventAdapter eventAdapter;
    private MaterialButton createEventBtn;
    private UserDBHandler db;
    private UserHashed currUser;
    private MaterialTextView headerText;
    public List<Event> eventList = new ArrayList<Event>();
    private MaterialTextView listEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            Intent extra = getIntent();
            if (extra == null) {
                Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
                Log.e("CreateEventActivity", "No user logged in!");
            } else {
                headerText = findViewById(R.id.user_heading);
                currUser = (UserHashed) extra.getSerializableExtra("currUser");
                if (currUser == null) {
                    currUser = new UserHashed("John Doe", "john@example.com", "password", "Organizer", "Organizer", "", "DC", "", 901);
                }
                Log.d("CreateEventActivity", "Current user: " + currUser.getName());
                headerText.setText(String.format("Hello, %s", currUser.getName()));
            }
        }

        db = new UserDBHandler(this);
        eventList = db.viewEventsForOrganizer(Integer.toString(currUser.getUser_id()));
        listEmpty = findViewById(R.id.event_list_empty);
        RecyclerView eventListView = findViewById(R.id.event_list);

        if (eventList.isEmpty()) {
            listEmpty.setVisibility(View.VISIBLE);
            eventListView.setVisibility(View.GONE);
        } else {
            listEmpty.setVisibility(View.GONE);
            eventListView.setVisibility(View.VISIBLE);
        }

        eventListView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(eventList);
        eventListView.setAdapter(eventAdapter);

        eventFilterEditText = findViewById(R.id.event_filter);

        eventFilterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                eventAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        db = new UserDBHandler(this);
        createEventBtn = findViewById(R.id.create_event_btn);
        createEventBtn.setOnClickListener(v -> {
//            db.addNewUser(new User("John doe", "john@example.com", "password", "Organizer", "Organizer"));
//            db.addNewUser(new User("Jane doe", "jane@example.com", "password", "Attendee", "Attendee"));
//            db.addNewUser(new User("Jack doe", "jack@example.com", "password", "Attendee", "Attendee"));
//            db.addNewUser(new User("Jill doe", "jill@example.com", "password", "Attendee", "Attendee"));


//            db.addEvent(2123, currUser.getUser_id(), "Event Title", "Event Description", "2023-10-01", "2023-10-02", "Location", 0);
//            db.addEvent(2124, currUser.getUser_id(), "Event Title 2", "Event Description 2", "2023-10-01", "2023-10-02", "Location 2", 0);
//            db.addEvent(2125, currUser.getUser_id(), "Event Title 3", "Event Description 3", "2023-10-01", "2023-10-02", "Location 3", 0);
//            db.addEvent(2126, currUser.getUser_id(), "Event Title 4", "Event Description 4", "2023-10-01", "2023-10-02", "Location 4", 0);
//            db.addEvent(2127, currUser.getUser_id(), "Event Title 5", "Event Description 5", "2023-10-01", "2023-10-02", "Location 5", 0);
//            db.addEvent(2128, currUser.getUser_id(), "Event Title 6", "Event Description 6", "2023-10-01", "2023-10-02", "Location 6", 0);


            Intent intent = new Intent(this, CreateEventActivity.class);
            intent.putExtra("currUser", currUser);
            startActivity(intent);
        });

    }
}