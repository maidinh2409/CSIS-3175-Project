package com.example.dory.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dory.R;
import com.google.android.material.textfield.TextInputEditText;

public class InvitationsFragment extends Fragment {
    private TextInputEditText searchInvitations;
    private RecyclerView invitationsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invitations, container, false);
        searchInvitations = view.findViewById(R.id.search_invitations);
        invitationsRecyclerView = view.findViewById(R.id.invitationsRecyclerView);
        invitationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }
}