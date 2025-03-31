package com.example.dory.user;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.dory.R;
import com.example.dory.events.InvitationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AttendeeActivity extends AppCompatActivity {


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

        Log.d("attendeeActivity", "onCreate: ");

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Handle navigation item clicks
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_invitations) {
                loadFragment(new InvitationsFragment());
                return true;
            }
//                else if (item.getItemId() == R.id.navigation_events) {
//                    loadFragment(new EventsFragment());
//                    return true;
//                }
            else if (item.getItemId() == R.id.navigation_search) {
                // TODO: Replace with SearchFragment or activity
            } else if (item.getItemId() == R.id.navigation_profile) {
                // TODO: Replace with ProfileFragment
            }
            return false;
        });

        // Load default fragment
        bottomNav.setSelectedItemId(R.id.navigation_invitations);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}