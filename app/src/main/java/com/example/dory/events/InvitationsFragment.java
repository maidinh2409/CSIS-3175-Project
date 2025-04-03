package com.example.dory.events;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dory.R;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.Invitation;
import com.example.dory.userDatabase.UserDBHandler;
import com.example.dory.userDatabase.UserHashed;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class InvitationsFragment extends Fragment implements InvitationAdapter.OnInvitationActionListener {
    private TextInputEditText searchInvitations;
    private MaterialButton searchBtn;
    private UserDBHandler db;
    private UserHashed currUser;
    private List<Invitation> invitationList;
    private RecyclerView invitationsRecyclerView;
    private InvitationAdapter invitationAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null) {
            currUser = (UserHashed) getActivity().getIntent().getSerializableExtra("currUser");
        }
        View view = inflater.inflate(R.layout.fragment_invitations, container, false);
        invitationsRecyclerView = view.findViewById(R.id.invitationsRecyclerView);
        invitationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchInvitations = view.findViewById(R.id.search_invitations);
        searchBtn = view.findViewById(R.id.search_invitations_btn);

        // search button click listener
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invitationAdapter.getFilter().filter(searchInvitations.getText().toString());
            }
        });

        db = new UserDBHandler(getContext());
        invitationList = db.viewInvitationsForAttendeeID(Integer.toString(currUser.getUser_id()));

        // Remove accepted and declined invitations
        invitationList.removeIf(e -> e.getStatus().equalsIgnoreCase("ACCEPTED") || e.getStatus().equalsIgnoreCase("DECLINED"));

        invitationAdapter = new InvitationAdapter(invitationList, this);
        invitationsRecyclerView.setAdapter(invitationAdapter);
        return view;
    }

    /**
     * Called when the accept button is clicked.
     *
     * @param invitation the invitation that was accepted
     */
    public void onAccept(Invitation invitation) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = dateFormat.format(Calendar.getInstance().getTime());
        invitation.setStatus("ACCEPTED");
        boolean res = db.updateInvitation(invitation.invitationID, invitation.eventID, invitation.attendeeID, "ACCEPTED", invitation.creationTime, currentTime);
        if (res) {
            invitationAdapter.removeInvitation(invitation);
        }
        Toast.makeText(getContext(), "Invitation accepted", Toast.LENGTH_SHORT).show();
        Event event = db.getEventById(invitation.eventID);
        addToCalendar(event.getTitle(), event.getDescription(), event.getLocation(), event.getStartDate(), event.getEndDate());
    }

    /**
     * Called when the decline button is clicked.
     *
     * @param invitation the invitation that was declined
     */
    @Override
    public void onDecline(Invitation invitation) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = dateFormat.format(Calendar.getInstance().getTime());
        invitation.setStatus("DECLINED");
        boolean res = db.updateInvitation(invitation.invitationID, invitation.eventID, invitation.attendeeID, "DECLINED", invitation.creationTime, currentTime);
        if (res) {
            invitationAdapter.removeInvitation(invitation);
        }
        Toast.makeText(getContext(), "Invitation declined", Toast.LENGTH_SHORT).show();
    }

    /**
     * called when an invitation is clicked. opens the event activity.
     *
     * @param event
     */
    @Override
    public void onOpen(Event event) {
        Intent intent = new Intent(getContext(), EventActivity.class);
        intent.putExtra("currUser", currUser);
        intent.putExtra("eventObj", event);
        startActivity(intent);
    }


    /**
     * adds an event to the user's calendar app. requires the user to have logged in to their calendar app
     *
     * @param title       Event title
     * @param description Event description
     * @param location    Event location
     * @param startTime   Event start time
     * @param endTime     Event end time
     */
    private void addToCalendar(String title, String description, String location, String startTime, String endTime) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, description);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
        startActivity(intent);
    }
}