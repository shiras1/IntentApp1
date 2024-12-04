package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.weather.ApiClient;
import com.example.myapplication.weather.WeatherData;
import com.example.myapplication.weather.WeatherService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// This is the main activity class for the application.
// It extends AppCompatActivity and acts as the entry point for the app.
// It sets up the user interface and handles user interactions
// such as button clicks. In this case, it likely contains code
// to interact with UI components from activity_main.xml,
// processing user input, and possibly integrating with DBHelper to save and retrieve data.
public class MainActivity extends AppCompatActivity {

    public static User user = null;
    public static String userId = null;
    FirebaseAuth mAuth;
    private WeatherService.OpenWeatherMapService openWeatherMapService;
    private String apiKey = "d2c3e8ab3abfc00f1c05a80e15805c01";

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbUsers = db.getReference("users");
    ActivityResultLauncher<Intent> loginActivityLauncher;

    TextView tvBMI, tvTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        openWeatherMapService = ApiClient.getInstance().create(WeatherService.OpenWeatherMapService.class);

        tvBMI = findViewById(R.id.tvBMI);
        tvTemp = findViewById(R.id.tvTemp);

        loginActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    if (user == null) {
                        DatabaseReference userDb = dbUsers.child(mAuth.getCurrentUser().getUid());
                        userDb.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                user = dataSnapshot.getValue(User.class);
                                userId = mAuth.getCurrentUser().getUid();
                                Toast.makeText(MainActivity.this, "`Welcome` " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                tvBMI.setText("Your BMI: " + user.calculateBMI());
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Welcome " + user.getUsername(), Toast.LENGTH_SHORT).show();
                        tvBMI.setText("Your BMI: " + user.calculateBMI());
                    }

                });

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            loginActivityLauncher.launch(intent);
        } else {
            DatabaseReference userDb = dbUsers.child(mAuth.getCurrentUser().getUid());
            userDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    userId = mAuth.getCurrentUser().getUid();
                    Toast.makeText(MainActivity.this, "Welcome " + user.getUsername(), Toast.LENGTH_SHORT).show();
                    tvBMI.setText("Your BMI: " + user.calculateBMI());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault()); //it is Geocoder
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i=0; i<maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }

            String finalAddress = builder.toString(); //This is the complete address.
            System.out.println(finalAddress);
        } catch (IOException e) {}
        catch (NullPointerException e) {}
        loadWeatherData("London");
    }

    private void loadWeatherData(String location) {
        openWeatherMapService.getCurrentWeatherData(location, apiKey).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful()) {
                    WeatherData weatherData = response.body();
                    tvTemp.setText(weatherData.temperature + " C");
                } else {
                    showErrorToast();
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                showErrorToast();
            }
        });
    }

    private void showErrorToast() {
        Toast.makeText(MainActivity.this, "Failed to load weather data", Toast.LENGTH_SHORT).show();
    }

}


