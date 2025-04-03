package com.example.dory.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dory.InApp.ProfileActivity;
import com.example.dory.R;
import com.example.dory.events.CreateEventActivity;
import com.example.dory.events.EventActivity;
import com.example.dory.events.EventAdapter;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class OrganizerActivity extends AppCompatActivity implements EventAdapter.OnEventActionListener {
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
            } else {
                headerText = findViewById(R.id.user_heading);
                currUser = (UserHashed) extra.getSerializableExtra("currUser");
                if (currUser == null) {
                    Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                headerText.setText(String.format("Hello, %s", currUser.getName()));
            }
        }

        MaterialToolbar appBar = findViewById(R.id.topAppBar);
        appBar.setNavigationOnClickListener(view -> finish());
        appBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.profileIcon) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("currUser", currUser);
                startActivity(intent);
                return true;
            }
            return false;
        });

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
        eventAdapter = new EventAdapter(eventList, true, this);
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
            Intent intent = new Intent(this, CreateEventActivity.class);
            intent.putExtra("currUser", currUser);
            startActivity(intent);
        });

    }

    /**
     * refreshes the event list when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        eventList.clear();
        eventList.addAll(db.viewEventsForOrganizer(Integer.toString(currUser.getUser_id())));
        eventAdapter.notifyDataSetChanged();
    }

    /**
     * opens the event activity when an event is clicked
     *
     * @param event
     */
    @Override
    public void onOpen(Event event) {
        if (event != null) {
            Intent intent = new Intent(this, EventActivity.class);
            intent.putExtra("currUser", currUser);
            intent.putExtra("eventObj", event);
            startActivity(intent);
        }
        ;
    }

    /**
     * deletes an event from the database and updates the event list
     *
     * @param event
     */
    @Override
    public void onDelete(Event event) {
        MaterialAlertDialogBuilder deleteDialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Are you sure you want to delete this event?")
                .setNeutralButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    db = new UserDBHandler(this);
                    if (db.delEvent(event.getEventID())) {
                        eventAdapter.removeEvent(event);
                        Toast.makeText(this, "Event deleted successfully: ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                    }
                    db.close();
                });
        deleteDialog.show();
    }
}