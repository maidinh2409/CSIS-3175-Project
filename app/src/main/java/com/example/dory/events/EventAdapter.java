package com.example.dory.events;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dory.R;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.UserDBHandler;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private List<Event> eventList;
    private List<Event> eventListFull;
    private UserDBHandler db;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
        this.eventListFull = new ArrayList<>(eventList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getEventName().setText(eventList.get(position).getTitle());
        holder.getEventDate().setText(eventList.get(position).getStartDate());
        holder.getEventLocation().setText(eventList.get(position).getLocation());
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(view.getContext(), holder.getAdapterPosition());
            }
        });
    }

    private void showDeleteDialog(Context context, int position) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Are you sure you want to delete this event?")
                .setNeutralButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Handle delete action
                    db = new UserDBHandler(context);
                    Event event = eventList.get(position);
                    db.delEvent(event.getEventID());
                    eventList.remove(position);
                    eventListFull.remove(event);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Event deleted successfully: " + position, Toast.LENGTH_SHORT).show();
                    db.close();
                })
                .show();
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Event> filteredList = new ArrayList<>();
                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.addAll(eventListFull);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();
                    for (Event event : eventListFull) {
                        if (event.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(event);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                Log.d("EventAdapter", "publishResults called with " + filterResults.values.toString());
                eventList.clear();
                eventList.addAll((List<Event>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventName;
        private final TextView eventDate;
        private final TextView eventLocation;
        private final Button deleteButton;
        private final Button openButton;

        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.textViewEventName);
            eventDate = view.findViewById(R.id.textViewEventDate);
            eventLocation = view.findViewById(R.id.textViewEventLocation);
            deleteButton = view.findViewById(R.id.deleteEventBtn);
            openButton = view.findViewById(R.id.openBtn);
        }


        public TextView getEventName() {
            return eventName;
        }

        public TextView getEventDate() {
            return eventDate;
        }

        public TextView getEventLocation() {
            return eventLocation;
        }

        public Button getDeleteButton() {
            return deleteButton;
        }
    }

}
