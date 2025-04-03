package com.example.dory.events;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dory.R;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.Invitation;
import com.example.dory.userDatabase.UserDBHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {
    private List<Invitation> invitationList;
    private List<Invitation> invitationListFull;
    private UserDBHandler db;
    private OnInvitationActionListener listener;

    /**
     * interface for handling invitation actions
     */
    public interface OnInvitationActionListener {
        /**
         * called when the accept button is clicked.
         *
         * @param invitation the invitation that was accepted
         */
        void onAccept(Invitation invitation);

        /**
         * called when the decline button is clicked.
         *
         * @param invitation the invitation that was declined
         */
        void onDecline(Invitation invitation);

        void onOpen(Event event);
    }

    /**
     * constructor for the InvitationAdapter
     *
     * @param invitationList the list of invitations to display
     * @param listener       the listener for invitation actions
     */
    public InvitationAdapter(List<Invitation> invitationList, OnInvitationActionListener listener) {
        this.invitationList = invitationList;
        this.listener = listener;
        this.invitationListFull = new ArrayList<>(invitationList);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = new UserDBHandler(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invitation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Invitation invitation = invitationList.get(position);
        Event event = db.getEventById(invitation.getEventID());

        // if the event is null, hide the invitation card
        if (event == null) {
            holder.invitationCard.setVisibility(View.GONE);
            holder.invitationName.setVisibility(View.GONE);
            holder.invitationDate.setVisibility(View.GONE);
            holder.invitationLocation.setVisibility(View.GONE);
            holder.invitationDescription.setVisibility(View.GONE);

        } else {
            holder.invitationDate.setText(String.format("%s - %s", event.getStartDate(), event.getEndDate()));
            holder.invitationName.setText(event.getTitle());
            holder.invitationLocation.setText(event.getLocation());
            holder.invitationDescription.setText(event.getDescription());
        }

        // Handles the accept button click.
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("InvitationAdapter", "Accept button clicked");
                if (listener != null) {
                    listener.onAccept(invitation);
                }
            }
        });

        // Handles the decline button click.
        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onDecline(invitation);
                }
            }
        });

        holder.invitationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onOpen(event);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return invitationList.size();
    }

    // removes an invitation from the list.
    public void removeInvitation(Invitation invitation) {
        int position = invitationList.indexOf(invitation);
        if (position != -1) {
            invitationList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // filters the list of invitations based on the search query.
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Invitation> filteredList = new ArrayList<>();
                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.addAll(invitationListFull);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();
                    for (Invitation invitation : invitationListFull) {
                        Event event = db.getEventById(invitation.getEventID());

                        if (event != null && event.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(invitation);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }


            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                invitationList.clear();
                invitationList.addAll((List<Invitation>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    ;


    /**
     * ViewHolder class for the InvitationAdapter.
     * Holds the views for each invitation item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView invitationName;
        private MaterialTextView invitationDate;
        private MaterialTextView invitationLocation;
        private MaterialTextView invitationDescription;
        private MaterialButton acceptButton;
        private MaterialButton declineButton;
        private MaterialCardView invitationCard;

        public ViewHolder(View itemView) {
            super(itemView);
            invitationName = itemView.findViewById(R.id.eventNameTextView);
            invitationDate = itemView.findViewById(R.id.eventDateTimeTextView);
            invitationLocation = itemView.findViewById(R.id.eventLocationTextView);
            invitationDescription = itemView.findViewById(R.id.eventDescriptionTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
            invitationCard = itemView.findViewById(R.id.invitation_card);
        }
    }
}
