package com.example.dory.events;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dory.LoginPages.LoginActivity;
import com.example.dory.R;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class EventsFragment extends Fragment implements EventAdapter.OnEventActionListener {
    private TabLayout eventTabs;
    private RecyclerView eventRecyclerView;
    private UserDBHandler db;
    private UserHashed currUser;
    private EventAdapter acceptedAdapter;
    private EventAdapter pastAdapter;
    private List<Event> acceptedEventList;
    private List<Event> pastEventList;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        if (getActivity() != null) {
            currUser = (UserHashed) getActivity().getIntent().getSerializableExtra("currUser");
            if (currUser == null) {
                Toast.makeText(getContext(), "No user logged in!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        }

        db = new UserDBHandler(getContext());
        eventTabs = view.findViewById(R.id.eventsTabLayout);
        eventRecyclerView = view.findViewById(R.id.eventsRecyclerView);

        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        acceptedEventList = db.getAcceptedEventsForAttendee(currUser.getUser_id());
        pastEventList = db.getPastEventsForAttendee(currUser.getUser_id());

        acceptedAdapter = new EventAdapter(acceptedEventList, false, this);
        pastAdapter = new EventAdapter(pastEventList, false, this);
        eventRecyclerView.setAdapter(acceptedAdapter);

        // Tab selection listener
        eventTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    eventRecyclerView.setAdapter(acceptedAdapter);
                } else {
                    eventRecyclerView.setAdapter(pastAdapter);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

    /**
     * opens the event details activity for the selected event
     * @param event Event object
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

    /**
     * @param event
     */
    @Override
    public void onDelete(Event event) {
    }
}
