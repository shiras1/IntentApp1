package com.example.firebaseexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.*;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "firebaseexample";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Write a message to the database
        Log.d(TAG, "onCreate");
        FirebaseDatabase database =
                FirebaseDatabase.getInstance("https://shirarotberg2024-default-rtdb.europe-west1.firebasedatabase.app/");
        Log.d(TAG, database.toString());
        DatabaseReference myRef = database.getReference("message");

       // myRef.setValue("Hello, World!");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


}