package com.example.dory.events;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dory.userDatabase.UserHashed;
import com.example.dory.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;


import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserHashed> users;
    private SparseBooleanArray selectedUsers = new SparseBooleanArray();

    public UserAdapter(List<UserHashed> userList) {
        this.users = userList;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        MaterialCheckBox checkBox;

        public UserViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.user_checkbox);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_layout, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserHashed user = users.get(position);
        holder.checkBox.setText(user.getName());

        // sets the checkbox to false if the user is not in the sparse array
        holder.checkBox.setChecked(selectedUsers.get(position, false));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedUsers.put(position, true);
            } else {
                selectedUsers.delete(position);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * Returns the list of selected users.
     */
    public List<UserHashed> getSelectedUsers() {
        List<UserHashed> selected = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            if (selectedUsers.get(i, false)) {
                selected.add(users.get(i));
            }
        }
        return selected;
    }
}
