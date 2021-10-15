package com.example.bluetoothkeyboard;


import android.app.Application;
import android.content.Intent;
import com.example.bluetoothkeyboard.YourService;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, YourService.class));
    }
}
