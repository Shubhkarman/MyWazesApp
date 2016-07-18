package com.example.abhishek.mapp;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


/**
 * Created by Abhishek on 4/18/2016.
 */
public class Report extends FragmentActivity {
    ImageButton btnroadblock, btnaccident, btnconstruction, btntraffic;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_1);



        btnroadblock = (ImageButton) findViewById(R.id.imageButton);
        btnroadblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Report.this,
                        AMapsActivity2.class);
                startActivity(intent);
            }
        });
        btnconstruction = (ImageButton) findViewById(R.id.imageButton3);
        btnconstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Report.this,
                        Construction.class);
                startActivity(intent);
            }
        });
        btntraffic = (ImageButton) findViewById(R.id.imageButton4);
        btntraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Report.this,
                        Traffic.class);
                startActivity(intent);
            }
        });
        btnaccident = (ImageButton) findViewById(R.id.imageButton2);
        btnaccident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Report.this,
                        Accident.class);
                startActivity(intent);
            }
        });
    }
}

