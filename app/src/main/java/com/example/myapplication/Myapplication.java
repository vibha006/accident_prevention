package com.example.myapplication;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Myapplication extends Application {
    private static Myapplication singleton;
    private List<Location> mylocations;

    public List<Location> getMylocations() {
        return mylocations;
    }

    public void setMylocations(List<Location> mylocations) {
        this.mylocations = mylocations;
    }

    public static Myapplication getInstance() {
        return singleton;
    }

    public void onCreate(){
        super.onCreate();
        singleton = this;
        mylocations = new ArrayList<>();
    }

}
