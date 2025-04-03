package com.example.dory.events;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dory.R;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdvancedSearchFragment extends Fragment implements EventAdapter.OnEventActionListener {
    private TextInputEditText organizerEditText;
    private TextInputEditText titleEditText;
    private TextInputEditText dateEditText;
    private MaterialButton searchButton;
    private RecyclerView searchResultsRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> allEvents;
    private List<Event> filteredEvents;
    private UserDBHandler db;
    private UserHashed currUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advanced_search, container, false);

        if (getActivity() != null) {
            currUser = (UserHashed) getActivity().getIntent().getSerializableExtra("currUser");
        }

        organizerEditText = view.findViewById(R.id.organizerEditText);
        titleEditText = view.findViewById(R.id.titleEditText);
        dateEditText = view.findViewById(R.id.dateEditText);
        searchButton = view.findViewById(R.id.searchButton);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        db = new UserDBHandler(getContext());


        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        allEvents = db.getAllEventsForAttendee(currUser.getUser_id());
        filteredEvents = new ArrayList<>(allEvents);

        eventAdapter = new EventAdapter(filteredEvents, false, this);
        searchResultsRecyclerView.setAdapter(eventAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        return view;
    }

    /**
     * Filters the list of events based on the organizer's name, event title, and event start date.
     * The filtered results are then displayed in the RecyclerView.
     */
    private void performSearch() {
        String organizerQuery = organizerEditText.getText().toString().trim();
        String titleQuery = titleEditText.getText().toString().trim();

        // Expected as yyyy-MM-dd
        String dateQuery = dateEditText.getText().toString().trim();

        filteredEvents.clear();

        for (Event event : allEvents) {
            boolean matches = true;

            // organizer name filter
            if (!organizerQuery.isEmpty()) {
                UserHashed organizer = db.getUserFromId(event.getOrganizerID());
                if (organizer == null || !organizer.getName().equalsIgnoreCase(organizerQuery)) {
                    matches = false;
                }
            }

            // Title filter.
            if (matches && !titleQuery.isEmpty()) {
                if (!event.title.toLowerCase().contains(titleQuery.toLowerCase())) {
                    matches = false;
                }
            }

            // date filter by start date
            if (matches && !dateQuery.isEmpty()) {
                if (!(event.startDate.startsWith(dateQuery))) {
                    matches = false;
                }
            }

            if (matches) {
                filteredEvents.add(event);
            }
        }

        eventAdapter.notifyDataSetChanged();
    }

    /**
     * @param event the event to view details for
     */
    @Override
    public void onOpen(Event event) {
        if (event != null) {
            Intent intent = new Intent(getActivity(), EventActivity.class);
            intent.putExtra("currUser", currUser);
            intent.putExtra("eventObj", event);
            startActivity(intent);
        }
    }

    @Override
    public void onDelete(Event event) {

    }
}
