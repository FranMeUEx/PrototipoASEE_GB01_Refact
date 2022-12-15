package es.unex.prototipoasee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import es.unex.prototipoasee.R;

public class AppInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        setTitle(R.string.app_info);
    }
}