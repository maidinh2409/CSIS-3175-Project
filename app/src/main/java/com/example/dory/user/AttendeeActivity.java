package com.example.dory.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.dory.InApp.ProfileActivity;
import com.example.dory.R;
import com.example.dory.events.AdvancedSearchFragment;
import com.example.dory.events.EventsFragment;
import com.example.dory.events.InvitationsFragment;
import com.example.dory.userDatabase.UserHashed;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AttendeeActivity extends AppCompatActivity {
    UserHashed currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        currUser = (UserHashed) getIntent().getSerializableExtra("currUser");

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

        getIntent().putExtra("currUser", currUser);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Handle navigation item clicks
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_invitations) {
                startFragment(new InvitationsFragment());
                return true;
            } else if (item.getItemId() == R.id.navigation_events) {
                startFragment(new EventsFragment());
                return true;
            } else if (item.getItemId() == R.id.navigation_search) {
                startFragment(new AdvancedSearchFragment());
                return true;
            }
            return false;
        });

        // load default fragment
        bottomNav.setSelectedItemId(R.id.navigation_invitations);
    }

    /**
     * starts a fragment in the fragment container
     * @param fragment the fragment to start
     */
    private void startFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}