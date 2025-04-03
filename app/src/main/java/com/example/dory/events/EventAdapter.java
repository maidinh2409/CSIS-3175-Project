package com.example.dory.events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dory.R;
import com.example.dory.userDatabase.Event;
import com.example.dory.userDatabase.UserDBHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the event list
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private List<Event> eventList;
    private List<Event> eventListFull;
    private UserDBHandler db;
    private boolean showButtons;
    private OnEventActionListener listener;

    /**
     * interface for event actions
     */
    public interface OnEventActionListener {
        void onOpen(Event event);

        void onDelete(Event event);
    }

    /**
     * constructor for the event adapter
     *
     * @param eventList   list of events to display
     * @param showButtons whether to show the delete button
     * @param listener    listener for event actions
     */
    public EventAdapter(List<Event> eventList, boolean showButtons, OnEventActionListener listener) {
        this.eventList = eventList;
        this.listener = listener;
        this.eventListFull = new ArrayList<>(eventList);
        this.showButtons = showButtons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.getEventName().setText(event.getTitle());
        holder.getEventDate().setText(event.getStartDate());
        holder.getEventLocation().setText(event.getLocation());
        if (!showButtons) {
            holder.getDeleteButton().setVisibility(View.GONE);
        } else {
            holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onDelete(event);
                    }
                }
            });
        }
        holder.getOpenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onOpen(event);
                }
            }
        });
    }

    /**
     * removes an event from the list and notifies the adapter
     *
     * @param event event to remove
     */
    public void removeEvent(Event event) {
        int position = eventList.indexOf(event);
        if (position != -1) {
            eventList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * filters the event list based on text input
     *
     * @return Filter object
     */
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

            /**
             * updates the event list with the filtered list
             * @param charSequence text input
             * @param filterResults filtered list
             */
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                eventList.clear();
                eventList.addAll((List<Event>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView eventName;
        private final MaterialTextView eventDate;
        private final MaterialTextView eventLocation;
        private final MaterialButton deleteButton;
        private final MaterialButton openButton;

        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.textViewEventName);
            eventDate = view.findViewById(R.id.textViewEventDate);
            eventLocation = view.findViewById(R.id.textViewEventLocation);
            deleteButton = view.findViewById(R.id.deleteEventBtn);
            openButton = view.findViewById(R.id.openBtn);
        }

        public MaterialTextView getEventName() {
            return eventName;
        }

        public MaterialTextView getEventDate() {
            return eventDate;
        }

        public MaterialTextView getEventLocation() {
            return eventLocation;
        }

        public MaterialButton getDeleteButton() {
            return deleteButton;
        }

        public MaterialButton getOpenButton() {
            return openButton;
        }
    }

}
