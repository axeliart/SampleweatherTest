package com.example.ray.weathertest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    String urlFiveDay = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Atlanta,ga&units=imperial&cnt=5&appid=8e1af422c78d7f280383d6144f2de0d7";

    ProgressDialog progressDialog;
    ListView weatherList;
    ArrayList<HashMap<String, String>> weatherArrayList;
    HashMap<String, String> temperatureMap;
    public static HashMap<String, Integer> drawableMap, iconMap;
    TextView date;
    TextView tempHigh;
    TextView tempLow;
    TextView weatherStatus;
    ImageView weatherIcon;
    weatherListAdapter weatherAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date = (TextView) findViewById(R.id.date);
        tempHigh = (TextView) findViewById(R.id.todayTempHigh);
        tempLow = (TextView) findViewById(R.id.todayTempLow);
        weatherStatus = (TextView) findViewById(R.id.todayWeatherStatus);
        weatherIcon = (ImageView) findViewById(R.id.todayWeatherIcon);
        weatherList = (ListView) findViewById(R.id.weatherList);

        weatherArrayList = new ArrayList<>();
        temperatureMap = new HashMap<>();
        drawableMap = new HashMap<>();
        iconMap = new HashMap<>();

        drawableHashMapInit();
        iconHashMapInit();
        new weatherParseWeek().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    //Initializing the Hashmap Values
    private void drawableHashMapInit() {
        drawableMap.put("01d", R.drawable.art_clear);
        drawableMap.put("02d", R.drawable.art_light_clouds);
        drawableMap.put("03d", R.drawable.art_clouds);
        drawableMap.put("04d", R.drawable.art_clouds);
        drawableMap.put("09d", R.drawable.art_light_rain);
        drawableMap.put("10d", R.drawable.art_rain);
        drawableMap.put("11d", R.drawable.art_storm);
        drawableMap.put("13d", R.drawable.art_snow);
        drawableMap.put("50d", R.drawable.art_fog);
        drawableMap.put("01n", R.drawable.art_clear);
        drawableMap.put("02n", R.drawable.art_light_clouds);
        drawableMap.put("03n", R.drawable.art_clouds);
        drawableMap.put("04n", R.drawable.art_clouds);
        drawableMap.put("09n", R.drawable.art_light_rain);
        drawableMap.put("10n", R.drawable.art_rain);
        drawableMap.put("11n", R.drawable.art_storm);
        drawableMap.put("13n", R.drawable.art_snow);
        drawableMap.put("50n", R.drawable.art_fog);

    }

    private void iconHashMapInit() {
        iconMap.put("01d", R.drawable.ic_clear);
        iconMap.put("02d", R.drawable.ic_light_clouds);
        iconMap.put("03d", R.drawable.ic_cloudy);
        iconMap.put("04d", R.drawable.ic_cloudy);
        iconMap.put("09d", R.drawable.ic_light_rain);
        iconMap.put("10d", R.drawable.ic_rain);
        iconMap.put("11d", R.drawable.ic_storm);
        iconMap.put("13d", R.drawable.ic_snow);
        iconMap.put("50d", R.drawable.ic_fog);
        iconMap.put("01n", R.drawable.ic_clear);
        iconMap.put("02n", R.drawable.ic_light_clouds);
        iconMap.put("03n", R.drawable.ic_cloudy);
        iconMap.put("04n", R.drawable.ic_cloudy);
        iconMap.put("09n", R.drawable.ic_light_rain);
        iconMap.put("10n", R.drawable.ic_rain);
        iconMap.put("11n", R.drawable.ic_storm);
        iconMap.put("13n", R.drawable.ic_snow);
        iconMap.put("50n", R.drawable.ic_fog);
    }
    //weather parsing
    private class weatherParseWeek extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String jsonString = serviceCall(urlFiveDay);
            if (jsonString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray weatherList = jsonObject.getJSONArray("list");

                    //looping through and taking in all the required fields
                    for (int i = 0; i < weatherList.length(); i++) {
                        JSONObject day = weatherList.getJSONObject(i);
                        int dt = day.getInt("dt");

                        JSONObject temp = day.getJSONObject("temp");
                        String maxTemp = temp.getString("max");
                        String minTemp = temp.getString("min");
                        String dayTemp = temp.getString("day");

                        String pressure = day.getString("pressure");
                        String humidity = day.getString("humidity");
                        String speed = day.getString("speed");

                        JSONArray weather = day.getJSONArray("weather");
                        String main = "";
                        String icon = "";
                        for (int j = 0; j < weather.length(); j++) {
                            JSONObject weatherObject = weather.getJSONObject(j);
                            main = weatherObject.getString("main");
                            icon = weatherObject.getString("icon");
                        }

                        HashMap<String, String> weatherMap = new HashMap<>();
                        weatherMap.put("main", main);
                        weatherMap.put("icon", icon);
                        weatherMap.put("dt", String.valueOf(dt));
                        weatherMap.put("max", maxTemp);
                        weatherMap.put("min", minTemp);
                        weatherMap.put("day", dayTemp);
                        weatherMap.put("pressure", pressure);
                        weatherMap.put("humidity", humidity);
                        weatherMap.put("speed", speed);

                        weatherArrayList.add(weatherMap);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            //setting the values at the first index to signify today's values
            tempLow.setText(temperatureFormat(weatherArrayList.get(0).get("min")));
            tempHigh.setText(temperatureFormat(weatherArrayList.get(0).get("max")));
            weatherStatus.setText(weatherArrayList.get(0).get("main"));
            date.setText(dateFormat(weatherArrayList.get(0).get("dt")));
            weatherIcon.setImageResource(imageChoose(weatherArrayList.get(0).get("icon"), drawableMap));

            weatherAdapter = new weatherListAdapter(MainActivity.this);
            weatherList.setAdapter(weatherAdapter);
        }


    }

    //Date formatting for the initial page
    public String dateFormat(String date) {
        Long timeStamp = Integer.valueOf(date) * 1000L;
        String todayDate = new SimpleDateFormat("EEE d").format(timeStamp);
        return String.format(getResources().getString(R.string.today_date), todayDate);
    }

    //list Day formatting method
    public String listDay(String dt) {
        Calendar mCalender = Calendar.getInstance();
        mCalender.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = mCalender.getTime();
        String tomorrowDateFormatted = new SimpleDateFormat("MM/dd", Locale.getDefault()).format(tomorrow);

        Long timeStamp = Integer.valueOf(dt) * 1000L;
        String serviceDateFormatted = new SimpleDateFormat("MM/dd", Locale.getDefault()).format(timeStamp);

        if (Objects.equals(tomorrowDateFormatted, serviceDateFormatted)) {
            return getResources().getString(R.string.tomorrow);
        } else {
            return new SimpleDateFormat("EEEE", Locale.getDefault()).format(timeStamp);
        }
    }

    //temperature value formatting
    public String temperatureFormat(String temperature) {
        return temperature.substring(0, temperature.indexOf(".")) + (char) 0x00B0;
    }

    //hashmap value call to retrieve appropriate image
    public Integer imageChoose(String icon, HashMap hashMap) {
        return (Integer) hashMap.get(icon);
    }

    //service call method
    public String serviceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    //converting it into readable string
    private String convertStreamToString(InputStream mInputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    //adapter for List
    class weatherListAdapter extends BaseAdapter {

        private MainActivity mainActivity;
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        public weatherListAdapter(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public int getCount() {
            return mainActivity.weatherArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.weather_row, null);
            }
            final TextView weatherDate = (TextView) convertView.findViewById(R.id.dateText);
            TextView weatherMain = (TextView) convertView.findViewById(R.id.weatherMain);
            TextView weatherMax = (TextView) convertView.findViewById(R.id.weatherMax);
            TextView weatherMin = (TextView) convertView.findViewById(R.id.weatherMin);
            ImageView weatherIcon = (ImageView) convertView.findViewById(R.id.weatherIcon);

            HashMap<String, String> index = mainActivity.weatherArrayList.get(position);
            for (int i = 0; i < mainActivity.weatherArrayList.size(); i++) {

                weatherDate.setText(listDay(index.get("dt")));
                weatherMax.setText(temperatureFormat(index.get("max")));
                weatherMin.setText(temperatureFormat(index.get("min")));
                weatherMain.setText(index.get("main"));
                weatherIcon.setImageResource(mainActivity.imageChoose(index.get("icon"), iconMap));
            }

            //detailed information for row click
            mainActivity.weatherList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> index = mainActivity.weatherArrayList.get(position);
                    Intent detailIntent = new Intent(mainActivity, DetailView.class);
                    detailIntent.putExtra("humidity", index.get("humidity"));
                    detailIntent.putExtra("speed", index.get("speed"));
                    detailIntent.putExtra("pressure", index.get("pressure"));
                    detailIntent.putExtra("dt", index.get("dt"));
                    detailIntent.putExtra("max", index.get("max"));
                    detailIntent.putExtra("min", index.get("min"));
                    detailIntent.putExtra("icon", index.get("icon"));
                    detailIntent.putExtra("main", index.get("main"));
                    detailIntent.putExtra("date", weatherDate.getText());
                    mainActivity.startActivity(detailIntent);
                }
            });
            return convertView;
        }
    }
}
