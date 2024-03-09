package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.e(TAG, "MainActivity::OnCreate");
    }


    @Override
    protected void onStop() {
        // Call the superclass method first.
        super.onStop();
        Log.e(TAG, "MainActivity::OnStop");
    }

    @Override
    protected void onStart() {
        // Call the superclass method first.
        super.onStart();
        Log.e(TAG, "MainActivity::OnStart");
    }

}