package com.application.iothings;

import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Switch ledSwitch; // Reference to the Switch in the layout
    private DatabaseReference ledStateRef; // Firebase database reference
    private boolean isSwitchBeingUpdated = false; // Flag to prevent feedback loop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ledStateRef = database.getReference("led/state");

        // Find the Switch by ID
        ledSwitch = findViewById(R.id.LEDSwitch);

        // Set listener for switch state changes
        ledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isSwitchBeingUpdated) { // Only update Firebase if not updating programmatically
                ledStateRef.setValue(isChecked ? 1 : 0)
                        .addOnSuccessListener(aVoid -> {
                            // Optional: Handle success
                        })
                        .addOnFailureListener(e -> {
                            // Optional: Handle failure
                        });
            }
        });

        // Listen for changes in Firebase to update the Switch state
        ledStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Safely get the state from Firebase
                Integer state = snapshot.getValue(Integer.class);
                if (state != null) {
                    isSwitchBeingUpdated = true; // Prevent triggering the listener
                    ledSwitch.setChecked(state == 1);
                    isSwitchBeingUpdated = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle Firebase errors (e.g., log or show a toast)
                System.err.println("Firebase error: " + error.getMessage());
            }
        });
    }
}
