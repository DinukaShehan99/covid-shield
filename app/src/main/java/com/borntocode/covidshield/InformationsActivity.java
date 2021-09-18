package com.borntocode.covidshield;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class InformationsActivity extends AppCompatActivity {

    ConstraintLayout btn_info01, btn_info02, btn_info03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);

        btn_info01 = findViewById(R.id.btn_visitWebsite01);
        btn_info02 = findViewById(R.id.btn_visitWebsite02);
        btn_info03 = findViewById(R.id.btn_visitWebsite03);

        btn_info01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.presidentsoffice.gov.lk/index.php/covid-19-dashboard/")));
            }
        });

        btn_info02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.epid.gov.lk/web/index.php?option=com_content&view=article&id=225&Itemid=518&lang=en")));
            }
        });

        btn_info03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://hpb.health.gov.lk/covid19-dashboard/")));
            }
        });
    }
}