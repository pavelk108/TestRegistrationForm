package com.example.pavel.testregistrationform;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RegActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        {
            ActionBar actionBar = getSupportActionBar();
            //Configure action bar
            // enable back button

            actionBar.setDisplayHomeAsUpEnabled(true);
            // set button image
            actionBar.setHomeAsUpIndicator(R.drawable.exit_button);
            //set title
            actionBar.setTitle(R.string.reg_title);
        }
    }
}
