package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ShowSavedLocationList extends AppCompatActivity {

    ListView iv_savedpoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_location_list);
        iv_savedpoints = findViewById(R.id.iv_savedpoints);

        Myapplication myapplication = (Myapplication)getApplicationContext();
        List<Location> savedLocation = myapplication.getMylocations();
        iv_savedpoints.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, savedLocation));
    }
}