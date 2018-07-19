package edu.cpp.cs499.tanTime;

//import android.graphics.drawable.Drawable;

import android.Manifest;
import android.app.Notification;
import android.location.Location;
import android.os.AsyncTask;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;



public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Weather> weatherArrayList = new ArrayList<>();

    private double deviation;                       //Sun Relative Position

    public double lat;
    public double lon;

    private ImageView imageView;                    //compass
    private float[] mGravity = new float[3];        //compass
    private float[] mGeomagnetic = new float[3];    //compass
    private float azimuth = 0f;                     //compass
    private float currentAzimuth = 0f;              //compass
    private SensorManager mSensorManager;           //compass


    private static final long START_TIME_IN_MILLIS = 600000;    //timer

    private TextView mTextViewCountDown;            //timer
    private Button mButtonStart;                    //timer
    private Button mButtonReset;                    //timer

    private CountDownTimer mCountDownTimer;         //timer

    private boolean mTimerRunning;                  //timer

    private long mTimeLeftInMillis;                 //timer
    private long mEndTime;                          //timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();   //Hides ActionBar
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);

        SunRelativePosition s = new SunRelativePosition();
        deviation = s.getAzimuth();
        if(Double.isNaN(deviation)){
            deviation = 0.0;
        }
        //TextView textView = (TextView)findViewById(R.id.suncompass_text);
        //textView.setText(" "+1+azimuth);
        //Toast.makeText(null,"azimuth: "+ azimuth,Toast.LENGTH_LONG).show();


        GPStracker g = new GPStracker(getApplicationContext());
        Location l = g.getLocation();
        if(l != null){
            lat = l.getLatitude();
            lon = l.getLongitude();
            Toast.makeText(getApplicationContext(),"LAT: " + lat+ "\nLON: "+ lon,Toast.LENGTH_LONG).show();
        }
        //listView =findViewById(R.id.idListView);



  /*      FetchWeatherDetails fd = new FetchWeatherDetails();
        fd.onPreExecute();
        String weatherList = fd.doInBackground(weatherUrl);
        fd.onPostExecute(weatherList);*/



        imageView = (ImageView)findViewById(R.id.compass);                  //compass
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);   //compass

        mTextViewCountDown = findViewById(R.id.text_view_countdown);        //timer

        mButtonStart = findViewById(R.id.button_start);                     //timer
        mButtonReset = findViewById(R.id.button_reset);                     //timer

        URL weatherUrl = NetworkUtils.buildUrlForWeather();         //weather
        new FetchWeatherDetails().execute(weatherUrl);              //weather
        Log.i(TAG, "onCreate: weatherUrl: " + weatherUrl);      //weather

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimerRunning){
                    pauseTimer();
                } else{
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    private class FetchWeatherDetails extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL weatherUrl = urls[0];
            String weatherSearchResults = null;

            try {
                weatherSearchResults = NetworkUtils.getResponseFromHttpUrl(weatherUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "doInBackground: weatherSearchResults: " + weatherSearchResults);
            return weatherSearchResults;
        }

        @Override
        protected void onPostExecute(String weatherSearchResults) {
            if(weatherSearchResults != null && !weatherSearchResults.equals("")) {
                weatherArrayList = parseJSON(weatherSearchResults);
                //Just for testing
/*                Iterator itr = weatherArrayList.iterator();
                while(itr.hasNext()) {
                    Weather weatherInIterator = (Weather) itr.next();
                    Log.i(TAG, "onPostExecute: Date: " + weatherInIterator.getDate()+
                            " Min: " + weatherInIterator.getMinTemp() +
                            " Max: " + weatherInIterator.getMaxTemp() );
                }*/
            }
            super.onPostExecute(weatherSearchResults);
        }
    }

    private ArrayList<Weather> parseJSON(String weatherSearchResults) {
        if(weatherArrayList != null) {
            weatherArrayList.clear();
        }

        if(weatherSearchResults != null) {
            try {
                JSONObject rootObject = new JSONObject(weatherSearchResults);
                JSONArray results = rootObject.getJSONArray("DailyForecasts");

                for (int i = 0; i < results.length(); i++) {
                    Weather weather = new Weather();

                    JSONObject resultsObj = results.getJSONObject(i);

                    String date = resultsObj.getString("Date");
                    weather.setDate(date);

                    JSONObject sunObj =resultsObj.getJSONObject("Sun");
                    String sunState1 = sunObj.getString("Rise");
                    weather.setSunrise(sunState1);

                    String sunState2 = sunObj.getString("Set");
                    weather.setSunset(sunState2);

                    JSONObject temperatureObj = resultsObj.getJSONObject("Temperature");
                    String minTemperature = temperatureObj.getJSONObject("Minimum").getString("Value");
                    weather.setMinTemp(minTemperature);

                    String maxTemperature = temperatureObj.getJSONObject("Maximum").getString("Value");
                    weather.setMaxTemp(maxTemperature);

                    JSONObject dayObj =resultsObj.getJSONObject("Day");
                    String weatherIcon = dayObj.getString("Icon");
                    weather.setIcon(weatherIcon);

                    String iconPhrase = dayObj.getString("IconPhrase");
                    weather.setCondition(iconPhrase);

                    //String link = resultsObj.getString("Link");
                    //weather.setLink(link);

/*                    Log.i(TAG, "parseJSON: date: " + date + " " +
                            "Min: " + minTemperature + " " +
                            "Max: " + maxTemperature + " " +
                            "Rise: " + sunState1 + " " +
                            "Set: " + sunState2 + " " +
                            "Icon: " + dayState1 + " " +
                            "IconPhrase: " + dayState2);*/

                   //set text image
                    switch(i) {
                        case 0: //day 1
                            TextView day1TextView = findViewById(R.id.day1);                    //Day
                            day1TextView.setText("SUN");
                            TextView day1tempTextView = findViewById(R.id.day1temp);            //temperature
                            day1tempTextView.setText(minTemperature+"/"+maxTemperature);
                            ImageView day1iconImageView = findViewById(R.id.day1icon);          //icon
                            day1iconImageView.setImageDrawable(getDrawable(R.drawable.sunny));
                            TextView day1conditionTextView = findViewById(R.id.day1condition);  //condition
                            day1conditionTextView.setText(iconPhrase);
                            break;
                        case 1: //day 2
                            TextView day2TextView = findViewById(R.id.day2);                    //Day
                            day2TextView.setText("MON");
                            TextView day2tempTextView = findViewById(R.id.day2temp);            //temperature
                            day2tempTextView.setText(minTemperature+"/"+maxTemperature);
                            ImageView day2iconImageView = findViewById(R.id.day2icon);          //icon
                            day2iconImageView.setImageDrawable(getDrawable(R.drawable.sunny));
                            TextView day2conditionTextView = findViewById(R.id.day2condition);  //condition
                            day2conditionTextView.setText(iconPhrase);
                            break;
                        case 2: //day 3
                            TextView day3TextView = findViewById(R.id.day3);                    //Day
                            day3TextView.setText("TUE");
                            TextView day3tempTextView = findViewById(R.id.day3temp);            //temperature
                            day3tempTextView.setText(minTemperature+"/"+maxTemperature);
                            ImageView day3iconImageView = findViewById(R.id.day1icon);          //icon
                            day3iconImageView.setImageDrawable(getDrawable(R.drawable.sunny));
                            TextView day3conditionTextView = findViewById(R.id.day3condition);  //condition
                            day3conditionTextView.setText(iconPhrase);
                            break;
                        case 3: //day 4
                            TextView day4TextView = findViewById(R.id.day4);                    //Day
                            day4TextView.setText("WED");
                            TextView day4tempTextView = findViewById(R.id.day4temp);            //temperature
                            day4tempTextView.setText(minTemperature+"/"+maxTemperature);
                            ImageView day4iconImageView = findViewById(R.id.day4icon);          //icon
                            day4iconImageView.setImageDrawable(getDrawable(R.drawable.sunny));
                            TextView day4conditionTextView = findViewById(R.id.day4condition);  //condition
                            day4conditionTextView.setText(iconPhrase);
                            break;
                        //case default:
                        //    break;
                    }

                    weatherArrayList.add(weather);
                }

/*                if(weatherArrayList != null) {//set list view
                    WeatherAdapter weatherAdapter = new WeatherAdapter(this, weatherArrayList);
                    listView.setAdapter(weatherAdapter);
                }*/

                return weatherArrayList;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        final float alpha = 0.97f;
        synchronized (this){
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                mGravity[0] =alpha*mGravity[0]+(1-alpha)*sensorEvent.values[0];
                mGravity[1] =alpha*mGravity[1]+(1-alpha)*sensorEvent.values[1];
                mGravity[2] =alpha*mGravity[2]+(1-alpha)*sensorEvent.values[2];
            }
            if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                mGeomagnetic[0] =alpha*mGeomagnetic[0]+(1-alpha)*sensorEvent.values[0];
                mGeomagnetic[1] =alpha*mGeomagnetic[1]+(1-alpha)*sensorEvent.values[1];
                mGeomagnetic[2] =alpha*mGeomagnetic[2]+(1-alpha)*sensorEvent.values[2];
            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R,I,mGravity,mGeomagnetic);
            if(success)
            {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R,orientation);
                azimuth = ((float)Math.toDegrees(orientation[0])+(float)deviation)%360;
                azimuth = (azimuth+360)%360;
                Animation anim = new RotateAnimation(-currentAzimuth,-azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
                currentAzimuth = azimuth;

                anim.setDuration(500);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);

                imageView.startAnimation(anim);
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    private void startTimer(){
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateButtons();
//                Toast.makeText(null, "Time is up", Toast.LENGTH_LONG).show();
////                Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
////                v.vibrate(500);
            }
        }.start();

        mTimerRunning = true;
        updateButtons();
    }
    private void pauseTimer(){
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateButtons();
    }
    private void resetTimer(){
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        updateButtons();
    }
    private void updateCountDownText(){
        int minutes = (int) (mTimeLeftInMillis /1000)/60;
        int seconds = (int) (mTimeLeftInMillis /1000)%60;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }
    private void updateButtons(){
        if(mTimerRunning) {
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStart.setText("PAUSE");
        }else{
            mButtonStart.setText("START");
            if(mTimeLeftInMillis<1000){
                mButtonStart.setVisibility(View.INVISIBLE);
            }else{
                mButtonStart.setVisibility(View.VISIBLE);
            }
            if(mTimeLeftInMillis < START_TIME_IN_MILLIS){
                mButtonReset.setVisibility(View.VISIBLE);
            }else{
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }

    }
    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

//        mCountDownTimer.cancel();

    }
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning",false);

        updateCountDownText();
        updateButtons();

        if(mTimerRunning){
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if(mTimeLeftInMillis<0){
                mTimeLeftInMillis =0;
                mTimerRunning = false;
                updateCountDownText();
                updateButtons();
            }else{
                startTimer();
            }
        }
    }
}
