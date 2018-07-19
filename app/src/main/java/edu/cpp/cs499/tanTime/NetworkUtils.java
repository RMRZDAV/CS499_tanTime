package edu.cpp.cs499.tanTime;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    //need to create function that gets location key by gps coordinates
    //GPS Coordinates need to be in format 33.75,-117.92
    private final static String TAG = "NetworkUtils";
    private final static String WEATHERDB_BASE_URL = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/327148";
            //Location key: 327148
            //API Key: mWpPwKbHLhcpKfsowf6rC74pXH5TT9MS
            //Latitude: 33.756188 | Longitude: -117.920827
    //http://dataservice.accuweather.com/forecasts/v1/daily/5day/327148

    private final static String API_KEY= "mWpPwKbHLhcpKfsowf6rC74pXH5TT9MS";

    //private final static String METRIC_VALUE = "false";

    //private final static String LANGUAGE_VALUE = "en-us";

    private final static String PARAM_API_KEY = "apikey";

    //private final static String PARAM_METRIC = "metric";

    //private final static String PARAM_LANGUAGE = "language";

    public static URL buildUrlForWeather(){
        Uri builtUri = Uri.parse(WEATHERDB_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY,API_KEY)
                //.appendQueryParameter(PARAM_METRIC, METRIC_VALUE)
                .build();
        URL url = null;
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "buildUrlForWeather: url:"+url);
        return url;
    }

    public static String getResponseFromHttpUrl (URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in  = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
