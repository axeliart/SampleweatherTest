package com.example.ray.weathertest;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailView extends AppCompatActivity {

    TextView date;
    TextView tempHigh;
    TextView tempLow;
    TextView weatherStatus;
    ImageView weatherIcon;

    TextView humidity;
    TextView pressure;
    TextView speed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        date = (TextView) findViewById(R.id.date);
        tempHigh = (TextView) findViewById(R.id.todayTempHigh);
        tempLow = (TextView) findViewById(R.id.todayTempLow);
        weatherStatus = (TextView) findViewById(R.id.todayWeatherStatus);
        weatherIcon = (ImageView) findViewById(R.id.todayWeatherIcon);
        humidity = (TextView) findViewById(R.id.humidity);
        pressure = (TextView) findViewById(R.id.pressure);
        speed = (TextView) findViewById(R.id.speed);

        //from main activity
        Bundle bundle = getIntent().getExtras();
        String humidityValue = bundle.getString("humidity");
        String pressureValue = bundle.getString("pressure");
        String speedValue = bundle.getString("speed");
        String dtValue = bundle.getString("dt");
        String dateValue = bundle.getString("date");
        String minValue = bundle.getString("min");
        String maxValue = bundle.getString("max");

        //formatting the temperature values
        date.setText(dateValue+ "\n" + dateFormat(dtValue));
        tempHigh.setText(bundle.getString("max").substring(0,getIndexOfChar(maxValue))+ (char) 0x00B0);
        tempLow.setText(bundle.getString("min").substring(0,getIndexOfChar(minValue)) + (char) 0x00B0);
        weatherStatus.setText(bundle.getString("main"));
        weatherIcon.setImageResource(MainActivity.drawableMap.get(bundle.getString("icon")));
        humidity.setText(String.format(getString(R.string.humidity), humidityValue)+ " %");
        speed.setText(String.format(getString(R.string.speed), speedValue) + " mi/h");
        pressure.setText(String.format(getString(R.string.speed), pressureValue.substring(0, getIndexOfChar(pressureValue)))+ " hPa");

    }
    //pressure value index
    private int getIndexOfChar(String pressureValue) {
        return pressureValue.indexOf(".");
    }

    //Date formatting for this activity
    private String dateFormat(String dt){
        Long timeStamp = Integer.valueOf(dt) * 1000L;
        return new SimpleDateFormat("MMMM d", Locale.getDefault()).format(timeStamp);
    }
}
