package com.example.intentapp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        EditText shareEditText = findViewById(R.id.shareEditText);
        Button shareButton = findViewById(R.id.shareButton);

        shareButton.setOnClickListener(v -> {
            String textToShare = shareEditText.getText().toString();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Share text via"));
        });
    }
}
