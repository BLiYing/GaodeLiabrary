package com.tepia.bliying.gaodelibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.example.gaodelibrary.GaodeEntity;
import com.example.gaodelibrary.UtilsContextOfGaode;

public class MainActivity extends AppCompatActivity {
    private GaodeEntity gaodeEntity;
    private AMapLocation aMapLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startbutton = findViewById(R.id.startBtn);
        Button endbutton = findViewById(R.id.endBtn);
        /*UtilsContextOfGaode.init(this);
        gaodeEntity = new GaodeEntity(this,MainActivity.class,R.mipmap.ic_launcher_round);

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gaodeEntity.startTrace();

            }
        });
        endbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gaodeEntity.stopTrace();

            }
        });*/
    }
}
